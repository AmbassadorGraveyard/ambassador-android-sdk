package com.example.ambassador.ambassadorsdk;

/**
 * Created by JakeDunahee on 8/31/15.
 */
public class UtilityClass {
    public static Boolean isSuccessfulResponseCode(int statusCode) {
        return (statusCode >= 200 && statusCode < 300);
    }
}
