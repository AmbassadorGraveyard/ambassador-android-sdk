package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.SharedPreferences;

import java.sql.Time;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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
    public static final String PUSHER_APP_ID = "***REMOVED***";
    public static final String PUSHER_KEY = "***REMOVED***";
    public static final String PUSHER_SECRET = "***REMOVED***";
    public static final String MBSY_UNIVERSAL_ID = "***REMOVED***";
//    public static final String API_KEY = "UniversalToken ***REMOVED***"; // TEMP HERE UNTIL AMBASSADOR SETUP CREATED

    private  static AmbassadorSingleton mInstance = null;
    public Context context;
    private SharedPreferences sharePrefs;
    public RAFParameters rafParameters;

    public static AmbassadorSingleton getInstance() {
        if(mInstance == null) {
            mInstance = new AmbassadorSingleton();
            mInstance.context = MyApplication.getAppContext();
            mInstance.sharePrefs = mInstance.context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        }

        return mInstance;
    }

    public void setLinkedInToken(String token) {
        sharePrefs.edit().putString("linkedInToken", token).apply();
    }

    public String getLinkedInToken() {
        return sharePrefs.getString("linkedInToken", null);
    }

    public void setTwitterAccessToken(String token) {
        sharePrefs.edit().putString("twitterToken", token).apply();
    }

    public String getTwitterAccessToken() {
        return sharePrefs.getString("twitterToken", null);
    }

    public void setTwitterAccessTokenSecret(String twitterTokenSecret) {
        sharePrefs.edit().putString("twitterTokenSecret", twitterTokenSecret).apply();
    }

    public String getTwitterAccessTokenSecret() {
        return sharePrefs.getString("twitterTokenSecret", null);
    }

    public void setIdentifyObject(String objectString) {
        sharePrefs.edit().putString("identifyObject", objectString).apply();
    }

    public String getIdentifyObject() {
        return sharePrefs.getString("identifyObject", null);
    }

    public void startIdentify(IIdentify identify) {
        identify.getIdentity();
    }

    public void registerConversion(ConversionParameters parameters) {
        ConversionUtility conversionUtility = new ConversionUtility(context, parameters);
        conversionUtility.registerConversion();
    }

    public void startConversionTimer() {
        final ConversionUtility utility = new ConversionUtility(context);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                utility.readAndSaveDatabaseEntries();
            }
        }, 0, 10000);
    }

    public void setCampaignID(String campaignID) {
        sharePrefs.edit().putString("campaignID", campaignID).apply();
    }

    public String getCampaignID() {
        return sharePrefs.getString("campaignID", null);
    }

    public void savePusherInfo(String pusherObject) {
        sharePrefs.edit().putString("pusherObject", pusherObject).apply();
    }

    public String getPusherInfo() {
        return sharePrefs.getString("pusherObject", null);
    }

    public void saveURL(String url) {
        sharePrefs.edit().putString("url", url).apply();
    }

    public String getURL() {
        return sharePrefs.getString("url", null);
    }

    public void saveAPIKey(String apiKey) {
        sharePrefs.edit().putString("apiKey", apiKey).apply();
    }

    public String getAPIKey() {
        return sharePrefs.getString("apiKey", null);
    }

    public void convertForInstallation(ConversionParameters parameters) {
        AmbassadorSingleton.getInstance().registerConversion(parameters);
        sharePrefs.edit().putBoolean("installConversion", true).apply();
    }

    public Boolean convertedOnInstall() {
        return sharePrefs.getBoolean("installConversion", false);
    }
}
