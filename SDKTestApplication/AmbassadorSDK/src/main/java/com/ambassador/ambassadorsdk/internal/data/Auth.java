package com.ambassador.ambassadorsdk.internal.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.google.gson.Gson;


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
    }

    public String getUniversalToken() {
        return universalToken;
    }

    public void setUniversalToken(String universalToken) {
        this.universalToken = universalToken;
    }

    public String getLinkedInToken() {
        return linkedInToken;
    }

    public void setLinkedInToken(String linkedInToken) {
        this.linkedInToken = linkedInToken;
    }

    public String getTwitterToken() {
        return twitterToken;
    }

    public void setTwitterToken(String twitterToken) {
        this.twitterToken = twitterToken;
    }

    public String getTwitterSecret() {
        return twitterSecret;
    }

    public void setTwitterSecret(String twitterSecret) {
        this.twitterSecret = twitterSecret;
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

}
