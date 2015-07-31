package com.example.ambassador.ambassadorsdk;

import android.app.Application;
import android.content.Context;

/**
 * Created by JakeDunahee on 7/31/15.
 */
class MyApplication extends Application{
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
