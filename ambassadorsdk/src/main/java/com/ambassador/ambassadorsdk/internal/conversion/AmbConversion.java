package com.ambassador.ambassadorsdk.internal.conversion;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.inject.Inject;

public class AmbConversion {

    @Inject protected Campaign campaign;
    @Inject protected User user;
    @Inject protected RequestManager requestManager;
    protected ConversionParameters conversionParameters;
    protected boolean limitOnce;
    protected ConversionStatusListener conversionStatusListener;

    protected AmbConversion(ConversionParameters conversionParameters, boolean limitOnce, ConversionStatusListener conversionStatusListener) {
        AmbSingleton.inject(this);
        this.conversionParameters = conversionParameters;
        this.limitOnce = limitOnce;
        this.conversionStatusListener = conversionStatusListener;
    }

    public void execute() {
        if (conversionParameters.getCampaign() == -1 || conversionParameters.getRevenue() < 0) {
            Log.e("Ambassador", "Campaign and Revenue MUST be set on ConversionParameters!");
            if (conversionStatusListener != null) conversionStatusListener.error();
            return;
        }

        if (campaign.getReferredByShortCode() == null || "".equals(campaign.getReferredByShortCode()) || user.getEmail() == null) {
            Log.w("Ambassador", "Missing referrer data or user email, conversion is pending.");
            if (conversionStatusListener != null) conversionStatusListener.pending();
            conversionStatusListener = null;
            save();
            return;
        }

        conversionParameters.email = user.getEmail();

        requestManager.registerConversionRequest(conversionParameters, new RequestManager.RequestCompletion() {
            @Override
            public void onSuccess(Object successResponse) {
                if (conversionStatusListener != null) conversionStatusListener.success();
            }

            @Override
            public void onFailure(Object failureResponse) {
                if (conversionStatusListener != null) conversionStatusListener.error();
            }
        });
    }

    protected void save() {
        SharedPreferences sharedPreferences = AmbSingleton.getContext().getSharedPreferences("conversions", Context.MODE_PRIVATE);
        String content = sharedPreferences.getString("conversions", "[]");
        JsonArray conversions = new JsonParser().parse(content).getAsJsonArray();
        conversions.add(new JsonParser().parse(new Gson().toJson(conversionParameters)).getAsJsonObject());
        sharedPreferences.edit().putString("conversions", conversions.toString()).apply();
    }

    public static void attemptExecutePending() {
        SharedPreferences sharedPreferences = AmbSingleton.getContext().getSharedPreferences("conversions", Context.MODE_PRIVATE);
        String content = sharedPreferences.getString("conversions", "[]");
        sharedPreferences.edit().putString("conversions", "[]").apply();
        final JsonArray conversions = new JsonParser().parse(content).getAsJsonArray();
        for (final JsonElement jsonElement : conversions) {
            AmbConversion ambConversion = new Gson().fromJson(jsonElement, AmbConversion.class);
            ambConversion.execute();
        }
    }

    public static AmbConversion get(ConversionParameters conversionParameters, boolean limitOnce, ConversionStatusListener conversionStatusListener) {
        return new AmbConversion(conversionParameters, limitOnce, conversionStatusListener);
    }

}
