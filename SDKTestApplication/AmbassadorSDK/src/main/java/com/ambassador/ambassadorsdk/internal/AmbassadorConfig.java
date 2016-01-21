package com.ambassador.ambassadorsdk.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONObject;

public class AmbassadorConfig {

    public static final String LINKED_IN_CLIENT_ID = new StringResource(R.string.linked_in_client_id).getValue();
    public static final String LINKED_IN_CLIENT_SECRET = new StringResource(R.string.linked_in_client_secret).getValue();
    public static final String LINKED_IN_CALLBACK_URL = new StringResource(R.string.linked_in_callback_url).getValue();
    static final String PUSHER_KEY_DEV = new StringResource(R.string.pusher_key_dev).getValue();
    static final String PUSHER_KEY_PROD = new StringResource(R.string.pusher_key_prod).getValue();
    static final String AUGUR_API_KEY = new StringResource(R.string.augur_api_key).getValue();

    public static final Boolean isReleaseBuild = false;

    private Context context = AmbassadorSingleton.getInstanceContext();
    private SharedPreferences sharePrefs;
    private ServiceSelectorPreferences rafParameters;

    public static String ambassadorApiUrl() {
        if (AmbassadorConfig.isReleaseBuild) {
            return new StringResource(R.string.ambassador_api_url).getValue();
        } else {
            return new StringResource(R.string.ambassador_api_url_dev).getValue();
        }
    }

    static String pusherCallbackURL() {
        if (AmbassadorConfig.isReleaseBuild) {
            return new StringResource(R.string.pusher_callback_url).getValue();
        } else {
            return new StringResource(R.string.pusher_callback_url_dev).getValue();
        }
    }

    public AmbassadorConfig() {
        sharePrefs = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);
    }

    public void setLinkedInToken(String token) {
        sharePrefs.edit().putString("linkedInToken", token).apply();
    }

    public void setTwitterAccessToken(String token) {
        sharePrefs.edit().putString("twitterToken", token).apply();
    }

    public void setTwitterAccessTokenSecret(String twitterTokenSecret) {
        sharePrefs.edit().putString("twitterTokenSecret", twitterTokenSecret).apply();
    }

    void setIdentifyObject(String objectString) {
        sharePrefs.edit().putString("identifyObject", objectString).apply();
    }

    public void setCampaignID(String campaignID) {
        sharePrefs.edit().putString("campaignID", campaignID).apply();
    }

    public void setPusherInfo(String pusherObject) {
        sharePrefs.edit().putString("pusherObject", pusherObject).apply();
    }

    public void setURL(String url) {
        sharePrefs.edit().putString("url", url).apply();
    }

    public void setUniversalToken(String univKey) {
        sharePrefs.edit().putString("universalToken", univKey).apply();
    }

    public void setUniversalID(String univID) {
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

    public void setUserEmail(String email) {
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

    public String getTwitterAccessTokenSecret() {
        return sharePrefs.getString("twitterTokenSecret", null);
    }

    public String getIdentifyObject() { return sharePrefs.getString("identifyObject", null); }

    public String getCampaignID() {
        return sharePrefs.getString("campaignID", null);
    }

    public String getPusherInfo() { return sharePrefs.getString("pusherObject", null); }

    @Nullable
    public JSONObject getPusherInfoObject() {
        try {
            return new JSONObject(getPusherInfo());
        } catch (Exception e) {
            return null;
        }
    }

    public String getURL() {
        return sharePrefs.getString("url", null);
    }

    public String getUniversalKey() { return sharePrefs.getString("universalToken", null); }

    public String getUniversalID() {
        return sharePrefs.getString("universalID", null);
    }

    public String getReferrerShortCode() { return sharePrefs.getString("referrerShortCode", null); }

    public String getReferralShortCode() { return sharePrefs.getString("referralShortCode", null); }

    public String getWebDeviceId() {
        return sharePrefs.getString("webDeviceId", null);
    }

    public String getUserFullName() {
        return sharePrefs.getString("fullName", null);
    }

    public String getEmailSubjectLine() {
        return sharePrefs.getString("subjectLine", null);
    }

    public String getUserEmail() {
        return sharePrefs.getString("userEmail", null);
    }

    public boolean getConvertedOnInstall() { return sharePrefs.getBoolean("installConversion", false); }

    public void setConvertOnInstall() {
        sharePrefs.edit().putBoolean("installConversion", true).apply();
    }

    public void nullifyTwitterIfInvalid(final NullifyCompleteListener listener) {
        if (TwitterCore.getInstance() != null && TwitterCore.getInstance().getSessionManager() != null) {
            final SessionManager sm = TwitterCore.getInstance().getSessionManager();
            TwitterSession activeSession = (TwitterSession) sm.getActiveSession();

            /** if TwitterSDK has an active session, store it in place of what we have */
            if (activeSession != null) {
                String key = activeSession.getAuthToken().token;
                String secret = activeSession.getAuthToken().secret;
                setTwitterAccessToken(key);
                setTwitterAccessTokenSecret(secret);
            }

            if (getTwitterAccessToken() != null && getTwitterAccessTokenSecret() != null) {
                TwitterAuthToken tat = new TwitterAuthToken(getTwitterAccessToken(), getTwitterAccessTokenSecret());
                TwitterSession twitterSession = new TwitterSession(tat, -1, null);
                sm.setActiveSession(twitterSession);
                TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(null, null, new Callback<User>() {
                    @Override
                    public void success(Result<User> result) {
                        callNullifyComplete(listener);
                    }

                    @Override
                    public void failure(TwitterException e) {
                        setTwitterAccessToken(null);
                        setTwitterAccessTokenSecret(null);
                        sm.clearActiveSession();
                        callNullifyComplete(listener);
                    }
                });
            } else {
                callNullifyComplete(listener);

            }
        } else {
            callNullifyComplete(listener);
        }
    }

    public void nullifyLinkedInIfInvalid(final NullifyCompleteListener listener) {
        if (getLinkedInToken() != null) {
            RequestManager rm = buildRequestManager();
            rm.getProfileLinkedIn(new RequestManager.RequestCompletion() {
                @Override
                public void onSuccess(Object successResponse) {
                    callNullifyComplete(listener);
                }

                @Override
                public void onFailure(Object failureResponse) {
                    setLinkedInToken(null);
                    callNullifyComplete(listener);
                }
            });
        } else {
            callNullifyComplete(listener);
        }
    }

    RequestManager buildRequestManager() {
        return new RequestManager();
    }

    void callNullifyComplete(NullifyCompleteListener listener) {
        if (listener != null) {
            listener.nullifyComplete();
        }
    }

    public interface NullifyCompleteListener {
        void nullifyComplete();
    }

}
