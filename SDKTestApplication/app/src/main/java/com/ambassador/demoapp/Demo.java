package com.ambassador.demoapp;

import android.app.Application;

import com.ambassador.ambassadorsdk.AmbassadorSDK;

public final class Demo extends Application {

    private static final boolean IS_RELEASE = false;

    private static Demo instance;

    @Override
    public void onCreate() {
        super.onCreate();
        Demo.instance = this;

        if (IS_RELEASE) {
            AmbassadorSDK.runWithKeys(getApplicationContext(), "SDKToken ***REMOVED***", "***REMOVED***");
        } else {
            AmbassadorSDK.runWithKeys(this, "SDKToken ***REMOVED***", "***REMOVED***");
        }
    }

    public void identify(String email) {
        AmbassadorSDK.identify(email);
    }

    public static Demo get() {
        return instance;
    }

}
