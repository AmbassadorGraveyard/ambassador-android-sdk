package com.ambassador.ambassadorsdk.internal.api.bulkshare;

import android.util.Log;

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

    /** */
    private static BulkShareClient bulkShareClient;

    /**
     *
     */
    public static void init() {
        bulkShareClient = ServiceGenerator.createService(BulkShareClient.class);
    }

    /**
     *
     * @param uid
     * @param auth
     * @param body
     * @param completion
     */
    public static void bulkShareSms(String uid, String auth, BulkShareSmsBody body, final RequestManager.RequestCompletion completion) {
        bulkShareClient.bulkShareSms(uid, auth, body, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                completion.onSuccess("success");
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse() != null && Utilities.isSuccessfulResponseCode(error.getResponse().getStatus())) {
                    completion.onSuccess("success");
                } else {
                    completion.onFailure("failure");
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
    public static void bulkShareEmail(String uid, String auth, BulkShareEmailBody body, final RequestManager.RequestCompletion completion) {
        bulkShareClient.bulkShareEmail(uid, auth, body, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                completion.onSuccess("success");
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse() != null && Utilities.isSuccessfulResponseCode(error.getResponse().getStatus())) {
                    completion.onSuccess("success");
                } else {
                    completion.onFailure("failure");
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
    public static void bulkShareTrack(String uid, String auth, BulkShareTrackBody[] body) {
        bulkShareClient.bulkShareTrack(uid, auth, body, new Callback<String>() {
            @Override
            public void success(String s, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

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
