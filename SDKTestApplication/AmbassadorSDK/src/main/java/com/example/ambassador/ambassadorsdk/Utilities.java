package com.example.ambassador.ambassadorsdk;

import android.content.Context;

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
}
