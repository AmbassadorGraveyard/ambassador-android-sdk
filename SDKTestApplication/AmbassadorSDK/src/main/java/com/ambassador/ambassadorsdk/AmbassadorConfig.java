package com.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by JakeDunahee on 7/29/15.
 */
public class AmbassadorConfig {
    // Constants
    static final String TWITTER_KEY = "QmXl03hbQEKSLLiDY4e6vpIjP";
    static final String TWITTER_SECRET = "IfIbOuVbKwPkfJQW0zknChNMBbqhmpkuuK8FJmqkQqBqCGa4dW";
    static final String CALLBACK_URL = "http://localhost:2999";
    static final String LINKED_IN_CLIENT_ID = "777z4czm3edaef";
    static final String LINKED_IN_CLIENT_SECRET = "lM1FzXJauTSfxdnW";
    //static final String PUSHER_APP_ID = "112803";
    static final String PUSHER_KEY_DEV = "8bd3fe1994164f9b83f6";
    static final String PUSHER_KEY_PROD = "79576dbee58121cac49a";
    //static final String PUSHER_SECRET = "35327adb59c3b567a44a";
    static final Boolean isReleaseBuild = false;

    private Context context = AmbassadorSingleton.get();
    private SharedPreferences sharePrefs = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
    private ServiceSelectorPreferences rafParameters;

    static String identifyURL() {
        if (AmbassadorConfig.isReleaseBuild) {
            return "https://api.getambassador.com/universal/action/identify/?u=";
        } else {
            return "https://dev-ambassador-api.herokuapp.com/universal/action/identify/?u=";
        }
    }

    static String conversionURL() {
        if (AmbassadorConfig.isReleaseBuild) {
            return "https://api.getambassador.com/universal/action/conversion/?u=";
        } else {
            return "https://dev-ambassador-api.herokuapp.com/universal/action/conversion/?u=";
        }
    }

    static String bulkSMSShareURL() {
        if (AmbassadorConfig.isReleaseBuild) {
            return "https://api.getambassador.com/share/sms/";
        } else {
            return "https://dev-ambassador-api.herokuapp.com/share/sms/";
        }
    }

    static String bulkEmailShareURL() {
        if (AmbassadorConfig.isReleaseBuild) {
            return "https://api.getambassador.com/share/email/";
        } else {
            return "https://dev-ambassador-api.herokuapp.com/share/email/";
        }
    }

    static String shareTrackURL() {
        if (AmbassadorConfig.isReleaseBuild) {
            return "https://api.getambassador.com/track/share/";
        } else {
            return "https://dev-ambassador-api.herokuapp.com/track/share/";
        }
    }

    static String pusherChannelNameURL() {
        if (AmbassadorConfig.isReleaseBuild) {
            return "https://api.getambassador.com/auth/session/";
        } else {
            return "https://dev-ambassador-api.herokuapp.com/auth/session/";
        }
    }

    static String pusherCallbackURL() {
        if (AmbassadorConfig.isReleaseBuild) {
            return "https://api.getambassador.com/auth/subscribe/";
        } else {
            return "https://dev-ambassador-api.herokuapp.com/auth/subscribe/";
        }
    }

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

    public void setPusherInfo(String pusherObject) {
        sharePrefs.edit().putString("pusherObject", pusherObject).apply();
    }

    public void setURL(String url) {
        sharePrefs.edit().putString("url", url).apply();
    }

    void setUniversalToken(String univKey) {
        sharePrefs.edit().putString("universalToken", univKey).apply();
    }

    void setUniversalID(String univID) {
        sharePrefs.edit().putString("universalID", univID).apply();
    }

    public void setReferrerShortCode(String shortCode) {
        sharePrefs.edit().putString("referrerShortCode", shortCode).apply();
    }

    public void setReferralShortCode(String shortCode) {
        sharePrefs.edit().putString("referralShortCode", shortCode).apply();
    }

    public void setWebDeviceId(String deviceId) {
        sharePrefs.edit().putString("webDeviceId", deviceId).apply();
    }

    public void setUserFullName(String firstName, String lastName) {
        sharePrefs.edit().putString("fullName", firstName + " " + lastName).apply();
    }

    public void setEmailSubject(String subjectLine) {
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

    public ServiceSelectorPreferences getRafParameters() { return rafParameters; }

    public String getLinkedInToken() { return sharePrefs.getString("linkedInToken", null); }

    public String getTwitterAccessToken() {
        return sharePrefs.getString("twitterToken", null);
    }

    String getTwitterAccessTokenSecret() { return sharePrefs.getString("twitterTokenSecret", null); }

    String getIdentifyObject() { return sharePrefs.getString("identifyObject", null); }

    public String getCampaignID() {
        return sharePrefs.getString("campaignID", null);
    }

    public String getPusherInfo() { return sharePrefs.getString("pusherObject", null); }

    public String getURL() {
        return sharePrefs.getString("url", null);
    }

    public String getUniversalKey() { return sharePrefs.getString("universalToken", null); }

    public String getUniversalID() {
        return sharePrefs.getString("universalID", null);
    }

    String getReferrerShortCode() { return sharePrefs.getString("referrerShortCode", null); }

    String getReferralShortCode() { return sharePrefs.getString("referralShortCode", null); }

    String getWebDeviceId() {
        return sharePrefs.getString("webDeviceId", null);
    }

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

    void setConvertOnInstall() {
        sharePrefs.edit().putBoolean("installConversion", true).apply();
    }
}
