package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by JakeDunahee on 7/22/15.
 */
public class Ambassador {
    public static void presentRAF(Context context, RAFParameters rafParameters, String campaignID) {
        Intent intent = new Intent(context, AmbassadorActivity.class);

        if (rafParameters == null) {
            rafParameters = new RAFParameters();
        }

        AmbassadorSingleton.getInstance().setCampaignID(campaignID);

        intent.putExtra("test", rafParameters);
        context.startActivity(intent);
    }

    public static void identify(String identifier) {
        Context context = AmbassadorSingleton.getInstance().context;

        IIdentify identify = new IdentifyAugur(context, identifier);
        AmbassadorSingleton.getInstance().startIdentify(identify);
    }

    public static void registerConversion(ConversionParameters parameters) {
        AmbassadorSingleton.getInstance().registerConversion(parameters);
    }

    public static void runWithKey(String apiKey) {
        AmbassadorSingleton.getInstance().saveAPIKey(apiKey);
        AmbassadorSingleton.getInstance().startConversionTimer();
    }

    public static void runWithKeyAndConvertOnInstall(String apiKey, ConversionParameters parameters) {
        AmbassadorSingleton.getInstance().saveAPIKey(apiKey);
        AmbassadorSingleton.getInstance().startConversionTimer();

        // Checks boolean from sharedpreferences to see if this the first launch and registers conversion if it is
        if (!AmbassadorSingleton.getInstance().convertedOnInstall()) {
            AmbassadorSingleton.getInstance().convertForInstallation(parameters);
        }
    }
}
