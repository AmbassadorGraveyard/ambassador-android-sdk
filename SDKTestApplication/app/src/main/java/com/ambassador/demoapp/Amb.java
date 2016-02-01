package com.ambassador.demoapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.ConversionParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for interfacing with Ambassador SDK.
 */
public class Amb {

    private Context context;

    private SharedPreferences prefs;
    private List<ConversionParameters> conversions;

    private Amb() {}

    public Amb(Context context) {
        this.context = context.getApplicationContext();
        prefs = context.getSharedPreferences("amb_demo", Context.MODE_PRIVATE);
        conversions = new ArrayList<>();
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

        AmbassadorSDK.registerConversion(parameters, true);
        conversions.add(parameters);
    }

    public void buyConversion() {
        String email = getEmail();
        if (email == null) return;

        ConversionParameters parameters = new ConversionParameters.Builder()
                .setEmail(email)
                .setCampaign(Integer.parseInt(getCampaignId()))
                .setRevenue(24)
                .setCustom1("This is a buyConversion from the Ambassador SDK Android test application.")
                .setCustom2("Buy buyConversion registered for $24.00")
                .build();

        AmbassadorSDK.registerConversion(parameters, false);
        conversions.add(parameters);
    }

    public void presentRAF(String path) {
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

}
