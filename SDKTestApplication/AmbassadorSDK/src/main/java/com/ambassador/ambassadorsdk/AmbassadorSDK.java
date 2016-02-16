package com.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.IIdentify;
import com.ambassador.ambassadorsdk.internal.IdentifyAugurSDK;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.PusherSDK;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.activities.AmbassadorActivity;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
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

    @Inject
    protected static AmbassadorConfig ambassadorConfig;

    @Inject
    protected static PusherSDK pusherSDK;

    @Inject
    static RequestManager requestManager;

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
        ambassadorConfig.resetForNewCampaign();
        Intent intent = buildIntent(context, AmbassadorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ambassadorConfig.setCampaignID(campaignID);
        context.startActivity(intent);
    }

    private static Intent buildIntent(Context context, Class target) {
        return new Intent(context, target);
    }

    public static void identify(String emailAddress) {
        ambassadorConfig.setUserEmail(emailAddress);
        if (ambassadorConfig.getGcmRegistrationToken() != null) {
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
        if (!restrictToInstall || !getConvertedOnInstall()) {
            Utilities.debugLog("Conversion", "restrictToInstall: " + restrictToInstall);

            ConversionUtility conversionUtility = buildConversionUtility(conversionParameters);
            conversionUtility.registerConversion();
        }

        if (restrictToInstall) setConvertedOnInstall();
    }

    private static ConversionUtility buildConversionUtility(ConversionParameters conversionParameters) {
        return new ConversionUtility(AmbassadorSingleton.getInstanceContext(), conversionParameters);
    }

    private static Boolean getConvertedOnInstall() {
        return ambassadorConfig.getConvertedOnInstall();
    }

    private static void setConvertedOnInstall() {
        if (!ambassadorConfig.getConvertedOnInstall()) {
            ambassadorConfig.setConvertOnInstall();
        }
    }

    public static void runWithKeys(Context context, String universalToken, String universalID) {
        AmbassadorSingleton.init(context);
        AmbassadorSingleton.getInstanceComponent().inject(new AmbassadorSDK());

        registerInstallReceiver(context);
        setupGcm(context);

        ambassadorConfig.setUniversalToken(universalToken);
        ambassadorConfig.setUniversalID(universalID);
        startConversionTimer();

        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                if (ex instanceof Exception && AmbassadorConfig.isReleaseBuild) {
                    Exception exception = (Exception) ex;
                    for (StackTraceElement element : exception.getStackTrace()) {
                        element.getClassName();
                        if (element.getClassName().contains("com.ambassador.ambassadorsdk")) {
                            DefaultRavenFactory.ravenInstance("https://627333068b7242a1a2c0f036900211cc:c0c5651dd83b4b48bf0fad28e2757e32@app.getsentry.com/66262")
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
                ambassadorConfig.setGcmRegistrationToken(token);
                if (ambassadorConfig.getUserEmail() != null) {
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
        requestManager.updateGcmRegistrationToken(ambassadorConfig.getUserEmail(), ambassadorConfig.getGcmRegistrationToken(), new RequestManager.RequestCompletion() {
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
