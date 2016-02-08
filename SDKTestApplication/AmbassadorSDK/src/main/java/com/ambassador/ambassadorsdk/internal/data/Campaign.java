package com.ambassador.ambassadorsdk.internal.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.google.gson.Gson;

/**
 * Stores, serializes and unserializes data pertaining to an Ambassador campaign, and what
 * the SDK needs to present a RAF and provide proper functionality.
 */
public class Campaign {

    protected String id;
    protected String shortCode;
    protected String shareMessage;
    protected String emailSubject;
    protected String referredByShortCode;

    /**
     * Serializes data into a JSON string and saves in SharedPreferences,
     * keyed on the campaign ID.
     */
    public void save() {
        if (AmbassadorSingleton.getInstanceContext() != null) {
            String data = new Gson().toJson(this);
            SharedPreferences sharedPreferences = AmbassadorSingleton.getInstanceContext().getSharedPreferences("campaign", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(id, data).apply();
        }
    }

    /**
     * Clears instance data from this object's fields.
     */
    public void clear() {
        id = null;
        shortCode = null;
        shareMessage = null;
        emailSubject = null;
        referredByShortCode = null;
    }

}
