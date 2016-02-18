package com.ambassador.ambassadorsdk.internal.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.google.gson.Gson;

/**
 * Stores, serializes and unserializes data pertaining to an Ambassador campaign, and what
 * the SDK needs to present a RAF and provide proper functionality.
 */
public class Campaign implements Data {

    // region Fields
    protected String id;
    protected String url;
    protected String shortCode;
    protected String shareMessage;
    protected String emailSubject;
    protected String referredByShortCode;
    protected boolean convertedOnInstall;
    // endregion

    // region Getters / Setters
    @Nullable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        save();
        AmbSingleton.getInstanceContext()
                .getSharedPreferences("campaign", Context.MODE_PRIVATE)
                .edit()
                .putString("campaignId", id)
                .apply();
    }

    @Nullable
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        save();
    }

    @Nullable
    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
        save();
    }

    @Nullable
    public String getShareMessage() {
        return shareMessage;
    }

    public void setShareMessage(String shareMessage) {
        this.shareMessage = shareMessage;
        save();
    }

    @Nullable
    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
        save();
    }

    @Nullable
    public String getReferredByShortCode() {
        return referredByShortCode;
    }

    public void setReferredByShortCode(String referredByShortCode) {
        this.referredByShortCode = referredByShortCode;
        save();
    }

    public boolean isConvertedOnInstall() {
        return convertedOnInstall;
    }

    public void setConvertedOnInstall(boolean convertedOnInstall) {
        this.convertedOnInstall = convertedOnInstall;
        save();
    }
    // endregion

    // region Persistence methods
    /**
     * Serializes data into a JSON string and saves in SharedPreferences,
     * keyed on the campaign ID.
     */
    @Override
    public void save() {
        if (AmbSingleton.getInstanceContext() != null) {
            String data = new Gson().toJson(this);
            SharedPreferences sharedPreferences = AmbSingleton.getInstanceContext().getSharedPreferences("campaign", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(id, data).apply();
        }
    }

    /**
     * Clears instance data from this object's fields.
     */
    @Override
    public void clear() {
        id = null;
        url = null;
        shortCode = null;
        shareMessage = null;
        emailSubject = null;
        referredByShortCode = null;
        convertedOnInstall = false;
    }

    /**
     * Clears the object and sets the data based on the currently saved
     * values for the current campaign id.
     */
    @Override
    public void refresh() {
        clear();
        String campaignId = AmbSingleton.getInstanceContext().getSharedPreferences("campaign", Context.MODE_PRIVATE).getString("campaignId", null);

        if (campaignId == null) return;

        String json = AmbSingleton.getInstanceContext().getSharedPreferences("campaign", Context.MODE_PRIVATE).getString(campaignId, null);

        if (json == null) return;

        Campaign campaign = new Gson().fromJson(json, Campaign.class);
        setId(campaign.getId());
        setUrl(campaign.getUrl());
        setShortCode(campaign.getShortCode());
        setShareMessage(campaign.getShareMessage());
        setEmailSubject(campaign.getEmailSubject());
        setReferredByShortCode(campaign.getReferredByShortCode());
        setConvertedOnInstall(campaign.isConvertedOnInstall());
    }
    // endregion

}
