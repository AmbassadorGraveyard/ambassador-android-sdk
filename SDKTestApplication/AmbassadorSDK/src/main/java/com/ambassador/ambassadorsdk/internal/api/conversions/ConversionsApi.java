package com.ambassador.ambassadorsdk.internal.api.conversions;

import com.ambassador.ambassadorsdk.internal.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.RequestManager;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.api.ServiceGenerator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Handles Ambassador API conversion methods using Retrofit services and contains all relevant pojo classes.
 */
public class ConversionsApi {

    /** Client for making Conversions requests to the Ambassador API */
    public ConversionsClient conversionsClient;

    /**
     * Default constructor.
     * Instantiates ConversionsApi and automatically initializes client.
     */
    public ConversionsApi() {
        this(true);
    }

    /**
     * Optionally initializes client.
     * @param doInit whether or not to automatically initialize client.
     */
    public ConversionsApi(boolean doInit) {
        if (doInit) {
            init();
        }
    }

    /**
     * Instantiates and sets the Conversions client objects using the ServiceGenerator.
     */
    public void init() {
        setConversionsClient(ServiceGenerator.createService(ConversionsClient.class));
    }

    /**
     * Sets the client for conversions requests
     * @param conversionsClient an instantiation of ConversionsClient for this ConversionsApi to use
     */
    public void setConversionsClient(ConversionsClient conversionsClient) {
        this.conversionsClient = conversionsClient;
    }

    /**
     *
     * @param uid the Ambassador universal id
     * @param auth the Ambassador universal token
     * @param body the request body as a RegisterConversionRequestBody object
     * @param completion callback for request completion
     */
    public void registerConversionRequest(String uid, String auth, RegisterConversionRequestBody body, final RequestManager.RequestCompletion completion) {
        conversionsClient.registerConversionRequest(uid, auth, uid, body, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                completion.onSuccess("success");
                Utilities.debugLog("amb-request", "SUCCESS: ConversionsApi.registerConversionRequest(...)");
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse() != null && Utilities.isSuccessfulResponseCode(error.getResponse().getStatus())) {
                    completion.onSuccess("success");
                    Utilities.debugLog("amb-request", "SUCCESS: ConversionsApi.registerConversionRequest(...)");
                } else {
                    completion.onFailure("failure");
                    Utilities.debugLog("amb-request", "FAILURE: ConversionsApi.registerConversionRequest(...)");
                }
            }
        });
    }

    /** Pojo for register conversion post request body */
    public static class RegisterConversionRequestBody {

        private AugurObject fp;
        private FieldsObject fields;

        public RegisterConversionRequestBody(AugurObject fp, FieldsObject fields) {
            this.fp = fp;
            this.fields = fields;
        }

        public static class AugurObject {

            private ConsumerObject consumer;
            private DeviceObject device;

            public AugurObject(String UID, String type, String ID) {
                this.consumer = new ConsumerObject(UID);
                this.device = new DeviceObject(type, ID);
            }

            public static class ConsumerObject {

                private String UID;

                public ConsumerObject(String UID) {
                    this.UID = UID;
                }

            }

            public static class DeviceObject {

                private String type;
                private String ID;

                public DeviceObject(String type, String ID) {
                    this.type = type;
                    this.ID = ID;
                }

            }

        }

        public static class FieldsObject {

            private int mbsy_campaign;
            private String mbsy_email;
            private String mbsy_first_name;
            private String mbsy_last_name;
            private int mbsy_email_new_ambassador;
            private String mbsy_uid;
            private String mbsy_custom1;
            private String mbsy_custom2;
            private String mbsy_custom3;
            private int mbsy_auto_create;
            private int mbsy_revenue;
            private int mbsy_deactivate_new_ambassador;
            private String mbsy_transaction_uid;
            private String mbsy_add_to_group_id;
            private String mbsy_event_data1;
            private String mbsy_event_data2;
            private String mbsy_event_data3;
            private int mbsy_is_approved;
            private String mbsy_short_code;

            public FieldsObject(ConversionParameters parameters, String shortCode) {
                this.mbsy_campaign = parameters.mbsy_campaign;
                this.mbsy_email = parameters.mbsy_email;
                this.mbsy_first_name = parameters.mbsy_first_name;
                this.mbsy_last_name = parameters.mbsy_last_name;
                this.mbsy_email_new_ambassador = parameters.mbsy_email_new_ambassador;
                this.mbsy_uid = parameters.mbsy_uid;
                this.mbsy_custom1 = parameters.mbsy_custom1;
                this.mbsy_custom2 = parameters.mbsy_custom2;
                this.mbsy_custom3 = parameters.mbsy_custom3;
                this.mbsy_auto_create = parameters.mbsy_auto_create;
                this.mbsy_revenue = parameters.mbsy_revenue;
                this.mbsy_deactivate_new_ambassador = parameters.mbsy_deactivate_new_ambassador;
                this.mbsy_transaction_uid = parameters.mbsy_transaction_uid;
                this.mbsy_add_to_group_id = parameters.mbsy_add_to_group_id;
                this.mbsy_event_data1 = parameters.mbsy_event_data1;
                this.mbsy_event_data2 = parameters.mbsy_event_data2;
                this.mbsy_event_data3 = parameters.mbsy_event_data3;
                this.mbsy_is_approved = parameters.mbsy_is_approved;
                this.mbsy_short_code = shortCode;
            }

        }

    }

}
