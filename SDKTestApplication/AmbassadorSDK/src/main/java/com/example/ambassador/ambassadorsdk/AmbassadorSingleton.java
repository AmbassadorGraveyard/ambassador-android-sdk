package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by JakeDunahee on 7/29/15.
 */
public class AmbassadorSingleton {
    // Constants
    public static final String TWITTER_KEY = "***REMOVED***";
    public static final String TWITTER_SECRET = "***REMOVED***";
    public static final String LINKED_IN_CALLBACK_URL = "http://localhost:2999";
    public static final String LINKED_IN_CLIENT_ID = "***REMOVED***";
    public static final String LINKED_IN_CLIENT_SECRET = "***REMOVED***";

    private  static AmbassadorSingleton mInstance = null;
    public Context context;
    public RAFParameters rafParameters;

    public static AmbassadorSingleton getInstance() {
        if(mInstance == null) {
            mInstance = new AmbassadorSingleton();
        }

        return mInstance;
    }

    public String getLinkedInToken() {
        SharedPreferences prefs = context.getSharedPreferences("com.example.ambassador.ambassadorsdk", Context.MODE_PRIVATE);
        return prefs.getString("linkedInToken", null);
    }

    public void setTwitterAccessToken(String token) {
        SharedPreferences prefs = context.getSharedPreferences("com.example.ambassador.ambassadorsdk", Context.MODE_PRIVATE);
        prefs.edit().putString("twitterToken", token).apply();
    }

    public String getTwitterAccessToken() {
        SharedPreferences prefs = context.getSharedPreferences("com.example.ambassador.ambassadorsdk", Context.MODE_PRIVATE);
        return prefs.getString("twitterToken", null);
    }

    public void setTwitterAccessTokenSecret(String twitterTokenSecret) {
        SharedPreferences prefs = context.getSharedPreferences("com.example.ambassador.ambassadorsdk", Context.MODE_PRIVATE);
        prefs.edit().putString("twitterTokenSecret", twitterTokenSecret).apply();
    }

    public String getTwitterAccessTokenSecret() {
        SharedPreferences prefs = context.getSharedPreferences("com.example.ambassador.ambassadorsdk", Context.MODE_PRIVATE);
        return prefs.getString("twitterTokenSecret", null);
    }
}
