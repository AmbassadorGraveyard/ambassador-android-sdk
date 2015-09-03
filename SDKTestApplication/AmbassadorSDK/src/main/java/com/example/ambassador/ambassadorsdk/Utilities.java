package com.example.ambassador.ambassadorsdk;


/**
 * Created by JakeDunahee on 8/31/15.
 */
class Utilities {
    public static Boolean isSuccessfulResponseCode(int statusCode) {
        return (statusCode >= 200 && statusCode < 300);
    }

    public static int getPixelSizeForDimension(int dimension) {
        return MyApplication.getAppContext().getResources().getDimensionPixelSize(dimension);
    }

    public static float getScreenDensity() {
        return MyApplication.getAppContext().getResources().getDisplayMetrics().density;
    }
}
