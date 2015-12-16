package com.ambassador.ambassadorsdk.internal.api.identify;

import android.os.Handler;
import android.util.Log;

import com.ambassador.ambassadorsdk.internal.RequestManager;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.api.ServiceGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.augur.wintermute.Augur;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 *
 */
public class IdentifyApi {

    /** */
    private IdentifyClient identifyClient;

    public IdentifyApi() {
        this(true);
    }

    public IdentifyApi(boolean doInit) {
        if (doInit) {
            init();
        }
    }

    /**
     *
     */
    public void init() {
        identifyClient = ServiceGenerator.createService(IdentifyClient.class);
    }

    /**
     *
     * @param sessionId
     * @param requestId
     * @param uid
     * @param auth
     * @param request
     */
    public void identifyRequest(String sessionId, String requestId, String uid, String auth, IdentifyRequestBody request) {
        identifyClient.identifyRequest(sessionId, requestId, uid, auth, uid, request, new Callback<IdentifyRequestResponse>() {
            @Override
            public void success(IdentifyRequestResponse identifyRequestResponse, Response response) {
                // This should never happen, this request is not returning JSON so it hits the failure
            }

            @Override
            public void failure(RetrofitError error) {
                if (Utilities.isSuccessfulResponseCode(error.getResponse().getStatus())) {
                    // successful
                } else {
                    // unsuccessful
                }
            }
        });
    }

    /**
     *
     * @param sessionId
     * @param requestId
     * @param uid
     * @param auth
     * @param request
     * @param completion
     */
    public void updateNameRequest(String sessionId, String requestId, String uid, String auth, UpdateNameRequestBody request, final RequestManager.RequestCompletion completion) {
        identifyClient.updateNameRequest(sessionId, requestId, uid, auth, uid, request, new Callback<UpdateNameRequestResponse>() {
            @Override
            public void success(UpdateNameRequestResponse updateNameRequestResponse, Response response) {
                // This should never happen, this request is not returning JSON so it hits the failure
                completion.onSuccess("success");
            }

            @Override
            public void failure(RetrofitError error) {
                if (Utilities.isSuccessfulResponseCode(error.getResponse().getStatus())) {
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
     * @param completion
     */
    public void createPusherChannel(String uid, String auth, final RequestManager.RequestCompletion completion) {
        identifyClient.createPusherChannel(uid, auth, new Object(), new Callback<CreatePusherChannelResponse>() {
            @Override
            public void success(CreatePusherChannelResponse createPusherChannelResponse, Response response) {
                completion.onSuccess(createPusherChannelResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                completion.onFailure("failure");
            }
        });
    }

    /**
     *
     * @param url
     * @param completion
     */
    public void externalPusherRequest(final String url, final String uid, final String auth, final RequestManager.RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setDoInput(true);
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("MBSY_UNIVERSAL_ID", uid);
                    connection.setRequestProperty("Authorization", auth);
                    connection.setRequestProperty("Content-Type", "application/json");

                    final int responseCode = connection.getResponseCode();

                    InputStream iStream = null;
                    iStream = (Utilities.isSuccessfulResponseCode(responseCode)) ? connection.getInputStream() : connection.getErrorStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(iStream));
                    String line;
                    StringBuilder responseBuilder = new StringBuilder();

                    while ((line = rd.readLine()) != null) {
                        responseBuilder.append(line);
                    }

                    String response = responseBuilder.toString();

                    if (Utilities.isSuccessfulResponseCode(responseCode)) {
                        completion.onSuccess(response);
                    } else {
                        completion.onFailure(response);
                    }
                } catch (final IOException e) {
                    completion.onFailure("External PusherSDK Request failure due to IOException - " + e.getMessage());
                }
            }
        };
        new Thread(runnable).start();
    }

    /**
     *
     */
    public static class IdentifyRequestBody {

        private boolean enroll;
        private String campaign_id;
        private String email;
        private String source;
        private String mbsy_source;
        private String mbsy_cookie_code;
        private JsonObject fp;

        public IdentifyRequestBody(String campaign_id, String email, String augur) {
            this.enroll = true;
            this.campaign_id = campaign_id;
            this.email = email;
            this.source = "android_sdk_pilot";
            this.mbsy_source = "";
            this.mbsy_cookie_code = "";

            try {
                Gson gson = new Gson();
                fp = gson.fromJson(augur, JsonElement.class).getAsJsonObject();
            } catch (Exception e) {
                Utilities.debugLog("IdentifyRequest", "augurObject NULL");
            }
        }

    }

    /**
     *
     */
    public static class IdentifyRequestResponse {

    }

    /**
     *
     */
    public static class UpdateNameRequestBody {

        private String email;
        private UpdateBody update_body;

        public UpdateNameRequestBody(String email, String firstName, String lastName) {
            this.email = email;
            this.update_body = new UpdateBody(firstName, lastName);
        }

        public static class UpdateBody {

            private String first_name;
            private String last_name;

            public UpdateBody(String first_name, String last_name) {
                this.first_name = first_name;
                this.last_name = last_name;
            }

        }

    }

    /**
     *
     */
    public static class UpdateNameRequestResponse {
        
    }

    /**
     *
     */
    public static class CreatePusherChannelResponse {

        public String client_session_uid;
        public String expires_at;
        public String channel_name;

    }

}
