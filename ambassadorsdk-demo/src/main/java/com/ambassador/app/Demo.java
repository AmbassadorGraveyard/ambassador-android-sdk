package com.ambassador.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;

public final class Demo extends Application {

    private static Demo instance;

    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        Demo.instance = this;
        prefs = getSharedPreferences("amb_demo", Context.MODE_PRIVATE);
        AmbSingleton.init(this);
    }

    public void setEmail(String email) {
        prefs.edit().putString("email", email).apply();
    }

    @Nullable
    public String getEmail() {
        return prefs.getString("email", null);
    }

    public static Demo get() {
        return instance;
    }

}
