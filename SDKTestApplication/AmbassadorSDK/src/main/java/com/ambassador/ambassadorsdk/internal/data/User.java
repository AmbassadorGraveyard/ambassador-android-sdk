package com.ambassador.ambassadorsdk.internal.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.models.AugurData;
import com.google.gson.Gson;


/**
 * Stores, serializes and unserializes data pertaining to an Ambassador identified user,
 * and what the backend needs to register conversions and track shares.
 */
public class User {

    protected String firstName;
    protected String lastName;
    protected String email;
    protected String gcmToken;
    protected AugurData augurData;

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
    }

}
