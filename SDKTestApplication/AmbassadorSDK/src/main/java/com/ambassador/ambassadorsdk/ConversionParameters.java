package com.ambassador.ambassadorsdk;

import android.util.Log;

import com.ambassador.ambassadorsdk.internal.api.conversions.ConversionsApi;

public class ConversionParameters {

    public int campaign;
    public String email;
    public String firstName;
    public String lastName;
    public int emailNewAmbassador;
    public String uid;
    public String custom1;
    public String custom2;
    public String custom3;
    public int autoCreate;
    public int revenue;
    public int deactivateNewAmbassador;
    public String transactionUid;
    public String addToGroupId;
    public String eventData1;
    public String eventData2;
    public String eventData3;
    public int isApproved;
    private String shortCode;

    public int getCampaign() {
        return campaign;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getEmailNewAmbassador() {
        return emailNewAmbassador;
    }

    public String getUid() {
        return uid;
    }

    public String getCustom1() {
        return custom1;
    }

    public String getCustom2() {
        return custom2;
    }

    public String getCustom3() {
        return custom3;
    }

    public int getAutoCreate() {
        return autoCreate;
    }

    public int getRevenue() {
        return revenue;
    }

    public int getDeactivateNewAmbassador() {
        return deactivateNewAmbassador;
    }

    public String getTransactionUid() {
        return transactionUid;
    }

    public String getAddToGroupId() {
        return addToGroupId;
    }

    public String getEventData1() {
        return eventData1;
    }

    public String getEventData2() {
        return eventData2;
    }

    public String getEventData3() {
        return eventData3;
    }

    public int getIsApproved() {
        return isApproved;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void updateShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public ConversionParameters() {
        this.campaign = -1;
        this.email = "";
        this.firstName = "";
        this.lastName = "";
        this.emailNewAmbassador = 0;
        this.uid = "";
        this.custom1 = "";
        this.custom2 = "";
        this.custom3 = "";
        this.autoCreate = 1;
        this.revenue = -1;
        this.deactivateNewAmbassador = 0;
        this.transactionUid = "";
        this.addToGroupId = "";
        this.eventData1 = "";
        this.eventData2 = "";
        this.eventData3 = "";
        this.isApproved = 1;
        this.shortCode = "";
    }

    public boolean isValid() {
        return campaign > -1 && !email.equals("") && revenue > -1;
    }

    public ConversionsApi.RegisterConversionRequestBody.FieldsObject getFieldsObject() {
        return new ConversionsApi.RegisterConversionRequestBody.FieldsObject(this, shortCode);
    }

    public static class Builder {

        private static final String TAG = Builder.class.getSimpleName();

        private int campaign = -1;
        private String email;
        private String firstName;
        private String lastName;
        private int mbsy_email_new_ambassador;
        private String mbsy_uid;
        private String mbsy_custom1;
        private String mbsy_custom2;
        private String mbsy_custom3;
        private int mbsy_auto_create;
        private int mbsy_revenue = -1;
        private int mbsy_deactivate_new_ambassador;
        private String mbsy_transaction_uid;
        private String mbsy_add_to_group_id;
        private String mbsy_event_data1;
        private String mbsy_event_data2;
        private String mbsy_event_data3;
        private int mbsy_is_approved;

        private boolean revenueCheck = false;
        private boolean campaignCheck = false;
        private boolean emailCheck = false;

        public Builder() {}

        public Builder setCampaign(int mbsy_campaign) {
            this.campaign = mbsy_campaign;
            this.campaignCheck = true;
            return this;
        }

        public Builder setEmail(String mbsy_email) {
            this.email = mbsy_email;
            this.emailCheck = true;
            return this;
        }

        public Builder setFirstName(String mbsy_first_name) {
            this.firstName = mbsy_first_name;
            return this;
        }

        public Builder setLastName(String mbsy_last_name) {
            this.lastName = mbsy_last_name;
            return this;
        }

        public Builder setEmailNewAmbassador(int mbsy_email_new_ambassador) {
            this.mbsy_email_new_ambassador = mbsy_email_new_ambassador;
            return this;
        }

        public Builder setUid(String mbsy_uid) {
            this.mbsy_uid = mbsy_uid;
            return this;
        }

        public Builder setCustom1(String mbsy_custom1) {
            this.mbsy_custom1 = mbsy_custom1;
            return this;
        }

        public Builder setCustom2(String mbsy_custom2) {
            this.mbsy_custom2 = mbsy_custom2;
            return this;
        }

        public Builder setCustom3(String mbsy_custom3) {
            this.mbsy_custom3 = mbsy_custom3;
            return this;
        }

        public Builder setAutoCreate(int mbsy_auto_create) {
            this.mbsy_auto_create = mbsy_auto_create;
            return this;
        }

        public Builder setRevenue(int mbsy_revenue) {
            this.mbsy_revenue = mbsy_revenue;
            this.revenueCheck = true;
            return this;
        }

        public Builder setDeactivateNewAmbassador(int mbsy_deactivate_new_ambassador) {
            this.mbsy_deactivate_new_ambassador = mbsy_deactivate_new_ambassador;
            return this;
        }

        public Builder setTransactionUid(String mbsy_transaction_uid) {
            this.mbsy_transaction_uid = mbsy_transaction_uid;
            return this;
        }

        public Builder setAddToGroupId(String mbsy_add_to_group_id) {
            this.mbsy_add_to_group_id = mbsy_add_to_group_id;
            return this;
        }

        public Builder setEventData1(String mbsy_event_data1) {
            this.mbsy_event_data1 = mbsy_event_data1;
            return this;
        }

        public Builder setEventData2(String mbsy_event_data2) {
            this.mbsy_event_data2 = mbsy_event_data2;
            return this;
        }

        public Builder setEventData3(String mbsy_event_data3) {
            this.mbsy_event_data3 = mbsy_event_data3;
            return this;
        }

        public Builder setIsApproved(int mbsy_is_approved) {
            this.mbsy_is_approved = mbsy_is_approved;
            return this;
        }

        public ConversionParameters build() {
            validateRequiredFields();

            ConversionParameters out = new ConversionParameters();
            out.campaign = campaign;
            out.email = email;
            out.firstName = firstName;
            out.lastName = lastName;
            out.emailNewAmbassador = mbsy_email_new_ambassador;
            out.uid = mbsy_uid;
            out.custom1 = mbsy_custom1;
            out.custom2 = mbsy_custom2;
            out.custom3 = mbsy_custom3;
            out.autoCreate = mbsy_auto_create;
            out.revenue = mbsy_revenue;
            out.deactivateNewAmbassador = mbsy_deactivate_new_ambassador;
            out.transactionUid = mbsy_transaction_uid;
            out.addToGroupId = mbsy_add_to_group_id;
            out.eventData1 = mbsy_event_data1;
            out.eventData2 = mbsy_event_data2;
            out.eventData3 = mbsy_event_data3;
            out.isApproved = mbsy_is_approved;

            return out;
        }

        private void validateRequiredFields() {
            if (!revenueCheck || mbsy_revenue == -1) {
                Log.w(TAG, "Warning: you must set revenue!");
            }
            if (!campaignCheck || campaign == -1) {
                Log.w(TAG, "Warning: you must set campaign!");
            }
            if (!emailCheck || email == null) {
                Log.w(TAG, "Warning: you must set email!");
            }
        }

    }

}
