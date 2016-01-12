package com.ambassador.ambassadorsdk.utils;

import android.content.Context;
import android.content.res.Configuration;

public final class Device {

    private Context context;

    private Device() {}

    public Device(Context context) {
        this.context = context;
    }

    public boolean isTablet() {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public boolean isSmartPhone() {
        return !isTablet();
    }

    public String getType() {
        return (isTablet()) ? "Tablet" : "SmartPhone";
    }

}
