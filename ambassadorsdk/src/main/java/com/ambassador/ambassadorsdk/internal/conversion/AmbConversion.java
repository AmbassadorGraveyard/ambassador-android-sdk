package com.ambassador.ambassadorsdk.internal.conversion;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class AmbConversion {

    protected ConversionParameters conversionParameters;
    protected boolean limitOnce;
    protected ConversionStatusListener conversionStatusListener;

    protected AmbConversion(ConversionParameters conversionParameters, boolean limitOnce, ConversionStatusListener conversionStatusListener) {
        this.conversionParameters = conversionParameters;
        this.limitOnce = limitOnce;
        this.conversionStatusListener = conversionStatusListener;
    }

    public void execute() {
        if (conversionParameters.getCampaign() == -1 || conversionParameters.getRevenue() < 0) {
            Log.e("Ambassador", "Campaign and Revenue MUST be set on ConversionParameters!");
            conversionStatusListener.error();
            return;
        }

    }

    protected void save() {
        SharedPreferences sharedPreferences = AmbSingleton.getContext().getSharedPreferences("conversions", Context.MODE_PRIVATE);
        String content = sharedPreferences.getString("conversions", "[]");
        JsonArray conversions = new JsonParser().parse(content).getAsJsonArray();
        conversions.add(new JsonParser().parse(new Gson().toJson(conversionParameters)).getAsJsonObject());
        sharedPreferences.edit().putString("conversions", conversions.toString()).apply();
    }

    public static AmbConversion get(ConversionParameters conversionParameters, boolean limitOnce, ConversionStatusListener conversionStatusListener) {
        return new AmbConversion(conversionParameters, limitOnce, conversionStatusListener);
    }

}
