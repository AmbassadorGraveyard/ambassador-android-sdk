package com.ambassador.ambassadorsdk;

/**
 * Created by JakeDunahee on 8/21/15.
 */
public class ConversionParameters {
    public int mbsy_campaign;
    public String mbsy_email;
    public String mbsy_first_name;
    public String mbsy_last_name;
    public int mbsy_email_new_ambassador;
    public String mbsy_uid;
    public String mbsy_custom1;
    public String mbsy_custom2;
    public String mbsy_custom3;
    public int mbsy_auto_create;
    public int mbsy_revenue;
    public int mbsy_deactivate_new_ambassador;
    public String mbsy_transaction_uid;
    public String mbsy_add_to_group_id;
    public String mbsy_event_data1;
    public String mbsy_event_data2;
    public String mbsy_event_data3;
    public int mbsy_is_approved;

    // Sets up default constructor for parameters
    public ConversionParameters() {
        mbsy_campaign = -1;
        mbsy_email = "";
        mbsy_first_name = "";
        mbsy_last_name = "";
        mbsy_email_new_ambassador = 0;
        mbsy_uid = "";
        mbsy_custom1 = "";
        mbsy_custom2 = "";
        mbsy_custom3 = "";
        mbsy_auto_create = 1;
        mbsy_revenue = -1;
        mbsy_deactivate_new_ambassador = 0;
        mbsy_transaction_uid = "";
        mbsy_add_to_group_id = "";
        mbsy_event_data1 = "";
        mbsy_event_data2 = "";
        mbsy_event_data3 = "";
        mbsy_is_approved = 1;
    }

    // Boolean that checks if the conversion parameters are valid
    boolean isValid() {
        return mbsy_campaign > -1 && !mbsy_email.equals("") && mbsy_revenue > -1;
    }
}