package com.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ambassador.ambassadorsdk.internal.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.AmbassadorActivity;
import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.IIdentify;
import com.ambassador.ambassadorsdk.internal.IdentifyAugurSDK;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.PusherSDK;
import com.ambassador.ambassadorsdk.internal.Utilities;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class AmbassadorSDK {

    @Inject
    static AmbassadorConfig ambassadorConfig;

    public static void presentRAF(Context context, String campaignID) {
        Intent intent = buildIntent(context, AmbassadorActivity.class);
        ambassadorConfig.setCampaignID(campaignID);
        context.startActivity(intent);
    }

    static Intent buildIntent(Context context, Class target) {
        return new Intent(context, target);
    }

    public static void identify(String emailAddress) {
        ambassadorConfig.setUserEmail(emailAddress);

        IIdentify identify = buildIdentify();
        identify.getIdentity();

        PusherSDK pusher = buildPusherSDK();
        pusher.createPusher(null);
    }

    static IIdentify buildIdentify() {
        return new IdentifyAugurSDK();
    }

    static PusherSDK buildPusherSDK() {
        return new PusherSDK();
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

    static ConversionUtility buildConversionUtility(ConversionParameters conversionParameters) {
        return new ConversionUtility(AmbassadorSingleton.getInstanceContext(), conversionParameters);
    }

    static Boolean getConvertedOnInstall() {
        return ambassadorConfig.getConvertedOnInstall();
    }

    static void setConvertedOnInstall() {
        if (!ambassadorConfig.getConvertedOnInstall()) {
            ambassadorConfig.setConvertOnInstall();
        }
    }

    public static void runWithKeys(Context context, String universalToken, String universalID) {
        AmbassadorSingleton.getInstance().init(context);
        AmbassadorSingleton.getInstanceComponent().inject(new AmbassadorSDK());

        registerInstallReceiver(context);

        ambassadorConfig.setUniversalToken(universalToken);
        ambassadorConfig.setUniversalID(universalID);
        startConversionTimer();
    }

    static void startConversionTimer() {
        final ConversionUtility utility = new ConversionUtility(AmbassadorSingleton.getInstanceContext());
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                utility.readAndSaveDatabaseEntries();
            }
        }, 10000, 10000);
    }

    static void registerInstallReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.vending.INSTALL_REFERRER");
        context.registerReceiver(InstallReceiver.getInstance(), intentFilter);
    }

}
