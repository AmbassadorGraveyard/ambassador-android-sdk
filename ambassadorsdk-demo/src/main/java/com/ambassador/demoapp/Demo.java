package com.ambassador.demoapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.BuildConfig;
import com.ambassador.ambassadorsdk.ConversionParameters;

import java.util.ArrayList;
import java.util.List;

public final class Demo extends Application {

    private static final boolean IS_RELEASE = BuildConfig.IS_RELEASE_BUILD;

    private static Demo instance;

    private SharedPreferences prefs;
    private List<ConversionParameters> conversions;

    @Override
    public void onCreate() {
        super.onCreate();
        Demo.instance = this;

        prefs = getSharedPreferences("amb_demo", Context.MODE_PRIVATE);
        conversions = new ArrayList<>();
    }

    public void runWithKeys(String id, String token) {
        AmbassadorSDK.runWithKeys(getApplicationContext(), id, token);
    }

    public void identify(String email) {
        setEmail(email);
        conversions = new ArrayList<>();
        AmbassadorSDK.identify(email);
    }

    public void signupConversion(@NonNull String email, @NonNull String username) {
        ConversionParameters parameters = new ConversionParameters.Builder()
                .setEmail(email)
                .setCampaign(Integer.parseInt(getCampaignId()))
                .setCustom1("This is a buyConversion from the Ambassador SDK Android test application.")
                .setCustom2("Username registered: " + username)
                .build();

        ambConversion(parameters, true);
        conversions.add(parameters);
    }

    public void buyConversion() {
        String email = getEmail();
        if (email == null) return;

        ConversionParameters parameters = new ConversionParameters.Builder()
                .setEmail(email)
                .setCampaign(Integer.parseInt(getCampaignId()))
                .setRevenue(24.55f)
                .setCustom1("This is a buyConversion from the Ambassador SDK Android test application.")
                .setCustom2("Buy buyConversion registered for $24.00")
                .build();

        ambConversion(parameters, false);
        conversions.add(parameters);
    }

    protected void ambConversion(ConversionParameters conversionParameters, boolean install) {
        AmbassadorSDK.registerConversion(conversionParameters, install);
    }

    public void presentRAF(Context context, String path) {
        AmbassadorSDK.presentRAF(context, getCampaignId(), path);
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

    @Nullable
    public List<ConversionParameters> getConversions() {
        return this.conversions;
    }

    public static Demo get() {
        return instance;
    }

}
