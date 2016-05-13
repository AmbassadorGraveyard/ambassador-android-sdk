package com.ambassador.ambassadorsdk.internal.api.identify;

import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.ServiceGenerator;
import com.ambassador.ambassadorsdk.internal.identify.AmbassadorIdentification;
import com.ambassador.ambassadorsdk.internal.utils.ResponseCode;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Handles Ambassador API identify methods using Retrofit services and contains all relevant pojo classes.
 */
public final class IdentifyApi {

    /** Client for making Identify requests to the Ambassador API */
    private IdentifyClient identifyClient;

    /**
     * Default constructor.
     * Instantiates IdentifyApi and automatically initializes client.
     */
    @SuppressWarnings("unused")
    public IdentifyApi() {
        this(true);
    }

    /**
     * Optionally initializes client.
     * @param doInit whether or not to automatically initialize client.
     */
    public IdentifyApi(boolean doInit) {
        if (doInit) {
            init();
        }
    }

    /**
     * Instantiates and sets the Identify client objects using the ServiceGenerator.
     */
    public void init() {
        setIdentifyClient(ServiceGenerator.createService(IdentifyClient.class));
    }

    /**
     * Sets the client for identify requests
     * @param identifyClient an instantiation of IdentifyClient for this IdentifyApi to use
     */
    public void setIdentifyClient(IdentifyClient identifyClient) {
        this.identifyClient = identifyClient;
    }

    /**
     * Passes through to the identifyClient and handles the Retrofit callback.
     * @param sessionId the Pusher session id
     * @param requestId the Pusher request id
     * @param uid the Ambassador universal id
     * @param auth the Ambassador universal token
     * @param request the request body as an IdentifyRequestBody object
     */
    public void identifyRequest(String sessionId, String requestId, String uid, String auth, final IdentifyRequestBody request, final RequestManager.RequestCompletion requestCompletion) {
        identifyClient.identifyRequest(sessionId, requestId, uid, auth, uid, request, new Callback<IdentifyRequestResponse>() {
            @Override
            public void success(IdentifyRequestResponse identifyRequestResponse, Response response) {
                // This should never happen, this request is not returning JSON so it hits the failure
                Utilities.debugLog("amb-request", "SUCCESS: IdentifyApi.identifyRequest(...)");
                if (requestCompletion != null) requestCompletion.onSuccess(null);
            }

            @Override
            public void failure(RetrofitError error) {
                if (new ResponseCode(error.getResponse().getStatus()).isSuccessful()) {
                    // successful
                    Utilities.debugLog("amb-request", "SUCCESS: IdentifyApi.identifyRequest(...)");
                    if (requestCompletion != null) requestCompletion.onSuccess(null);
                } else {
                    // unsuccessful
                    Utilities.debugLog("amb-request", "FAILURE: IdentifyApi.identifyRequest(...)");
                    if (requestCompletion != null) requestCompletion.onFailure(null);
                }
            }
        });
    }

    /**
     * Passes through to the identifyClient and handles the Retrofit callback and
     * calling back to the RequestCompletion.
     * @param sessionId the Pusher session id
     * @param requestId the Pusher request id
     * @param uid the Ambassador universal id
     * @param auth the Ambassador universal token
     * @param request the request body as an UpdateNameRequestBody object
     * @param completion the callback for request completion
     */
    public void updateNameRequest(String sessionId, final String requestId, String uid, String auth, final UpdateNameRequestBody request, final RequestManager.RequestCompletion completion) {
        identifyClient.updateNameRequest(sessionId, requestId, uid, auth, uid, request, new Callback<UpdateNameRequestResponse>() {
            @Override
            public void success(UpdateNameRequestResponse updateNameRequestResponse, Response response) {
                // This should never happen, this request is not returning JSON so it hits the failure
                if (completion != null) completion.onSuccess(requestId);
                Utilities.debugLog("amb-request", "SUCCESS: IdentifyApi.updateNameRequest(...)");
            }

            @Override
            public void failure(RetrofitError error) {
                if (new ResponseCode(error.getResponse().getStatus()).isSuccessful()) {
                    if (completion != null) completion.onSuccess(requestId);
                    Utilities.debugLog("amb-request", "SUCCESS: IdentifyApi.updateNameRequest(...)");
                } else {
                    if (completion != null) completion.onFailure("failure");
                    Utilities.debugLog("amb-request", "FAILURE: IdentifyApi.updateNameRequest(...)");
                }
            }
        });
    }

