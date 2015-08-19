package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by JakeDunahee on 7/29/15.
 */
public class AmbassadorSingleton {
    // Constants
    public static final String TWITTER_KEY = "QmXl03hbQEKSLLiDY4e6vpIjP";
    public static final String TWITTER_SECRET = "IfIbOuVbKwPkfJQW0zknChNMBbqhmpkuuK8FJmqkQqBqCGa4dW";
    public static final String LINKED_IN_CALLBACK_URL = "http://localhost:2999";
    public static final String LINKED_IN_CLIENT_ID = "777z4czm3edaef";
    public static final String LINKED_IN_CLIENT_SECRET = "lM1FzXJauTSfxdnW";
    public static final String PUSHER_APP_ID = "112803";
    public static final String PUSHER_KEY = "8bd3fe1994164f9b83f6";
    public static final String PUSHER_SECRET = "35327adb59c3b567a44a";
    public static final String MBSY_UNIVERSAL_ID = "abfd1c89-4379-44e2-8361-ee7b87332e32";
    public static final String API_KEY = "UniversalToken bdb49d2b9ae24b7b6bc5da122370f3517f98336f"; // TEMP HERE UNTIL AMBASSADOR SETUP CREATED

    private  static AmbassadorSingleton mInstance = null;
    public Context context;
    public RAFParameters rafParameters;

    public static AmbassadorSingleton getInstance() {
        if(mInstance == null) {
            mInstance = new AmbassadorSingleton();
            mInstance.context = MyApplication.getAppContext();
        }

        return mInstance;
    }

    public void setLinkedInToken(String token) {
        SharedPreferences prefs = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        prefs.edit().putString("linkedInToken", token).apply();
    }

    public String getLinkedInToken() {
        SharedPreferences prefs = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        return prefs.getString("linkedInToken", null);
    }

    public void setTwitterAccessToken(String token) {
        SharedPreferences prefs = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        prefs.edit().putString("twitterToken", token).apply();
    }

    public String getTwitterAccessToken() {
        SharedPreferences prefs = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        return prefs.getString("twitterToken", null);
    }

    public void setTwitterAccessTokenSecret(String twitterTokenSecret) {
        SharedPreferences prefs = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        prefs.edit().putString("twitterTokenSecret", twitterTokenSecret).apply();
    }

    public String getTwitterAccessTokenSecret() {
        SharedPreferences prefs = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        return prefs.getString("twitterTokenSecret", null);
    }

    public void setIdentifyObject(String objectString) {
        SharedPreferences preferences = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        preferences.edit().putString("identifyObject", objectString).apply();
    }

    public String getIdentifyObject() {
        SharedPreferences preferences = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        return preferences.getString("identifyObject", "Fetching ShortURL");
    }

    public void startIdentify(String email) {
        Identify identify = new Identify(context, email);
        identify.getIdentity();
    }

    public void setCampaignID(String campaignID) {
        SharedPreferences preferences = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        preferences.edit().putString("campaignID", campaignID).apply();
    }

    public String getCampaignID() {
        SharedPreferences preferences = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        return preferences.getString("campaignID", null);
    }

    public void savePusherInfo(String pusherObject) {
        SharedPreferences preferences = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        preferences.edit().putString("pusherObject", pusherObject).apply();
    }

    public String getPusherInfo() {
        SharedPreferences preferences = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        return preferences.getString("pusherObject", null);
    }

    public void saveURL(String url) {
        SharedPreferences preferences = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        preferences.edit().putString("url", url).apply();
    }

    public String getURL() {
        SharedPreferences preferences = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        return preferences.getString("url", null);
    }
}
