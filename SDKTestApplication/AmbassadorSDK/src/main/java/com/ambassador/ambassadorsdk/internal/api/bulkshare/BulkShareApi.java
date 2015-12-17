package com.ambassador.ambassadorsdk.internal.api.bulkshare;

import com.ambassador.ambassadorsdk.internal.RequestManager;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.api.ServiceGenerator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 *
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
     * @param bulkShareClient
     */
    public void setBulkShareClient(BulkShareClient bulkShareClient) {
        this.bulkShareClient = bulkShareClient;
    }

    /**
     *
     * @param uid
     * @param auth
     * @param body
     * @param completion
     */
    public void bulkShareSms(String uid, String auth, BulkShareSmsBody body, final RequestManager.RequestCompletion completion) {
        bulkShareClient.bulkShareSms(uid, auth, body, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                completion.onSuccess("success");
                Utilities.debugLog("amb-request", "SUCCESS: BulkShareApi.bulkShareSms(...)");
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse() != null && Utilities.isSuccessfulResponseCode(error.getResponse().getStatus())) {
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
     *
     * @param uid
     * @param auth
     * @param body
     * @param completion
     */
    public void bulkShareEmail(String uid, String auth, BulkShareEmailBody body, final RequestManager.RequestCompletion completion) {
        bulkShareClient.bulkShareEmail(uid, auth, body, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                completion.onSuccess("success");
                Utilities.debugLog("amb-request", "SUCCESS: BulkShareApi.bulkShareEmail(...)");
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse() != null && Utilities.isSuccessfulResponseCode(error.getResponse().getStatus())) {
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
     *
     * @param uid
     * @param auth
     * @param body
     */
    public void bulkShareTrack(String uid, String auth, BulkShareTrackBody[] body) {
        bulkShareClient.bulkShareTrack(uid, auth, body, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Utilities.debugLog("amb-request", "SUCCESS: BulkShareApi.bulkShareTrack(...)");
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse() != null && Utilities.isSuccessfulResponseCode(error.getResponse().getStatus())) {
                    Utilities.debugLog("amb-request", "SUCCESS: BulkShareApi.bulkShareTrack(...)");
                } else {
                    Utilities.debugLog("amb-request", "FAILURE: BulkShareApi.bulkShareTrack(...)");
                }
            }
        });
    }

    /**
     *
     */
    public static class BulkShareSmsBody {

        private String name;
        private String message;
        private String[] to;

        public BulkShareSmsBody(String name, String message, String... to) {
            this.name = name;
            this.message = message;
            this.to = to;
        }

    }

    /**
     *
     */
    public static class BulkShareEmailBody {

        private String subject_line;
        private String message;
        private String short_code;
        private String[] to_emails;

        public BulkShareEmailBody(String subject_line, String message, String short_code, String... to_emails) {
            this.subject_line = subject_line;
            this.message = message;
            this.short_code = short_code;
            this.to_emails = to_emails;
        }

    }

    /**
     *
     */
    public static class BulkShareTrackBody {

        private String short_code;
        private String social_name;
        private String recipient_email;
        private String recipient_username;

        public BulkShareTrackBody(String short_code, String social_name, String recipient_email, String recipient_username) {
            this.short_code = short_code;
            this.social_name = social_name;
            this.recipient_email = recipient_email;
            this.recipient_username = recipient_username;
        }

    }

}
