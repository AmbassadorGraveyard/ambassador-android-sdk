package com.ambassador.demoapp;

import android.app.Application;
import android.support.annotation.Nullable;

import com.ambassador.ambassadorsdk.AmbassadorSDK;

public final class Demo extends Application {

    private static final boolean IS_RELEASE = false;

    private static Demo instance;

    public static Amb amb;

    @Override
    public void onCreate() {
        super.onCreate();
        Demo.instance = this;

        if (IS_RELEASE) {
            AmbassadorSDK.runWithKeys(getApplicationContext(), "SDKToken 84444f4022a8cd4fce299114bc2e323e57e32188", "830883cd-b2a7-449c-8a3c-d1850aa8bc6b");
        } else {
            AmbassadorSDK.runWithKeys(this, "SDKToken 9de5757f801ca60916599fa3f3c92131b0e63c6a", "abfd1c89-4379-44e2-8361-ee7b87332e32");
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
