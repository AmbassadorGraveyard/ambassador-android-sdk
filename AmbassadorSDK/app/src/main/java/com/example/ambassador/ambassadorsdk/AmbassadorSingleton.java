package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by JakeDunahee on 7/29/15.
 */
public class AmbassadorSingleton {
    // Constants
    public static final String TWITTER_KEY = "HNXVVGWu5wYtXLx1J85nnbfw3";
    public static final String TWITTER_SECRET = "tqpYvovGGGUMw0aqUihb0Ybd2cZRB9uMCTmmCtBFj1sR8c5Rey";
    public static final String LINKED_IN_CALLBACK_URL = "http://localhost:2999";
    public static final String LINKED_IN_CLIENT_ID = "777z4czm3edaef";
    public static final String LINKED_IN_CLIENT_SECRET = "lM1FzXJauTSfxdnW";

    private  static AmbassadorSingleton mInstance = null;
    public AmbassadorActivity ambActivity;
    public RAFParameters rafParameters;

    public static AmbassadorSingleton getInstance() {
        if(mInstance == null) {
            mInstance = new AmbassadorSingleton();
        }

        return mInstance;
    }

    public String getLinkedInToken() {
        SharedPreferences prefs = ambActivity.getSharedPreferences("com.example.ambassador.ambassadorsdk", Context.MODE_PRIVATE);
        return prefs.getString("linkedInToken", null);
    }

    public void setTwitterToken(String token) {
        SharedPreferences prefs = ambActivity.getSharedPreferences("com.example.ambassador.ambassadorsdk", Context.MODE_PRIVATE);
        prefs.edit().putString("twitterToken", token);
    }

    public String getTwitterToken() {
        SharedPreferences prefs = ambActivity.getSharedPreferences("com.example.ambassador.ambassadorsdk", Context.MODE_PRIVATE);
        return prefs.getString("twitterToken", null);
    }

    public void setTwitterTokenSecret(String twitterTokenSecret) {
        SharedPreferences prefs = ambActivity.getSharedPreferences("com.example.ambassador.ambassadorsdk", Context.MODE_PRIVATE);
        prefs.edit().putString("twitterTokenSecret", twitterTokenSecret);
    }

    public String getTwitterTokenSecret() {
        SharedPreferences prefs = ambActivity.getSharedPreferences("com.example.ambassador.ambassadorsdk", Context.MODE_PRIVATE);
        return prefs.getString("twitterTokenSecret", null);
    }
}
