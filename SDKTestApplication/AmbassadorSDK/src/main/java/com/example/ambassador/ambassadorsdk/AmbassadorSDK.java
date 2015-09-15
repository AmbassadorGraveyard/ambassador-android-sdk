package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;

/**
 * Created by JakeDunahee on 7/22/15.
 */
public class AmbassadorSDK {
    public static void presentRAF(Context context, ServiceSelectorPreferences rafParameters, String campaignID) {
        // Functionality: Present the RAF Screen using an intent from the passed class
        // Users can set up custom text with RAFParameters
        Intent intent = new Intent(context, AmbassadorActivity.class);

        if (rafParameters == null) { rafParameters = new ServiceSelectorPreferences(); }

        AmbassadorSingleton.getInstance().setCampaignID(campaignID);

        intent.putExtra("rafParameters", rafParameters);
        context.startActivity(intent);
    }

    public static void identify(String identifier) {
        // Functionality: Gets unique information from the device for tracking purposes
        Context context = MyApplication.getAppContext();

        IIdentify identify = new Identify(context, identifier);
        AmbassadorSingleton.getInstance().startIdentify(identify);
    }

    public static void registerConversion(ConversionParameters parameters) {
        // Functionality: Registers a conversion set by the dev
        AmbassadorSingleton.getInstance().registerConversion(parameters);
    }

    public static void runWithKey(String apiKey) {
        // Functionality: Basically initializes the AmbassadorSDK
        AmbassadorSingleton.getInstance().saveAPIKey(apiKey);
        AmbassadorSingleton.getInstance().startConversionTimer();
    }

    public static void runWithKeyAndConvertOnInstall(String apiKey, ConversionParameters parameters) {
        // Functionality: Initializes SDK and converts for the first time running
        AmbassadorSingleton.getInstance().saveAPIKey(apiKey);
        AmbassadorSingleton.getInstance().startConversionTimer();

        // Checks boolean from sharedpreferences to see if this the first launch and registers conversion if it is
        if (!AmbassadorSingleton.getInstance().convertedOnInstall()) {
            AmbassadorSingleton.getInstance().convertForInstallation(parameters);
        }
    }
}
