package com.ambassador.ambassadorsdk.internal.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;


/**
 *
 */
public final class Device {

    protected Configuration         configuration;
    protected ConnectivityManager   connectivityManager;
    protected InputMethodManager    inputMethodManager;

    public Device() {
        Context context = AmbassadorSingleton.getInstanceContext();
        configuration = context.getResources().getConfiguration();
        connectivityManager =
                (ConnectivityManager) AmbassadorSingleton
                        .getInstanceContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        inputMethodManager =
                (InputMethodManager) AmbassadorSingleton
                        .getInstanceContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public boolean isTablet() {
        return (configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public boolean isSmartPhone() {
        return (configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK)
                < Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @NonNull
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

    public void openSoftKeyboard(@NonNull View view) {
        inputMethodManager.showSoftInput(view, 0);
    }

    public void closeSoftKeyboard(@NonNull View view) {
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
