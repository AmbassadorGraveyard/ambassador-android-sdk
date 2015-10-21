package com.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;

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

    public static void registerConversion(ConversionParameters conversionParameters) {
        ConversionUtility conversionUtility = new ConversionUtility(AmbassadorSingleton.get(), conversionParameters);
        conversionUtility.registerConversion();
    }

    public static void runWithKeys(Context context, String universalToken, String universalID) {
        AmbassadorSingleton.getInstance().init(context);

        AmbassadorSDK ambassadorSDK = new AmbassadorSDK();
        ambassadorSDK.localRunWithKeys(universalToken, universalID);
    }

    public static void runWithKeysAndConvertOnInstall(String universalToken, String universalID, ConversionParameters parameters) {
        AmbassadorSDK ambassadorSDK = new AmbassadorSDK();
        ambassadorSDK.localRunWithKeysAndConvertOnInstall(universalToken, universalID, parameters);
    }


    // Package-private local functions
    void localPresentRAF(Context context, String campaignID) {
        // Functionality: Present the RAF Screen using an intent from the passed class
        Intent intent = new Intent(context, AmbassadorActivity.class);
        ambassadorConfig.setCampaignID(campaignID);
        context.startActivity(intent);
    }

    void localIdentify(String identifier) {
        // Functionality: Gets unique information from the device for tracking purposes
        Context context = AmbassadorSingleton.get();
        ambassadorConfig.setUserEmail(identifier);

        IIdentify identify = new Identify();
        identify.getIdentity();

        PusherSDK pusher = new PusherSDK(context);
        pusher.createPusher(new PusherSDK.PusherSubscribeCallback() {
            @Override
            public void pusherSubscribed() {
            }
        });
    }

    void localRunWithKeys(String universalToken, String universalID) {
        // Functionality: Basically initializes the AmbassadorSDK
        ambassadorConfig.setUniversalToken(universalToken);
        ambassadorConfig.setUniversalID(universalID);
        startConversionTimer();
    }

    void localRunWithKeysAndConvertOnInstall(String universalToken, String universalID, ConversionParameters parameters) {
        // Functionality: Initializes SDK and converts for the first time running
        ambassadorConfig.setUniversalToken(universalToken);
        ambassadorConfig.setUniversalID(universalID);
        startConversionTimer();

        // Checks boolean from sharedpreferences to see if this the first launch and registers conversion if it is
        if (!ambassadorConfig.convertedOnInstall()) {
            registerConversion(parameters);
            ambassadorConfig.setConvertForInstall();
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
        }, 0, 10000);
    }
}
