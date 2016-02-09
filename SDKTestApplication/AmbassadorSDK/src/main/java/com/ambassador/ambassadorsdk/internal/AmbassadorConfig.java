package com.ambassador.ambassadorsdk.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import org.json.JSONObject;

public class AmbassadorConfig { // TODO: Make final after UI tests figured out

    public static final Boolean isReleaseBuild = false;

    private Context context = AmbassadorSingleton.getInstanceContext();
    private SharedPreferences sharePrefs;

    public AmbassadorConfig() {
        sharePrefs = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
    }

    public void setPusherInfo(String pusherObject) {
        sharePrefs.edit().putString("pusherObject", pusherObject).apply();
    }
    
    public String getPusherInfo() { return sharePrefs.getString("pusherObject", null); }

    @Nullable
    public JSONObject getPusherInfoObject() {
        try {
            return new JSONObject(getPusherInfo());
        } catch (Exception e) {
            return null;
        }
    }

    public boolean getConvertedOnInstall() { return sharePrefs.getBoolean("installConversion", false); }

    public void setConvertOnInstall() {
        sharePrefs.edit().putBoolean("installConversion", true).apply();
    }

}
