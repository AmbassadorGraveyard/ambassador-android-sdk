package com.ambassador.ambassadorsdk.internal.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.google.gson.Gson;

import org.json.JSONObject;


/**
 * Stores, serializes and unserializes data pertaining to an Ambassador identified user,
 * and what the backend needs to register conversions and track shares.
 */
public class User {

    // region Fields
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String gcmToken;
    protected JSONObject pusherInfo;
    protected JSONObject augurData;
    protected String webDeviceId;
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
    }

    @Nullable
    public String getGcmToken() {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        this.gcmToken = gcmToken;
        save();
    }

    @Nullable
    public JSONObject getPusherInfo() {
        return pusherInfo;
    }

    public void setPusherInfo(JSONObject pusherInfo) {
        this.pusherInfo = pusherInfo;
        save();
    }

    @Nullable
    public JSONObject getAugurData() {
        return augurData;
    }

    public void setAugurData(JSONObject augurData) {
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
    // endregion

    // region Persistence methods
    /**
     * Serializes data into a JSON string and saves in SharedPreferences,
     * keyed on the user's email.
     */
    public void save() {
        if (AmbassadorSingleton.getInstanceContext() != null) {
            String data = new Gson().toJson(this);
            SharedPreferences sharedPreferences = AmbassadorSingleton.getInstanceContext().getSharedPreferences("user", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(email, data).apply();
        }
    }

    /**
     * Clears instance data from this object's fields.
     */
    public void clear() {
        firstName = null;
        lastName = null;
        email = null;
        gcmToken = null;
        augurData = null;
        webDeviceId = null;
    }

    /**
     * Clears the object and sets the data based on the currently saved
     * values for the current email address.
     */
    public void refresh() {
        clear();
        String email = AmbassadorSingleton.getInstanceContext().getSharedPreferences("user", Context.MODE_PRIVATE).getString("email", null);

        if (email == null) return;

        String json = AmbassadorSingleton.getInstanceContext().getSharedPreferences("user", Context.MODE_PRIVATE).getString(email, null);

        if (json == null) return;

        User user = new Gson().fromJson(json, User.class);
        setFirstName(user.getFirstName());
        setLastName(user.getLastName());
        setEmail(user.getEmail());
        setGcmToken(user.getGcmToken());
        setAugurData(user.getAugurData());
        setWebDeviceId(user.getWebDeviceId());
    }
    // endregion

}
