package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by JakeDunahee on 8/31/15.
 */
class Utilities {
    public static Boolean isSuccessfulResponseCode(int statusCode) {
        return (statusCode >= 200 && statusCode < 300);
    }

    public static int getPixelSizeForDimension(int dimension) {
        Context cxt = MyApplication.getAppContext();
        return cxt.getResources().getDimensionPixelSize(dimension);
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static String deviceType(Context context) {
        return (Utilities.isTablet(context)) ? "Tablet" : "SmartPhone";
    }
}
