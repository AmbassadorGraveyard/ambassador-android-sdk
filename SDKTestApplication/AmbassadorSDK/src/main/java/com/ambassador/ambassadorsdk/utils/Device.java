package com.ambassador.ambassadorsdk.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;

public final class Device {

    private Configuration configuration;
    private ConnectivityManager connectivityManager;

    public Device() {
        Context context = AmbassadorSingleton.getInstanceContext();
        configuration = context.getResources().getConfiguration();
        connectivityManager =
                (ConnectivityManager) AmbassadorSingleton
                        .getInstanceContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isTablet() {
        return (configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public boolean isSmartPhone() {
        return (configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                < Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public String getType() {
        return (isTablet()) ? "Tablet" : "SmartPhone";
    }

    public boolean isConnected() {
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.isConnected()) {
                return true;
            }
        }

        return false;
    }

}
