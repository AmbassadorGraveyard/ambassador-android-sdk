package com.ambassador.ambassadorsdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.IdentifyAugurSDK;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.activities.AmbassadorActivity;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.factories.RAFOptionsFactory;
import com.ambassador.ambassadorsdk.internal.notifications.GcmHandler;
import com.ambassador.ambassadorsdk.internal.utils.Identify;

import net.kencochrane.raven.DefaultRavenFactory;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import dagger.ObjectGraph;

/**
 * This is the main class of the Ambassador SDK. Contains public static methods for the 3rd party
 * developer to directly access and use. All public methods rely on runWithKeys to first be called
 * with a valid Context.
 */
public final class AmbassadorSDK {

    @Inject protected static Auth auth;
    @Inject protected static User user;
    @Inject protected static Campaign campaign;
    @Inject protected static PusherManager pusherManager;
    @Inject protected static RequestManager requestManager;

    public static void presentRAF(Context context, String campaignID) {
        if (context.getResources().getIdentifier("homeWelcomeTitle", "color", context.getPackageName()) != 0) {
            try {
                presentRAF(context, campaignID, RAFOptionsFactory.decodeCustomValues(context));
                return;
            } catch (Exception e) {
                // catch all to go to defaults
            }
        }

        presentRAF(context, campaignID, new RAFOptions.Builder().build());
    }

    public static void presentRAF(Context context, String campaignID, InputStream inputStream) {
        RAFOptions rafOptions;
        try {
            rafOptions = RAFOptionsFactory.decodeResources(inputStream, context);
        } catch (Exception e) {
            rafOptions = new RAFOptions.Builder().build();
        }

        presentRAF(context, campaignID, rafOptions);
    }

    public static void presentRAF(Context context, String campaignID, RAFOptions rafOptions) {
        RAFOptions.set(rafOptions);
        intentAmbassadorActivity(context, campaignID);
    }
 
    public static void presentRAF(Context context, String campaignID, String pathInAssets) {
        try {
            presentRAF(context, campaignID, context.getAssets().open(pathInAssets));
        } catch (Exception e) {
            presentRAF(context, campaignID);
            Log.e("AmbassadorSDK", pathInAssets + " not found.");
        }
    }

    private static void intentAmbassadorActivity(Context context, String campaignID) {
        campaign.clear();
        campaign.setId(campaignID);
        Intent intent = buildIntent(context, AmbassadorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static Intent buildIntent(Context context, Class target) {
        return new Intent(context, target);
    }

    /**
     * Sets an email address to associate with this user. Needed to properly handle referrals and
     * conversions.
     * @param emailAddress the unique identifier to associate a user in the Ambassador backend.
     * @return true if successful identify, false otherwise; only considers client side validation.
     */
    public static boolean identify(String emailAddress) {
        if (!new Identify(emailAddress).isValidEmail()) {
            return false;
        }
        String gcmToken = user.getGcmToken();
        user.clear();
        user.setEmail(emailAddress);
        if (gcmToken != null) {
            user.setGcmToken(gcmToken);
            updateGcm();
        }
        buildIdentify().getIdentity();
        pusherManager.startNewChannel();
        return true;
    }

    protected static IdentifyAugurSDK buildIdentify() {
        return new IdentifyAugurSDK();
    }

    public static void registerConversion(ConversionParameters conversionParameters, Boolean restrictToInstall) {
        //do conversion if it's not an install conversion, or if it is, make sure that we haven't already converted on install by checking sharedprefs
        if (!restrictToInstall || !campaign.isConvertedOnInstall()) {
            Utilities.debugLog("Conversion", "restrictToInstall: " + restrictToInstall);

            ConversionUtility conversionUtility = buildConversionUtility(conversionParameters);
            conversionUtility.registerConversion();
        }

        if (restrictToInstall) {
            campaign.setConvertedOnInstall(true);
        }
    }

    private static ConversionUtility buildConversionUtility(ConversionParameters conversionParameters) {
        return new ConversionUtility(AmbSingleton.getContext(), conversionParameters);
    }

    public static void runWithKeys(Context context, String universalToken, String universalId) {
        AmbSingleton.init(context);

        ObjectGraph objectGraph = AmbSingleton.getGraph();
        if (objectGraph == null) {
            return;
        }
        objectGraph.injectStatics();

        auth.clear();

        registerInstallReceiver(context);
        setupGcm(context);

        auth.setUniversalToken(universalToken);
        auth.setUniversalId(universalId);
        startConversionTimer();

        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                if (ex instanceof Exception && BuildConfig.IS_RELEASE_BUILD) {
                    Exception exception = (Exception) ex;
                    for (StackTraceElement element : exception.getStackTrace()) {
                        element.getClassName();
                        if (element.getClassName().contains("com.ambassador.ambassadorsdk")) {
                            DefaultRavenFactory.ravenInstance("***REMOVED***")
                                    .sendException((Exception) ex);
                            Log.v("amb", "Sending exception to Sentry:");
                            Log.v("amb", ex.toString());
                            break;
                        }
                    }
                }

                defaultHandler.uncaughtException(thread, ex);
            }
        });
    }

    protected static void registerInstallReceiver(Context context) {
        IntentFilter intentFilter = buildIntentFilter();
        intentFilter.addAction("com.android.vending.INSTALL_REFERRER");
        context.registerReceiver(InstallReceiver.getInstance(), intentFilter);
    }

    private static IntentFilter buildIntentFilter() {
        return new IntentFilter();
    }

    protected static void startConversionTimer() {
        final ConversionUtility utility = buildConversionUtility(AmbSingleton.getContext());
        Timer timer = buildTimer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                utility.readAndSaveDatabaseEntries();
            }
        }, 10000, 10000);
    }

    private static ConversionUtility buildConversionUtility(Context context) {
        return new ConversionUtility(context);
    }

    private static Timer buildTimer() {
        return new Timer();
    }

    protected static void setupGcm(final Context context) {
        new GcmHandler(context).getRegistrationToken(new GcmHandler.RegistrationListener() {
            @Override
            public void registrationSuccess(final String token) {
                Log.v("AMB_GCM", token);
                user.setGcmToken(token);
                if (user.getEmail() != null) {
                    updateGcm();
                }
            }

            @Override
            public void registrationFailure(Throwable e) {
                Log.e("AmbassadorSDK", e.toString());
            }
        });
    }

    protected static void updateGcm() {
        requestManager.updateGcmRegistrationToken(user.getEmail(), user.getGcmToken(), new RequestManager.RequestCompletion() {
            @Override
            public void onSuccess(Object successResponse) {
                // No reaction currently required
            }

            @Override
            public void onFailure(Object failureResponse) {
                // No reaction currently required
            }
        });
    }

    /**
     * Registers an activity and callback to pass a WelcomeScreenDialog through, once the InstallReceiver
     * is used.
     * @param activity the Activity to launch the dialog from.
     * @param availabilityCallback the callback interface to pass the dialog through, once available.
     */
    public static void presentWelcomeScreen(
            @NonNull final Activity activity,
            @NonNull final WelcomeScreenDialog.AvailabilityCallback availabilityCallback,
            @NonNull final WelcomeScreenDialog.Parameters parameters) {

        WelcomeScreenDialog.setActivity(activity);
        WelcomeScreenDialog.setAvailabilityCallback(availabilityCallback);
        WelcomeScreenDialog.setParameters(parameters);
    }

}