    /**
     * Passes through to the identifyClient and handles the Retrofit callback and
     * calling back to the RequestCompletion.
     * @param sessionId the Pusher session id
     * @param requestId the Pusher request id
     * @param uid the Ambassador universal id
     * @param auth the Ambassador universal token
     * @param request the request body as an UpdateGcmTokenBody object
     * @param completion the callback for request completion
     */
    public void updateGcmToken(String sessionId, final String requestId, String uid, String auth, final UpdateGcmTokenBody request, final RequestManager.RequestCompletion completion) {
        identifyClient.updateGcmToken(sessionId, requestId, uid, auth, uid, request, new Callback<UpdateGcmTokenResponse>() {
            @Override
            public void success(UpdateGcmTokenResponse updateGcmTokenResponse, Response response) {
                // This should never happen, this request is not returning JSON so it hits the failure
                completion.onSuccess(requestId);
                Utilities.debugLog("amb-request", "SUCCESS: IdentifyApi.updateGcmToken(...)");
            }

            @Override
            public void failure(RetrofitError error) {
                if (new ResponseCode(error.getResponse().getStatus()).isSuccessful()) {
                    completion.onSuccess(requestId);
                    Utilities.debugLog("amb-request", "SUCCESS: IdentifyApi.updateGcmToken(...)");
                } else {
                    completion.onFailure("failure");
                    Utilities.debugLog("amb-request", "FAILURE: IdentifyApi.updateGcmToken(...)");
                }
            }
        });
    }

