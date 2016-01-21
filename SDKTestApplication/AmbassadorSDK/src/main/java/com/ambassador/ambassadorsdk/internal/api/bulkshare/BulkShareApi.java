package com.ambassador.ambassadorsdk.internal.api.bulkshare;

import com.ambassador.ambassadorsdk.internal.RequestManager;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.api.ServiceGenerator;
import com.ambassador.ambassadorsdk.utils.ResponseCode;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Handles Ambassador API bulkshare methods using Retrofit services and contains all relevant pojo classes.
 */
public class BulkShareApi {

    /** Client for making BulkShare requests to the Ambassador API */
    private BulkShareClient bulkShareClient;

    /**
     * Default constructor.
     * Instantiates BulkShareApi and automatically initializes client.
     */
    public BulkShareApi() {
        this(true);
    }

    /**
     * Instantiates BulkShareApi optionally initializing client.
     * @param doInit whether or not to automatically initialize client.
     */
    public BulkShareApi(boolean doInit) {
        if (doInit) {
            init();
        }
    }

    /**
     * Generates and sets the client.
     */
    public void init() {
        setBulkShareClient(ServiceGenerator.createService(BulkShareClient.class));
    }

    /**
     * Sets the client for bulkshare requests
     * @param bulkShareClient an instantiation of BulkShareClient for this BulkShareApi to use
     */
    public void setBulkShareClient(BulkShareClient bulkShareClient) {
        this.bulkShareClient = bulkShareClient;
    }

    /**
     * Passes parameters to the BulkShareClient and handles retrofit callback
     * and calling back to RequestCompletion.
     * @param uid the Ambassador universal id
     * @param auth the Ambassador universal token
     * @param body the request body as a BulkShareSms object
     * @param completion callback for request completion
     */
    public void bulkShareSms(String uid, String auth, BulkShareSmsBody body, final RequestManager.RequestCompletion completion) {
        bulkShareClient.bulkShareSms(uid, auth, body, new Callback<BulkShareSmsResponse>() {
            @Override
            public void success(BulkShareSmsResponse bulkShareSmsResponse, Response response) {
                completion.onSuccess("success");
                Utilities.debugLog("amb-request", "SUCCESS: BulkShareApi.bulkShareSms(...)");
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse() != null && new ResponseCode(error.getResponse().getStatus()).isSuccessful()) {
                    completion.onSuccess("success");
                    Utilities.debugLog("amb-request", "SUCCESS: BulkShareApi.bulkShareSms(...)");
                } else {
                    completion.onFailure("failure");
                    Utilities.debugLog("amb-request", "FAILURE: BulkShareApi.bulkShareSms(...)");
                }
            }
        });
    }

    /**
     * Passes parameters to the BulkShareClient and handles retrofit callback
     * and calling back to RequestCompletion.
     * @param uid the Ambassador universal id
     * @param auth the Ambassador universal token
     * @param body the request body as a BulkShareEmailBody object
     * @param completion callback for request completion
     */
    public void bulkShareEmail(String uid, String auth, BulkShareEmailBody body, final RequestManager.RequestCompletion completion) {
        bulkShareClient.bulkShareEmail(uid, auth, body, new Callback<BulkShareEmailResponse>() {
            @Override
            public void success(BulkShareEmailResponse bulkShareEmailResponse, Response response) {
                completion.onSuccess("success");
                Utilities.debugLog("amb-request", "SUCCESS: BulkShareApi.bulkShareEmail(...)");
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse() != null && new ResponseCode(error.getResponse().getStatus()).isSuccessful()) {
                    completion.onSuccess("success");
                    Utilities.debugLog("amb-request", "SUCCESS: BulkShareApi.bulkShareEmail(...)");
                } else {
                    completion.onFailure("failure");
                    Utilities.debugLog("amb-request", "FAILURE: BulkShareApi.bulkShareEmail(...)");
                }
            }
        });
    }

    /**
     * Passes parameters to the BulkShareClient and handles retrofit callback
     * and calling back to RequestCompletion.
     * @param uid the Ambassador universal id
     * @param auth the Ambassador universal token
     * @param body the request body as a BulkShareTrackBody array
     */
    public void bulkShareTrack(String uid, String auth, BulkShareTrackBody[] body) {
        bulkShareClient.bulkShareTrack(uid, auth, body, new Callback<BulkShareTrackResponse[]>() {
            @Override
            public void success(BulkShareTrackResponse[] bulkShareTrackResponses, Response response) {
                Utilities.debugLog("amb-request", "SUCCESS: BulkShareApi.bulkShareTrack(...)");
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse() != null && new ResponseCode(error.getResponse().getStatus()).isSuccessful()) {
                    Utilities.debugLog("amb-request", "SUCCESS: BulkShareApi.bulkShareTrack(...)");
                } else {
                    Utilities.debugLog("amb-request", "FAILURE: BulkShareApi.bulkShareTrack(...)");
                }
            }
        });
    }

    /** Pojo for bulk share sms post request body */
    public static class BulkShareSmsBody {

        public String name;
        public String message;
        public String from_email;
        public String[] to;

        public BulkShareSmsBody(String name, String message, String from_email, String... to) {
            this.name = name;
            this.message = message;
            this.from_email = from_email;
            this.to = to;
        }

    }

    /** Pojo for bulk share sms post request response */
    public static class BulkShareSmsResponse {

        public String short_code;

    }

    /** Pojo for bulk share email post request body */
    public static class BulkShareEmailBody {

        public String subject_line;
        public String message;
        public String short_code;
        public String from_email;
        public String[] to_emails;

        public BulkShareEmailBody(String subject_line, String message, String short_code, String from_email, String... to_emails) {
            this.subject_line = subject_line;
            this.message = message;
            this.short_code = short_code;
            this.from_email = from_email;
            this.to_emails = to_emails;
        }

    }

    /** Pojo for bulk share email request response */
    public static class BulkShareEmailResponse {

        public String detail;

    }

    /** Pojo for bulk share track post request body */
    public static class BulkShareTrackBody {

        public String short_code;
        public String social_name;
        public String recipient_email;
        public String recipient_username;
        public String from_email;

        public BulkShareTrackBody(String short_code, String social_name, String recipient_email, String recipient_username, String from_email) {
            this.short_code = short_code;
            this.social_name = social_name;
            this.recipient_email = recipient_email;
            this.recipient_username = recipient_username;
            this.from_email = from_email;
        }

    }

    /** Pojo for bulk share track post request response */
    public static class BulkShareTrackResponse {

    }

}
