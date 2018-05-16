package com.ambassador.ambassadorsdk.internal.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.identify.AmbassadorIdentification;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.inject.Inject;


/**
 * Stores, serializes and unserializes data pertaining to an Ambassador identified user,
 * and what the backend needs to register conversions and track shares.
 */
public class User implements Data {

    @Inject
    protected AmbSingleton AmbSingleton;

    protected String userId;
    protected AmbassadorIdentification ambassadorIdentification;
    protected JsonObject deviceData;
    protected String webDeviceId;
    protected String facebookAccessToken;
    protected String twitterAccessToken;
    protected String linkedInAccessToken;
    protected String identifyData;

    @Nullable
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        save();
    }

    @Nullable
    public String getEmail() {
        return getAmbassadorIdentification().getEmail();
    }

    public void setEmail(String email) {
        getAmbassadorIdentification().setEmail(email);
        AmbSingleton.getInstance().getContext()
                .getSharedPreferences("user", Context.MODE_PRIVATE)
                .edit()
                .putString("email", email)
                .apply();
    }

    @NonNull
    public AmbassadorIdentification getAmbassadorIdentification() {
        if (ambassadorIdentification == null) {
            setAmbassadorIdentification(new AmbassadorIdentification());
        }
        return ambassadorIdentification;
    }

    public void setAmbassadorIdentification(AmbassadorIdentification ambassadorIdentification) {
        this.ambassadorIdentification = ambassadorIdentification;
        this.ambassadorIdentification.setOnChangeListener(new AmbassadorIdentification.OnChangeListener() {
            @Override
            public void change() {
                save();
            }
        });
        save();
    }

    @Nullable
    public JsonObject getPusherInfo() {
        return new JsonParser().parse(identifyData != null ? identifyData : "{}").getAsJsonObject();
    }

    public void setPusherInfo(JsonObject pusherInfo) {
        this.identifyData = pusherInfo.toString();
        save();

    }

    @Nullable
    public JsonObject getDeviceData() {
        return deviceData;
    }

    public void setDeviceData(JsonObject deviceData) {
        this.deviceData = deviceData;
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

    @Nullable
    public String getIdentifyData() {
        return identifyData;
    }

    public void setIdentifyData(String identifyData) {
        this.identifyData = identifyData;
        save();
    }

    /**
     * Serializes data into a JSON string and saves in SharedPreferences,
     * keyed on the user's email.
     */
    @Override
    public void save() {
        if (AmbSingleton.getInstance().getContext() != null) {
            String data = new Gson().toJson(this);
            SharedPreferences sharedPreferences =AmbSingleton.getInstance().getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(getAmbassadorIdentification().getEmail(), data).apply();
        }
    }

    @Override
    public void clear() {
        userId = null;
        ambassadorIdentification = null;
        deviceData = null;
        webDeviceId = null;
        facebookAccessToken = null;
        twitterAccessToken = null;
        linkedInAccessToken = null;
        identifyData = null;
    }

    /**
     * Clears the object and sets the data based on the currently saved
     * values for the current email address.
     */
    @Override
    public void refresh() {
        clear();
        String email =AmbSingleton.getInstance().getContext().getSharedPreferences("user", Context.MODE_PRIVATE).getString("email", null);

        if (email == null) return;

        String json =AmbSingleton.getInstance().getContext().getSharedPreferences("user", Context.MODE_PRIVATE).getString(email, null);

        if (json == null) return;

        User user = new Gson().fromJson(json, User.class);
        setUserId(user.getUserId());
        setAmbassadorIdentification(user.getAmbassadorIdentification());
        setDeviceData(user.getDeviceData());
        setWebDeviceId(user.getWebDeviceId());
        setFacebookAccessToken(user.getFacebookAccessToken());
        setTwitterAccessToken(user.getTwitterAccessToken());
        setLinkedInAccessToken(user.getLinkedInAccessToken());
        setIdentifyData(user.getIdentifyData());
    }
}