    /**
     * Passes through to the identifyClient and handles the Retrofit callback and
     * calling back to the RequestCompletion.
     * @param shortCode short code of the user to retrieve name + picture for.
     * @param uid ambassador universal id.
     * @param authKey ambassador universal token.
     * @param completion the callback for request completion
     */
    public void getUserFromShortCode(String shortCode, String uid, String authKey, final RequestManager.RequestCompletion completion) {
        identifyClient.getUserFromShortCode(uid, authKey, new GetUserFromShortCodeRequest(shortCode), new Callback<GetUserFromShortCodeResponse>() {
            @Override
            public void success(GetUserFromShortCodeResponse getUserFromShortCodeResponse, Response response) {
                completion.onSuccess(getUserFromShortCodeResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                completion.onFailure(null);
            }
        });
    }

    /**
     * Passes through to the identifyClient and handles the Retrofit callback and
     * calling back to the RequestCompletion.
     * @param uid the Ambassador universal id
     * @param auth the ambassador universal token
     * @param completion the callback for request completion
     */
    public void createPusherChannel(String uid, String auth, final RequestManager.RequestCompletion completion) {
        identifyClient.createPusherChannel(uid, auth, new Object(), new Callback<CreatePusherChannelResponse>() {
            @Override
            public void success(CreatePusherChannelResponse createPusherChannelResponse, Response response) {
                completion.onSuccess(createPusherChannelResponse);
                Utilities.debugLog("amb-request", "SUCCESS: IdentifyApi.createPusherChannel(...)");
            }

            @Override
            public void failure(RetrofitError error) {
                completion.onFailure("failure");
                Utilities.debugLog("amb-request", "FAILURE: IdentifyApi.createPusherChannel(...)");
            }
        });
    }

    /**
     * Not handled with Retrofit. Creates a runnable and runs it in a new thread.
     * Have to use low level connection stuff because this can hit any url.
     * @param url the String url to hit
     * @param completion callback for request completion
     */
    public void externalPusherRequest(final String url, final String uid, final String auth, final RequestManager.RequestCompletion completion) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handleExternalPusherRequest(url, uid, auth, completion);
            }
        };
        createThread(runnable).start();
    }

    /**
     * Creates a URL object for a String url
     * @param url String url to instantiate a URL object for
     * @return an instantiated URL object for the parameter
     * @throws MalformedURLException
     */
    protected URL createURL(String url) throws MalformedURLException {
        return new URL(url);
    }

    /**
     * Creates an HttpURLConnection object for a String url
     * @param url the string url to open a connection to
     * @return an instantiated HttpURLConnection object for the parameter
     * @throws IOException
     */
    protected HttpURLConnection createConnection(String url) throws IOException {
        return (HttpURLConnection) createURL(url).openConnection();
    }

    /**
     * Handles the logic of externalPusherRequest, called within runnable
     * @param url the String url to hit
     * @param uid the universalId identifier
     * @param auth the universalToken identifier
     * @param completion callback for request completion
     */
    protected void handleExternalPusherRequest(String url, String uid, String auth, RequestManager.RequestCompletion completion) {
        HttpURLConnection connection = null;
        try {
            connection = createConnection(url);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("MBSY_UNIVERSAL_ID", uid);
            connection.setRequestProperty("Authorization", auth);
            connection.setRequestProperty("Content-Type", "application/json");

            final int responseCode = connection.getResponseCode();

            InputStream iStream = null;
            iStream = (new ResponseCode(responseCode).isSuccessful()) ? connection.getInputStream() : connection.getErrorStream();
            BufferedReader rd = createBufferedReader(iStream);
            String line;
            StringBuilder responseBuilder = new StringBuilder();

            while ((line = rd.readLine()) != null) {
                responseBuilder.append(line);
            }

            String response = responseBuilder.toString();

            if (new ResponseCode(responseCode).isSuccessful()) {
                completion.onSuccess(response);
                Utilities.debugLog("amb-request", "SUCCESS: IdentifyApi.externalPusherRequest(...)");
            } else {
                completion.onFailure(response);
                Utilities.debugLog("amb-request", "FAILURE: IdentifyApi.externalPusherRequest(...)");
            }
        } catch (final IOException e) {
            completion.onFailure("External PusherSDK Request failure due to IOException - " + e.getMessage());
            Utilities.debugLog("amb-request", "FAILURE: IdentifyApi.externalPusherRequest(...)");
        }
    }

    /**
     * Creates a Thread object for a Runnable
     * @param runnable the runnable to place on the thread
     * @return the instantiated Runnable object
     */
    protected Thread createThread(Runnable runnable) {
        return new Thread(runnable);
    }

    /**
     * Opens a BufferedReader for an InputStream
     * @param is the InputStream to open a BufferedReader for
     * @return the opened BufferedReader
     */
    protected BufferedReader createBufferedReader(InputStream is) {
        return new BufferedReader(new InputStreamReader(is));
    }

    /**
     * Passes through to the identifyClient and handles the Retrofit callback and
     * calling back to the RequestCompletion.
     * @param uid the universalId identifier.
     * @param auth the universalToken identifier.
     * @param completion callback for request completion.
     */
    public void getCompanyInfo(String uid, String auth, final RequestManager.RequestCompletion completion) {
        identifyClient.getCompanyInfo(uid, auth, new Callback<GetCompanyInfoResponse>() {
            @Override
            public void success(GetCompanyInfoResponse getCompanyInfoResponse, Response response) {
                completion.onSuccess(getCompanyInfoResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                completion.onFailure(null);
            }
        });
    }

    /**
     * Gets the Envoy id and secret to do social OAuth stuff.
     * @param uid the universalId identifier.
     * @param auth the universalToken identifier.
     * @param companyUid the id for the company on the backend.
     * @param completion callback for request completion.
     */
    public void getEnvoyKeys(String uid, String auth, String companyUid, final RequestManager.RequestCompletion completion) {
        identifyClient.getEnvoyKeys(uid, auth, companyUid, new Callback<GetEnvoyKeysResponse>() {
            @Override
            public void success(GetEnvoyKeysResponse getEnvoyKeysResponse, Response response) {
                completion.onSuccess(getEnvoyKeysResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                completion.onFailure(null);
            }
        });
    }

    /** Pojo for identify post request body */
    public static class IdentifyRequestBody {

        private boolean enroll;
        private String campaign_id;
        private String source;
        private String mbsy_source;
        private String mbsy_cookie_code;
        private JsonObject fp;

        private String remote_user_id;

        private String email;
        private String first_name;
        private String last_name;
        private String custom1;
        private String custom2;
        private String custom3;
        private String company;
        private String street;
        private String city;
        private String state;
        private String zip;
        private String country;

        public IdentifyRequestBody(String campaign_id, String userId, String augur, AmbassadorIdentification ambassadorIdentification) {
            this.enroll = true;
            this.campaign_id = campaign_id;
            this.source = "android_sdk_pilot";
            this.mbsy_source = "";
            this.mbsy_cookie_code = "";
            this.remote_user_id = userId;

            this.email = ambassadorIdentification.getEmail();
            this.first_name = ambassadorIdentification.getFirstName();
            this.last_name = ambassadorIdentification.getLastName();
            this.custom1 = ambassadorIdentification.getCustomLabel1();
            this.custom2 = ambassadorIdentification.getCustomLabel2();
            this.custom3 = ambassadorIdentification.getCustomLabel3();
            this.company = ambassadorIdentification.getCompany();
            this.street = ambassadorIdentification.getStreet();
            this.city = ambassadorIdentification.getCity();
            this.state = ambassadorIdentification.getState();
            this.zip = ambassadorIdentification.getPostalCode();
            this.country = ambassadorIdentification.getCountry();

            try {
                Gson gson = new Gson();
                fp = gson.fromJson(augur, JsonElement.class).getAsJsonObject();
            } catch (Exception e) {
                Utilities.debugLog("IdentifyRequest", "augurObject NULL");
            }
        }

    }

    /** Pojo for identify post request response */
    public static class IdentifyRequestResponse {



    }

    /** Pojo for update name post request body */
    public static class UpdateNameRequestBody {

        private String email;
        private UpdateBody update_data;

        public UpdateNameRequestBody(String email, String firstName, String lastName) {
            this.email = email;
            this.update_data = new UpdateBody(firstName, lastName);
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

    /** */
    public static class UpdateNameRequestResponse {
        
    }

    /** Pojo for update name post request body */
    public static class UpdateGcmTokenBody {

        private String email;
        private UpdateBody update_data;

        public UpdateGcmTokenBody(String email, String gcmToken) {
            this.email = email;
            this.update_data = new UpdateBody(gcmToken);
        }

        public static class UpdateBody {

            private String gcm_token;

            public UpdateBody(String gcm_token) {
                this.gcm_token = gcm_token;
            }

        }

    }

    /** */
    public static class UpdateGcmTokenResponse {

    }

    /** Pojo for get user from short code request body */
    public static class GetUserFromShortCodeRequest {

        private String short_code;

        public GetUserFromShortCodeRequest(String short_code) {
            this.short_code = short_code;
        }

    }

    /** Pojo for get user from short code request response */
    public static class GetUserFromShortCodeResponse {

        public String name;
        public String avatar_url;

    }

    /** Pojo for create pusher channel response */
    public static class CreatePusherChannelResponse {

        public String client_session_uid;
        public String expires_at;
        public String channel_name;

    }

    /** Pojo for get company info response */
    public static class GetCompanyInfoResponse {

        public Result[] results;

        public static class Result {

            public String uid;
            public String url;

        }

    }

    /** Pojo for get envoy keys response */
    public static class GetEnvoyKeysResponse {

        public String envoy_client_id;
        public String envoy_client_secret;

    }

}
