package com.ambassador.ambassadorsdk.internal.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;
import com.google.gson.Gson;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import twitter4j.auth.AccessToken;


/**
 * Stores, serializes and unserializes data pertaining to a Ambassador authentication
 * data, including social API tokens and Ambassador API tokens.
 */
public class Auth implements Data {

    // region Fields
    protected String universalId;
    protected String universalToken;
    protected String facebookToken;
    protected String linkedInToken;
    protected String twitterToken;
    protected String twitterSecret;
    protected String envoyId;
    protected String envoySecret;
    // endregion

    // region Getters / Setters
    @Nullable
    public String getUniversalId() {
        return universalId;
    }

    public void setUniversalId(String universalId) {
        this.universalId = universalId;
        save();
    }

    @Nullable
    public String getUniversalToken() {
        return universalToken;
    }

    public void setUniversalToken(String universalToken) {
        this.universalToken = universalToken;
        save();
    }

    @Nullable
    public String getFacebookToken() {
        return facebookToken;
    }

    public void setFacebookToken(String facebookToken) {
        this.facebookToken = facebookToken;
        save();
    }

    @Nullable
    public String getLinkedInToken() {
        return linkedInToken;
    }

    public void setLinkedInToken(String linkedInToken) {
        this.linkedInToken = linkedInToken;
        save();
    }

    @Nullable
    public String getTwitterToken() {
        return twitterToken;
    }

    public void setTwitterToken(String twitterToken) {
        this.twitterToken = twitterToken;
        save();
    }

    @Nullable
    public String getTwitterSecret() {
        return twitterSecret;
    }

    public void setTwitterSecret(String twitterSecret) {
        this.twitterSecret = twitterSecret;
        save();
    }

    public String getEnvoyId() {
        return envoyId;
    }

    public void setEnvoyId(String envoyId) {
        this.envoyId = envoyId;
        save();
    }

    public String getEnvoySecret() {
        return envoySecret;
    }

    public void setEnvoySecret(String envoySecret) {
        this.envoySecret = envoySecret;
        save();
    }

    // endregion

    // region Persistence methods
    /**
     * Serializes data into a JSON string and saves in SharedPreferences.
     */
    @Override
    public void save() {
        if (AmbSingleton.getContext() != null) {
            String data = new Gson().toJson(this);
            SharedPreferences sharedPreferences = AmbSingleton.getContext().getSharedPreferences("auth", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString("auth", data).apply();
        }
    }

    /**
     * Clears instance data from this object's fields.
     */
    @Override
    public void clear() {
        universalId = null;
        universalToken = null;
        facebookToken = null;
        linkedInToken = null;
        twitterToken = null;
        twitterSecret = null;
        envoyId = null;
        envoySecret = null;
    }

    /**
     * Clears the object and sets the data based on the currently saved
     * values for authentication.
     */
    @Override
    public void refresh() {
        clear();
        String json = AmbSingleton.getContext().getSharedPreferences("auth", Context.MODE_PRIVATE).getString("auth", null);

        if (json == null) return;

        Auth auth = new Gson().fromJson(json, Auth.class);
        setUniversalId(auth.getUniversalId());
        setUniversalToken(auth.getUniversalToken());
        setFacebookToken(auth.getFacebookToken());
        setLinkedInToken(auth.getLinkedInToken());
        setTwitterToken(auth.getTwitterToken());
        setTwitterSecret(auth.getTwitterSecret());
        setEnvoyId(auth.getEnvoyId());
        setEnvoySecret(auth.getEnvoySecret());
    }
    // endregion

    // region Nullify methods

    /**
     * Checks the User's twitter token and secret for validity, and returns
     * results through the passed in callback.
     * @param listener completion listener to pass back result of nullify
     */
    public void nullifyTwitterIfInvalid(final NullifyCompleteListener listener) {
        String accessToken = getTwitterToken();
        String accessSecret = getTwitterSecret();

        if (accessToken == null || accessSecret == null) {
            setTwitterToken(null);
            setTwitterSecret(null);
            callNullifyComplete(listener);
            return;
        }

        AsyncTwitter twitter = getTwitter();
        String twitterConsumerKey = new StringResource(R.string.twitter_consumer_key).getValue();
        String twitterConsumerSecret = new StringResource(R.string.twitter_consumer_secret).getValue();
        twitter.setOAuthConsumer(twitterConsumerKey, twitterConsumerSecret);
        twitter.setOAuthAccessToken(new AccessToken(accessToken, accessSecret));

        ambTwitterAdapter.setListener(listener);
        twitter.addListener(ambTwitterAdapter);

        twitter.getUserTimeline();
    }

    protected AsyncTwitter getTwitter() {
        return new AsyncTwitterFactory().getInstance();
    }

    protected AmbTwitterAdapter ambTwitterAdapter = new AmbTwitterAdapter() {

        @Override
        public void gotUserTimeline(ResponseList<Status> statuses) {
            callNullifyComplete(listener);
        }

        @Override
        public void onException(TwitterException te, TwitterMethod method) {
            setTwitterSecret(null);
            setTwitterToken(null);
            callNullifyComplete(listener);
        }

    };

    protected class AmbTwitterAdapter extends TwitterAdapter {

        protected NullifyCompleteListener listener;

        public void setListener(NullifyCompleteListener listener) {
            this.listener = listener;
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

    @NonNull
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
