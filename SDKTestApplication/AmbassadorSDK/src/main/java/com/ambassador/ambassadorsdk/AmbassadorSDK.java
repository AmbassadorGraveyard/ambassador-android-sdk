package com.ambassador.ambassadorsdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ambassador.ambassadorsdk.internal.AmbassadorActivity;
import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.IIdentify;
import com.ambassador.ambassadorsdk.internal.IdentifyAugurSDK;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.PusherSDK;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.broadcasts.RegistrationIntentService;
import com.ambassador.ambassadorsdk.internal.factories.RAFOptionsFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public final class AmbassadorSDK {

    @Inject
    static AmbassadorConfig ambassadorConfig;

    @Inject
    static PusherSDK pusherSDK;

    public static void presentRAF(Context context, String campaignID) {
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
        Intent intent = buildIntent(context, AmbassadorActivity.class);
        ambassadorConfig.setCampaignID(campaignID);
        context.startActivity(intent);
    }

    private static Intent buildIntent(Context context, Class target) {
        return new Intent(context, target);
    }

    public static void identify(String emailAddress) {
        ambassadorConfig.setUserEmail(emailAddress);

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
    }

    static void registerInstallReceiver(Context context) {
        IntentFilter intentFilter = buildIntentFilter();
        intentFilter.addAction("com.android.vending.INSTALL_REFERRER");
        context.registerReceiver(InstallReceiver.getInstance(), intentFilter);
    }

    private static IntentFilter buildIntentFilter() {
        return new IntentFilter();
    }

    static void startConversionTimer() {
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

    private static void setupGcm(Context context) {
        if (checkPlayServices(context)) {
            Intent intent = new Intent(context, RegistrationIntentService.class);
            context.startService(intent);

            LocalBroadcastManager.getInstance(context).registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                }
            }, new IntentFilter("registrationComplete"));
        }
    }

    private static boolean checkPlayServices(Context context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }

}
