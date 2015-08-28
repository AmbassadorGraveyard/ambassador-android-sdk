package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Timer;
import java.util.TimerTask;

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

    private  static AmbassadorSingleton mInstance = null;
    public Context context;
    private SharedPreferences sharePrefs;
    public RAFParameters rafParameters;

    static AmbassadorSingleton getInstance() {
        if(mInstance == null) {
            mInstance = new AmbassadorSingleton();
            mInstance.context = MyApplication.getAppContext();
            mInstance.sharePrefs = mInstance.context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        }

        return mInstance;
    }

    // SHAREDINSTANCE SETTERS
    void setLinkedInToken(String token) {
        sharePrefs.edit().putString("linkedInToken", token).apply();
    }

    void setTwitterAccessToken(String token) {
        sharePrefs.edit().putString("twitterToken", token).apply();
    }

    void setTwitterAccessTokenSecret(String twitterTokenSecret) {
        sharePrefs.edit().putString("twitterTokenSecret", twitterTokenSecret).apply();
    }

    void setIdentifyObject(String objectString) {
        sharePrefs.edit().putString("identifyObject", objectString).apply();
    }

    void setCampaignID(String campaignID) {
        sharePrefs.edit().putString("campaignID", campaignID).apply();
    }

    void savePusherInfo(String pusherObject) {
        sharePrefs.edit().putString("pusherObject", pusherObject).apply();
    }

    void saveURL(String url) {
        sharePrefs.edit().putString("url", url).apply();
    }

    void saveAPIKey(String apiKey) {
        sharePrefs.edit().putString("apiKey", apiKey).apply();
    }

    void convertForInstallation(ConversionParameters parameters) {
        AmbassadorSingleton.getInstance().registerConversion(parameters);
        sharePrefs.edit().putBoolean("installConversion", true).apply();
    }
    // END SHAREDINSTANCE SETTERS


    // SHAREDINSTANCE GETTERS
    String getLinkedInToken() { return sharePrefs.getString("linkedInToken", null); }

    String getTwitterAccessToken() { return sharePrefs.getString("twitterToken", null); }

    String getTwitterAccessTokenSecret() { return sharePrefs.getString("twitterTokenSecret", null); }

    String getIdentifyObject() { return sharePrefs.getString("identifyObject", null); }

    String getCampaignID() {
        return sharePrefs.getString("campaignID", null);
    }

    String getPusherInfo() { return sharePrefs.getString("pusherObject", null); }

    String getURL() {
        return sharePrefs.getString("url", null);
    }

    String getAPIKey() { return sharePrefs.getString("apiKey", null); }

    Boolean convertedOnInstall() { return sharePrefs.getBoolean("installConversion", false); }
    //END SHAREDINSTANCE GETTERS



    void registerConversion(ConversionParameters parameters) {
        ConversionUtility conversionUtility = new ConversionUtility(context, parameters);
        conversionUtility.registerConversion();
    }

    void startIdentify(IIdentify identify) {
        identify.getIdentity();
    }
    
    void startConversionTimer() {
        final ConversionUtility utility = new ConversionUtility(context);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                utility.readAndSaveDatabaseEntries();
            }
        }, 0, 10000);
    }




}
