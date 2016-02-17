package com.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.IIdentify;
import com.ambassador.ambassadorsdk.internal.IdentifyAugurSDK;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.PusherSDK;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.activities.AmbassadorActivity;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.factories.RAFOptionsFactory;
import com.ambassador.ambassadorsdk.internal.notifications.GcmHandler;

import net.kencochrane.raven.DefaultRavenFactory;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

/**
 * Static methods called by the end-developer to utilize the SDK.
 */
public final class AmbassadorSDK {

    @Inject protected static Auth auth;
    @Inject protected static User user;
    @Inject protected static Campaign campaign;

    @Inject protected static PusherSDK pusherSDK;
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

    public static void identify(String emailAddress) {
        user.clear();
        user.setEmail(emailAddress);

        if (user.getGcmToken() != null) {
            updateGcm();
        }

        IIdentify identify = buildIdentify();
        identify.getIdentity();

        pusherSDK.createPusher(null);
    }

    private static IIdentify buildIdentify() {
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
        return new ConversionUtility(AmbassadorSingleton.getInstanceContext(), conversionParameters);
    }

    public static void runWithKeys(Context context, String universalToken, String universalId) {
        AmbassadorSingleton.init(context);
        AmbassadorSingleton.getInstanceComponent().inject(new AmbassadorSDK());

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
        final ConversionUtility utility = buildConversionUtility(AmbassadorSingleton.getInstanceContext());
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

}
