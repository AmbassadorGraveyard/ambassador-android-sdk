package com.ambassador.ambassadorsdk.internal.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.google.gson.Gson;


/**
 * Stores, serializes and unserializes data pertaining to a Ambassador authentication
 * data, including social API tokens and Ambassador API tokens.
 */
public class Auth implements Data {

    // region Fields
    protected String universalId;
    protected String universalToken;
    protected String envoyId;
    protected String envoySecret;
    // endregion

    // region Getters / Setters
    @Nullable
    public String getUniversalId() {
        return universalId;
    }

    public void setUniversalId(@Nullable String universalId) {
        this.universalId = universalId;
        save();
    }

    @Nullable
    public String getUniversalToken() {
        return universalToken;
    }

    public void setUniversalToken(@Nullable String universalToken) {
        this.universalToken = universalToken;
        save();
    }

    @Nullable
    public String getEnvoyId() {
        return envoyId;
    }

    public void setEnvoyId(@Nullable String envoyId) {
        this.envoyId = envoyId;
        save();
    }

    @Nullable
    public String getEnvoySecret() {
        return envoySecret;
    }

    public void setEnvoySecret(@Nullable String envoySecret) {
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
        setEnvoyId(auth.getEnvoyId());
        setEnvoySecret(auth.getEnvoySecret());
    }
    // endregion

    // region Nullify methods

    // endregion

}
