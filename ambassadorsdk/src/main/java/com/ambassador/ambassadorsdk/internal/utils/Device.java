package com.ambassador.ambassadorsdk.internal.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;

/**
 *
 */
public class Device { // TODO: Make final after UI tests figured out

    protected Configuration         configuration;
    protected ConnectivityManager   connectivityManager;
    protected InputMethodManager    inputMethodManager;
    protected ClipboardManager      clipboardManager;
    protected WindowManager         windowManager;

    public Device() {
        this(AmbSingleton.getContext());
    }

    public Device(Context context) {
        configuration = context.getResources().getConfiguration();
        connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboardManager =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        windowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public int getScreenWidth() {
        return windowManager.getDefaultDisplay().getWidth();
    }

    public int getScreenHeight() {
        return windowManager.getDefaultDisplay().getHeight();
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

    public void copyToClipboard(@NonNull String text) {
        ClipData clipData = ClipData.newPlainText("simpleText", text);
        clipboardManager.setPrimaryClip(clipData);
    }

    public int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

}
