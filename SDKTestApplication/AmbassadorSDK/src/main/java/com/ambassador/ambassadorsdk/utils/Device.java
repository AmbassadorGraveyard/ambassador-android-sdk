package com.ambassador.ambassadorsdk.utils;

import android.content.Context;
import android.content.res.Configuration;

public final class Device {

    private boolean isTablet = false;
    private boolean isSmartPhone = true;

    private Device() {}

    public Device(Context context) {
        if ((context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            isTablet = true;
            isSmartPhone = false;
        }
    }

    public boolean isTablet() {
        return isTablet;
    }

    public boolean isSmartPhone() {
        return isSmartPhone;
    }

    public String getType() {
        return (isTablet()) ? "Tablet" : "SmartPhone";
    }

}
