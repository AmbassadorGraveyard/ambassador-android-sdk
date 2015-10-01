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
    static final String TWITTER_KEY = "QmXl03hbQEKSLLiDY4e6vpIjP";
    static final String TWITTER_SECRET = "IfIbOuVbKwPkfJQW0zknChNMBbqhmpkuuK8FJmqkQqBqCGa4dW";
    static final String CALLBACK_URL = "http://localhost:2999";
    static final String LINKED_IN_CLIENT_ID = "777z4czm3edaef";
    static final String LINKED_IN_CLIENT_SECRET = "lM1FzXJauTSfxdnW";
    static final String PUSHER_APP_ID = "112803";
    static final String PUSHER_KEY = "8bd3fe1994164f9b83f6";
    static final String PUSHER_SECRET = "35327adb59c3b567a44a";
    static final Boolean isReleaseBuild = false;

    private static AmbassadorSingleton mInstance = null;
    private Context context;
    private SharedPreferences sharePrefs;
    private ServiceSelectorPreferences rafParameters;

    static AmbassadorSingleton getInstance() {
        if(mInstance == null) {
            mInstance = new AmbassadorSingleton();
            mInstance.context = MyApplication.getAppContext();
            mInstance.sharePrefs = mInstance.context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
        }

        return mInstance;
    }



    // STATIC VARIABLES
    static String identifyURL() {
        if (AmbassadorSingleton.isReleaseBuild) {
            return "https://api.ambassador.com/universal/action/identify/?u=";
        } else {
            return "https://dev-ambassador-api.herokuapp.com/universal/action/identify/?u=";
        }
    }

    static String conversionURL() {
        if (AmbassadorSingleton.isReleaseBuild) {
            return "https://api.ambassador.com/universal/action/conversion/?u=";
        } else {
            return "https://dev-ambassador-api.herokuapp.com/universal/action/conversion/?u=";
        }
    }

    static String bulkSMSShareURL() {
        if (AmbassadorSingleton.isReleaseBuild) {
            return "https://api.ambassador.com/share/sms/";
        } else {
            return "https://dev-ambassador-api.herokuapp.com/share/sms/";
        }
    }

    static String bulkEmailShareURL() {
        if (AmbassadorSingleton.isReleaseBuild) {
            return "https://api.ambassador.com/share/email/";
        } else {
            return "https://dev-ambassador-api.herokuapp.com/share/email/";
        }
    }

    static String shareTrackURL() {
        if (AmbassadorSingleton.isReleaseBuild) {
            return "https://api.ambassador.com/track/share/";
        } else {
            return "https://dev-ambassador-api.herokuapp.com/track/share/";
        }
    }

    static String pusherCallbackURL() {
        if (AmbassadorSingleton.isReleaseBuild) {
            return "https://api.ambassador.com/auth/subscribe/";
        } else {
            return "https://dev-ambassador-api.herokuapp.com/auth/subscribe/";
        }
    }
    // END STATIC VARIABLES


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

    public void saveURL(String url) {
        sharePrefs.edit().putString("url", url).apply();
    }

    void saveUniversalToken(String univKey) {
        sharePrefs.edit().putString("universalToken", univKey).apply();
    }

    void saveUniversalID(String univID) {
        sharePrefs.edit().putString("universalID", univID).apply();
    }

    public void saveShortCode(String shortCode) {
        sharePrefs.edit().putString("shortCode", shortCode).apply();
    }

    void saveUserFullName(String firstName, String lastName) {
        sharePrefs.edit().putString("fullName", firstName + " " + lastName).apply();
    }

    public void saveEmailSubject(String subjectLine) {
        sharePrefs.edit().putString("subjectLine", subjectLine).apply();
    }

    public void setRafDefaultMessage(String message) {
        rafParameters.defaultShareMessage = message;
    }

    void setUserEmail(String email) {
        sharePrefs.edit().putString("userEmail", email).apply();
    }

    public void setRafParameters(String defaultShareMessage, String titleText, String descriptionText, String toolbarTitle) {
        rafParameters = new ServiceSelectorPreferences();
        rafParameters.defaultShareMessage = defaultShareMessage;
        rafParameters.titleText = titleText;
        rafParameters.descriptionText = descriptionText;
        rafParameters.toolbarTitle = toolbarTitle;
    }
    // END SHAREDINSTANCE SETTERS


    // SHAREDINSTANCE GETTERS
    public ServiceSelectorPreferences getRafParameters() { return rafParameters; }

    String getLinkedInToken() { return sharePrefs.getString("linkedInToken", null); }

    String getTwitterAccessToken() { return sharePrefs.getString("twitterToken", null); }

    String getTwitterAccessTokenSecret() { return sharePrefs.getString("twitterTokenSecret", null); }

    String getIdentifyObject() { return sharePrefs.getString("identifyObject", null); }

    public String getCampaignID() {
        return sharePrefs.getString("campaignID", null);
    }

    public String getPusherInfo() { return sharePrefs.getString("pusherObject", null); }

    String getURL() {
        return sharePrefs.getString("url", null);
    }

    String getUniversalKey() { return sharePrefs.getString("universalToken", null); }

    String getUniversalID() {
        return sharePrefs.getString("universalID", null);
    }

    String getShortCode() { return sharePrefs.getString("shortCode", null); }

    String getFullName() {
        return sharePrefs.getString("fullName", null);
    }

    String getEmailSubjectLine() {
        return sharePrefs.getString("subjectLine", null);
    }

    String getUserEmail() {
        return sharePrefs.getString("userEmail", null);
    }

    boolean convertedOnInstall() { return sharePrefs.getBoolean("installConversion", false); }
    // END SHAREDINSTANCE GETTERS


    // AMBASSADOR SDK CALLS
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

    void convertForInstallation(ConversionParameters parameters) {
        registerConversion(parameters);
        sharePrefs.edit().putBoolean("installConversion", true).apply();
    }
    // END AMBASSADOR SDK CALLS
}
