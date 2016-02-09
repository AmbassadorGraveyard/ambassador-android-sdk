package com.ambassador.ambassadorsdk.internal.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.google.gson.Gson;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;


/**
 * Stores, serializes and unserializes data pertaining to a Ambassador authentication
 * data, including social API tokens and Ambassador API tokens.
 */
public class Auth {

    // region Fields
    protected String universalId;
    protected String universalToken;
    protected String linkedInToken;
    protected String twitterToken;
    protected String twitterSecret;
    // endregion

    // region Getters / Setters
    public String getUniversalId() {
        return universalId;
    }

    public void setUniversalId(String universalId) {
        this.universalId = universalId;
        save();
    }

    public String getUniversalToken() {
        return universalToken;
    }

    public void setUniversalToken(String universalToken) {
        this.universalToken = universalToken;
        save();
    }

    public String getLinkedInToken() {
        return linkedInToken;
    }

    public void setLinkedInToken(String linkedInToken) {
        this.linkedInToken = linkedInToken;
        save();
    }

    public String getTwitterToken() {
        return twitterToken;
    }

    public void setTwitterToken(String twitterToken) {
        this.twitterToken = twitterToken;
        save();
    }

    public String getTwitterSecret() {
        return twitterSecret;
    }

    public void setTwitterSecret(String twitterSecret) {
        this.twitterSecret = twitterSecret;
        save();
    }
    // endregion

    // region Persistence methods
    /**
     * Serializes data into a JSON string and saves in SharedPreferences.
     */
    public void save() {
        if (AmbassadorSingleton.getInstanceContext() != null) {
            String data = new Gson().toJson(this);
            SharedPreferences sharedPreferences = AmbassadorSingleton.getInstanceContext().getSharedPreferences("auth", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString("auth", data).apply();
        }
    }

    /**
     * Clears instance data from this object's fields.
     */
    public void clear() {
        universalId = null;
        universalToken = null;
        linkedInToken = null;
        twitterToken = null;
        twitterSecret = null;
    }
    // endregion

    // region Nullify methods

    /**
     * Checks the User's twitter token and secret for validity, and returns
     * results through the passed in callback.
     * @param listener completion listener to pass back result of nullify
     */
    public void nullifyTwitterIfInvalid(final NullifyCompleteListener listener) {
        if (TwitterCore.getInstance() != null && TwitterCore.getInstance().getSessionManager() != null) {
            final SessionManager sm = TwitterCore.getInstance().getSessionManager();
            TwitterSession activeSession = (TwitterSession) sm.getActiveSession();

            /** if TwitterSDK has an active session, store it in place of what we have */
            if (activeSession != null) {
                String key = activeSession.getAuthToken().token;
                String secret = activeSession.getAuthToken().secret;
                setTwitterToken(key);
                setTwitterSecret(secret);
            }

            if (getTwitterToken() != null && getTwitterSecret() != null) {
                TwitterAuthToken tat = new TwitterAuthToken(getTwitterToken(), getTwitterSecret());
                TwitterSession twitterSession = new TwitterSession(tat, -1, null);
                sm.setActiveSession(twitterSession);
                TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(null, null, new Callback<com.twitter.sdk.android.core.models.User>() {
                    @Override
                    public void success(Result<User> result) {
                        callNullifyComplete(listener);
                    }

                    @Override
                    public void failure(TwitterException e) {
                        setTwitterToken(null);
                        setTwitterSecret(null);
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

    /**
     * Checks the User's linkedin token for validity, and returns
     * results through the passed in callback.
     * @param listener completion listener to pass back result of nullify
     */
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

    protected RequestManager buildRequestManager() {
        return new RequestManager();
    }

    protected void callNullifyComplete(NullifyCompleteListener listener) {
        if (listener != null) {
            listener.nullifyComplete();
        }
    }

    public interface NullifyCompleteListener {
        void nullifyComplete();
    }
    // endregion

}
