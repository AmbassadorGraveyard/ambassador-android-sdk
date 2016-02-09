package com.ambassador.ambassadorsdk.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;

import org.json.JSONObject;

public class AmbassadorConfig { // TODO: Make final after UI tests figured out

    public static final Boolean isReleaseBuild = false;

    private Context context = AmbassadorSingleton.getInstanceContext();
    private SharedPreferences sharePrefs;

    public static String ambassadorApiUrl() {
        if (AmbassadorConfig.isReleaseBuild) {
            return new StringResource(R.string.ambassador_api_url).getValue();
        } else {
            return new StringResource(R.string.ambassador_api_url_dev).getValue();
        }
    }

    public AmbassadorConfig() {
        sharePrefs = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
    }

    void setIdentifyObject(String objectString) {
        sharePrefs.edit().putString("identifyObject", objectString).apply();
    }

    public void setPusherInfo(String pusherObject) {
        sharePrefs.edit().putString("pusherObject", pusherObject).apply();
    }

    public void setWebDeviceId(String deviceId) {
        sharePrefs.edit().putString("webDeviceId", deviceId).apply();
    }

    public String getLinkedInToken() { return sharePrefs.getString("linkedInToken", null); }

    public String getTwitterAccessToken() {
        return sharePrefs.getString("twitterToken", null);
    }

    public String getIdentifyObject() { return sharePrefs.getString("identifyObject", null); }

    public String getPusherInfo() { return sharePrefs.getString("pusherObject", null); }

    @Nullable
    public JSONObject getPusherInfoObject() {
        try {
            return new JSONObject(getPusherInfo());
        } catch (Exception e) {
            return null;
        }
    }

    public String getWebDeviceId() {
        return sharePrefs.getString("webDeviceId", null);
    }

    public boolean getConvertedOnInstall() { return sharePrefs.getBoolean("installConversion", false); }

    public void setConvertOnInstall() {
        sharePrefs.edit().putBoolean("installConversion", true).apply();
    }

}
