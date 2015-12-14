package com.ambassador.ambassadorsdk;

import android.util.Log;

/**
 * Created by dylan on 12/14/15.
 */
public class ConversionParametersBuilder {

    private static final String TAG = ConversionParametersBuilder.class.getSimpleName();

    public int mbsy_campaign = -1;
    public String mbsy_email;
    public String mbsy_first_name;
    public String mbsy_last_name;
    public int mbsy_email_new_ambassador;
    public String mbsy_uid;
    public String mbsy_custom1;
    public String mbsy_custom2;
    public String mbsy_custom3;
    public int mbsy_auto_create;
    public int mbsy_revenue = -1;
    public int mbsy_deactivate_new_ambassador;
    public String mbsy_transaction_uid;
    public String mbsy_add_to_group_id;
    public String mbsy_event_data1;
    public String mbsy_event_data2;
    public String mbsy_event_data3;
    public int mbsy_is_approved;

    private boolean revenueCheck = false, campaignCheck = false, emailCheck = false;

    public ConversionParametersBuilder() {}

    public ConversionParametersBuilder setCampaign(int mbsy_campaign) {
        this.mbsy_campaign = mbsy_campaign;
        this.campaignCheck = true;
        return this;
    }

    public ConversionParametersBuilder setEmail(String mbsy_email) {
        this.mbsy_email = mbsy_email;
        this.emailCheck = true;
        return this;
    }

    public ConversionParametersBuilder setFirstName(String mbsy_first_name) {
        this.mbsy_first_name = mbsy_first_name;
        return this;
    }

    public ConversionParametersBuilder setLastName(String mbsy_last_name) {
        this.mbsy_last_name = mbsy_last_name;
        return this;
    }

    public ConversionParametersBuilder setEmailNewAmbassador(int mbsy_email_new_ambassador) {
        this.mbsy_email_new_ambassador = mbsy_email_new_ambassador;
        return this;
    }

    public ConversionParametersBuilder setUid(String mbsy_uid) {
        this.mbsy_uid = mbsy_uid;
        return this;
    }

    public ConversionParametersBuilder setCustom1(String mbsy_custom1) {
        this.mbsy_custom1 = mbsy_custom1;
        return this;
    }

    public ConversionParametersBuilder setCustom2(String mbsy_custom2) {
        this.mbsy_custom2 = mbsy_custom2;
        return this;
    }

    public ConversionParametersBuilder setCustom3(String mbsy_custom3) {
        this.mbsy_custom3 = mbsy_custom3;
        return this;
    }

    public ConversionParametersBuilder setAutoCreate(int mbsy_auto_create) {
        this.mbsy_auto_create = mbsy_auto_create;
        return this;
    }

    public ConversionParametersBuilder setRevenue(int mbsy_revenue) {
        this.mbsy_revenue = mbsy_revenue;
        this.revenueCheck = true;
        return this;
    }

    public ConversionParametersBuilder setDeactivateNewAmbassador(int mbsy_deactivate_new_ambassador) {
        this.mbsy_deactivate_new_ambassador = mbsy_deactivate_new_ambassador;
        return this;
    }

    public ConversionParametersBuilder setTransactionUid(String mbsy_transaction_uid) {
        this.mbsy_transaction_uid = mbsy_transaction_uid;
        return this;
    }

    public ConversionParametersBuilder setAddToGroupId(String mbsy_add_to_group_id) {
        this.mbsy_add_to_group_id = mbsy_add_to_group_id;
        return this;
    }

    public ConversionParametersBuilder setEventData1(String mbsy_event_data1) {
        this.mbsy_event_data1 = mbsy_event_data1;
        return this;
    }

    public ConversionParametersBuilder setEventData2(String mbsy_event_data2) {
        this.mbsy_event_data2 = mbsy_event_data2;
        return this;
    }

    public ConversionParametersBuilder setEventData3(String mbsy_event_data3) {
        this.mbsy_event_data3 = mbsy_event_data3;
        return this;
    }

    public ConversionParametersBuilder setIsApproved(int mbsy_is_approved) {
        this.mbsy_is_approved = mbsy_is_approved;
        return this;
    }

    public ConversionParameters build() {
        validateRequiredFields();

        ConversionParameters out = new ConversionParameters();
        out.mbsy_campaign = mbsy_campaign;
        out.mbsy_email = mbsy_email;
        out.mbsy_first_name = mbsy_first_name;
        out.mbsy_last_name = mbsy_last_name;
        out.mbsy_email_new_ambassador = mbsy_email_new_ambassador;
        out.mbsy_uid = mbsy_uid;
        out.mbsy_custom1 = mbsy_custom1;
        out.mbsy_custom2 = mbsy_custom2;
        out.mbsy_custom3 = mbsy_custom3;
        out.mbsy_auto_create = mbsy_auto_create;
        out.mbsy_revenue = mbsy_revenue;
        out.mbsy_deactivate_new_ambassador = mbsy_deactivate_new_ambassador;
        out.mbsy_transaction_uid = mbsy_transaction_uid;
        out.mbsy_add_to_group_id = mbsy_add_to_group_id;
        out.mbsy_event_data1 = mbsy_event_data1;
        out.mbsy_event_data2 = mbsy_event_data2;
        out.mbsy_event_data3 = mbsy_event_data3;
        out.mbsy_is_approved = mbsy_is_approved;
        return out;
    }

    private void validateRequiredFields() {
        if (!revenueCheck || mbsy_revenue == -1) {
            Log.w(TAG, "Warning: you must set mbsy_revenue!");
        }
        if (!campaignCheck || mbsy_revenue == -1) {
            Log.w(TAG, "Warning: you must set mbsy_campaign!");
        }
        if (!emailCheck || mbsy_email == null) {
            Log.w(TAG, "Warning: you must set mbsy_email!");
        }
    }

}
