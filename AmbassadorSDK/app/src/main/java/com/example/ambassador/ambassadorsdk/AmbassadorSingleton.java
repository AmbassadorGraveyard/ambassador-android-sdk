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
    public static final String LINKED_IN_CLIENT_ID = "***REMOVED***";
    public static final String LINKED_IN_CLIENT_SECRET = "***REMOVED***";

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
}
