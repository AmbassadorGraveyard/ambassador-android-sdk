package com.example.ambassador.ambassadorsdk;

import android.app.Application;
import android.content.Context;

/**
 * Created by JakeDunahee on 7/31/15.
 */
public class MyApplication extends Application {
    private static Context context;

    private static AmbassadorActivityComponent component = null;

    public static void setComponent(AmbassadorActivityComponent comp) {
        component = comp;
    }

    public static AmbassadorActivityComponent component() {
        return component;
    }

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
