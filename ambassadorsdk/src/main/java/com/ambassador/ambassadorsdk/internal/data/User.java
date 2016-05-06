package com.ambassador.ambassadorsdk.internal.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


/**
 * Stores, serializes and unserializes data pertaining to an Ambassador identified user,
 * and what the backend needs to register conversions and track shares.
 */
public class User implements Data {

    // region Fields
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String gcmToken;
    protected JsonObject pusherInfo;
    protected JsonObject augurData;
    protected String webDeviceId;
    protected String facebookAccessToken;
    protected String twitterAccessToken;
    protected String linkedInAccessToken;
    // endregion

    // region Getters / Setters
    @Nullable
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        save();
    }

    @Nullable
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        save();
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        save();
        AmbSingleton.getContext()
                .getSharedPreferences("user", Context.MODE_PRIVATE)
                .edit()
                .putString("email", email)
                .apply();
    }

    @Nullable
    public String getGcmToken() {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        Log.v("Ambassador", gcmToken);
        this.gcmToken = gcmToken;
        save();
    }

    @Nullable
    public JsonObject getPusherInfo() {
        return pusherInfo;
    }

    public void setPusherInfo(JsonObject pusherInfo) {
        this.pusherInfo = pusherInfo;
        save();
    }

    @Nullable
    public JsonObject getAugurData() {
        return augurData;
    }

    public void setAugurData(JsonObject augurData) {
        this.augurData = augurData;
        save();
    }

    @Nullable
    public String getWebDeviceId() {
        return webDeviceId;
    }

    public void setWebDeviceId(String webDeviceId) {
        this.webDeviceId = webDeviceId;
        save();
    }

    @Nullable
    public String getFacebookAccessToken() {
        return facebookAccessToken;
    }

    public void setFacebookAccessToken(String facebookAccessToken) {
        this.facebookAccessToken = facebookAccessToken;
        save();
    }

    @Nullable
    public String getTwitterAccessToken() {
        return twitterAccessToken;
    }

    public void setTwitterAccessToken(String twitterAccessToken) {
        this.twitterAccessToken = twitterAccessToken;
        save();
    }

    @Nullable
    public String getLinkedInAccessToken() {
        return linkedInAccessToken;
    }

    public void setLinkedInAccessToken(String linkedInAccessToken) {
        this.linkedInAccessToken = linkedInAccessToken;
        save();
    }

    // endregion

    // region Persistence methods
    /**
     * Serializes data into a JSON string and saves in SharedPreferences,
     * keyed on the user's email.
     */
    @Override
    public void save() {
        if (AmbSingleton.getContext() != null) {
            String data = new Gson().toJson(this);
            SharedPreferences sharedPreferences = AmbSingleton.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(email, data).apply();
        }
    }

    /**
     * Clears instance data from this object's fields.
     */
    @Override
    public void clear() {
        firstName = null;
        lastName = null;
        email = null;
        gcmToken = null;
        augurData = null;
        webDeviceId = null;
        facebookAccessToken = null;
        twitterAccessToken = null;
        linkedInAccessToken = null;
    }

    /**
     * Clears the object and sets the data based on the currently saved
     * values for the current email address.
     */
    @Override
    public void refresh() {
        clear();
        String email = AmbSingleton.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).getString("email", null);

        if (email == null) return;

        String json = AmbSingleton.getContext().getSharedPreferences("user", Context.MODE_PRIVATE).getString(email, null);

        if (json == null) return;

        User user = new Gson().fromJson(json, User.class);
        setFirstName(user.getFirstName());
        setLastName(user.getLastName());
        setEmail(user.getEmail());
        setGcmToken(user.getGcmToken());
        setAugurData(user.getAugurData());
        setWebDeviceId(user.getWebDeviceId());
        setFacebookAccessToken(user.getFacebookAccessToken());
        setTwitterAccessToken(user.getTwitterAccessToken());
        setLinkedInAccessToken(user.getLinkedInAccessToken());
    }
    // endregion

}
