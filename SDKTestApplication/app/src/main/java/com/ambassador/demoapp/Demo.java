package com.ambassador.demoapp;

import android.app.Application;
import android.support.annotation.Nullable;

import com.ambassador.ambassadorsdk.AmbassadorSDK;

public final class Demo extends Application {

    private static final boolean IS_RELEASE = false;

    private static Demo instance;

    private static Amb amb;

    @Override
    public void onCreate() {
        super.onCreate();
        Demo.instance = this;

        if (IS_RELEASE) {
            AmbassadorSDK.runWithKeys(getApplicationContext(), "SDKToken ***REMOVED***", "***REMOVED***");
        } else {
            AmbassadorSDK.runWithKeys(this, "SDKToken ***REMOVED***", "***REMOVED***");
        }

        amb = new Amb(this);
    }

    public static Demo get() {
        return instance;
    }

    @Nullable
    public static Amb getAmb() {
        return amb;
    }

}
