package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

/**
 * Created by JakeDunahee on 7/22/15.
 */
public class AmbassadorSDK {

    @Inject
    AmbassadorSingleton ambassadorSingleton;

    public AmbassadorSDK() {
        ApplicationContext.getComponent().inject(this);
    }


    // Static Functions
    public static void presentRAF(Context context, String campaingID) {
        AmbassadorSDK ambassadorSDK = new AmbassadorSDK();
        ambassadorSDK.localPresentRAF(context, campaingID);
    }

    public static void identify(String emailAddress) {
        AmbassadorSDK ambassadorSDK = new AmbassadorSDK();
        ambassadorSDK.localIdentify(emailAddress);
    }

    public static void registerConversion(ConversionParameters conversionParameters) {
        AmbassadorSDK ambassadorSDK = new AmbassadorSDK();
        ambassadorSDK.localRegisterConversion(conversionParameters);
    }

    public static void runWithKeys(Context context, String universalToken, String universalID) {
        ApplicationContext.getInstance().init(context);

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
        ambassadorSingleton.setCampaignID(campaignID);
        context.startActivity(intent);
    }

    void localIdentify(String identifier) {
        // Functionality: Gets unique information from the device for tracking purposes
        Context context = ApplicationContext.get();
        ambassadorSingleton.setUserEmail(identifier);

        IIdentify identify = new Identify(context, identifier);
        ambassadorSingleton.startIdentify(identify);
    }

    void localRegisterConversion(ConversionParameters parameters) {
        // Functionality: Registers a conversion set by the dev
        ambassadorSingleton.registerConversion(parameters);
    }

    void localRunWithKeys(String universalToken, String universalID) {
        // Functionality: Basically initializes the AmbassadorSDK
        ambassadorSingleton.saveUniversalToken(universalToken);
        ambassadorSingleton.saveUniversalID(universalID);
        ambassadorSingleton.startConversionTimer();
    }

    void localRunWithKeysAndConvertOnInstall(String universalToken, String universalID, ConversionParameters parameters) {
        // Functionality: Initializes SDK and converts for the first time running
        ambassadorSingleton.saveUniversalToken(universalToken);
        ambassadorSingleton.saveUniversalID(universalID);
        ambassadorSingleton.startConversionTimer();

        // Checks boolean from sharedpreferences to see if this the first launch and registers conversion if it is
        if (!ambassadorSingleton.convertedOnInstall()) {
            ambassadorSingleton.convertForInstallation(parameters);
        }
    }
}
