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
    public static final String MBSY_UNIVERSAL_ID = "***REMOVED***";
    public static final String API_KEY = "UniversalToken ***REMOVED***"; // TEMP HERE UNTIL AMBASSADOR SETUP CREATED

    private  static AmbassadorSingleton mInstance = null;
    public Context context;
    public RAFParameters rafParameters;

    public static AmbassadorSingleton getInstance() {
        if(mInstance == null) {
            mInstance = new AmbassadorSingleton();
        }

        return mInstance;
    }

    public void setLinkedInToken(String token) {
        SharedPreferences prefs = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        prefs.edit().putString("linkedInToken", token);
    }

    public String getLinkedInToken() {
        SharedPreferences prefs = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        return prefs.getString("linkedInToken", null);
    }

    public void setIdentifyObject(String objectString) {
        SharedPreferences preferences = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        preferences.edit().putString("identifyObject", objectString).apply();
    }

    public String getIdentifyObject() {
        SharedPreferences preferences = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        return preferences.getString("identifyObject", "Fetching ShortURL");
    }

    public void startIdentify() {
        if (context == null) {
            context = MyApplication.getAppContext();
        }

        Identify identify = new Identify(context);
        identify.getIdentity();
    }
}
