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
            AmbassadorSDK.runWithKeys(getApplicationContext(), "SDKToken 84444f4022a8cd4fce299114bc2e323e57e32188", "830883cd-b2a7-449c-8a3c-d1850aa8bc6b");
        } else {
            AmbassadorSDK.runWithKeys(this, "SDKToken 9de5757f801ca60916599fa3f3c92131b0e63c6a", "abfd1c89-4379-44e2-8361-ee7b87332e32");
        }
    }

    public void identify(String email) {
        AmbassadorSDK.identify(email);
    }

    public void signupConversion(@NonNull String email, @NonNull String username) {
        AmbassadorSDK.registerConversion(
                new ConversionParameters.Builder()
                        .setEmail(email)
                        .setCampaign(Integer.parseInt(getCampaignId()))
                        .setCustom1("This is a buyConversion from the Ambassador SDK Android test application.")
                        .setCustom2("Username registered: " + username)
                        .build()
                , true
        );
    }

    public void buyConversion() {
        String email = getEmail();
        if (email == null) return;
        AmbassadorSDK.registerConversion(
                new ConversionParameters.Builder()
                        .setEmail(email)
                        .setCampaign(Integer.parseInt(getCampaignId()))
                        .setRevenue(24)
                        .setCustom1("This is a buyConversion from the Ambassador SDK Android test application.")
                        .setCustom2("Buy buyConversion registered for $24.00")
                        .build()
                , false
        );
    }

    public void presentRAF(String path) {
        AmbassadorSDK.presentRAF(this, getCampaignId(), path);
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
