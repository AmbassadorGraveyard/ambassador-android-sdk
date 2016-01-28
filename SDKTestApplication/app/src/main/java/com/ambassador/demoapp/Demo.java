package com.ambassador.demoapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.ConversionParameters;

public final class Demo extends Application {

    private static final boolean IS_RELEASE = false;

    private static Demo instance;

    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        Demo.instance = this;

        prefs = getSharedPreferences("amb_demo", MODE_PRIVATE);

        if (IS_RELEASE) {
            AmbassadorSDK.runWithKeys(getApplicationContext(), "SDKToken ***REMOVED***", "***REMOVED***");
        } else {
            AmbassadorSDK.runWithKeys(this, "SDKToken ***REMOVED***", "***REMOVED***");
        }
    }

    public void identify(String email) {
        AmbassadorSDK.identify(email);
    }

    public void conversion() {
        String email = getEmail();
        if (email == null) return;
        AmbassadorSDK.registerConversion(
                new ConversionParameters.Builder()
                        .setEmail(email)
                        .setCampaign(260)
                        .setRevenue(24)
                        .setCustom1("This is a conversion from the Ambassador SDK Android test application.")
                        .setCustom2("Buy conversion registered for $24.00")
                        .build()
                , false
        );

    }

    public void setEmail(String email) {
        prefs.edit().putString("email", email).apply();
    }

    @Nullable
    public String getEmail() {
        return prefs.getString("email", null);
    }

    public void setCampaignId(String campaignId) {
        if (campaignId != null && campaignId.length() == 0) {
            prefs.edit().putString("campaignId", null).apply();
        } else {
            prefs.edit().putString("campaignId", campaignId).apply();
        }
    }

    @NonNull
    public String getCampaignId() {
        return prefs.getString("campaignId", "260");
    }

    public static Demo get() {
        return instance;
    }

}
