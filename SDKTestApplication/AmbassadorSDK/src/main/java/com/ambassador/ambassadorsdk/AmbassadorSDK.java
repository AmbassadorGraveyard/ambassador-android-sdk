package com.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ambassador.ambassadorsdk.conversions.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.AmbassadorActivity;
import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.conversions.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.IIdentify;
import com.ambassador.ambassadorsdk.internal.IdentifyAugurSDK;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.PusherSDK;
import com.ambassador.ambassadorsdk.internal.Utilities;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

/**
 * Created by JakeDunahee on 7/22/15.
 */
public class AmbassadorSDK {

    @Inject
    AmbassadorConfig ambassadorConfig;

    public AmbassadorSDK() {
        AmbassadorSingleton.getComponent().inject(this);
    }


    // Static Functions
    public static void presentRAF(Context context, String campaignID) {
        AmbassadorSDK ambassadorSDK = new AmbassadorSDK();
        ambassadorSDK.localPresentRAF(context, campaignID);
    }

    public static void identify(String emailAddress) {
        AmbassadorSDK ambassadorSDK = new AmbassadorSDK();
        ambassadorSDK.localIdentify(emailAddress);
    }

    public static void registerConversion(ConversionParameters conversionParameters, Boolean restrictToInstall) {
        AmbassadorSDK ambassadorSDK = new AmbassadorSDK();

        //do conversion if it's not an install conversion, or if it is, make sure that we haven't already converted on install by checking sharedprefs
        if (!restrictToInstall || !ambassadorSDK.getConvertedOnInstall()) {
            Utilities.debugLog("Conversion", "restrictToInstall: " + restrictToInstall);

            ConversionUtility conversionUtility = new ConversionUtility(AmbassadorSingleton.get(), conversionParameters);

            conversionUtility.registerConversion();
        }

        if (restrictToInstall) ambassadorSDK.setConvertedOnInstall();
    }

    public static void runWithKeys(Context context, String universalToken, String universalID) {
        AmbassadorSingleton.getInstance().init(context);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.vending.INSTALL_REFERRER");
        context.registerReceiver(InstallReceiver.getInstance(), intentFilter);

        AmbassadorSDK ambassadorSDK = new AmbassadorSDK();
        ambassadorSDK.localRunWithKeys(universalToken, universalID);
    }

    // Package-private local functions
    void localPresentRAF(Context context, String campaignID) {
        Intent intent = new Intent(context, AmbassadorActivity.class);
        ambassadorConfig.setCampaignID(campaignID);
        context.startActivity(intent);
    }

    void localIdentify(String identifier) {
        ambassadorConfig.setUserEmail(identifier);

        IIdentify identify = new IdentifyAugurSDK();
        identify.getIdentity();

        PusherSDK pusher = new PusherSDK();
        pusher.createPusher(new PusherSDK.PusherSubscribeCallback() {
            @Override
            public void pusherSubscribed() {

            }

            @Override
            public void pusherFailed() {

            }
        });
    }

    void localRunWithKeys(String universalToken, String universalID) {
        ambassadorConfig.setUniversalToken(universalToken);
        ambassadorConfig.setUniversalID(universalID);
        startConversionTimer();
    }

    Boolean getConvertedOnInstall() {
        return ambassadorConfig.getConvertedOnInstall();
    }

    void setConvertedOnInstall() {
        if (!ambassadorConfig.getConvertedOnInstall()) {
            ambassadorConfig.setConvertOnInstall();
        }
    }

    void startConversionTimer() {
        final ConversionUtility utility = new ConversionUtility(AmbassadorSingleton.get());
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                utility.readAndSaveDatabaseEntries();
            }
        }, 10000, 10000);
    }
}
