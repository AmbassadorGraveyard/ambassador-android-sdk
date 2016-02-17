package com.ambassador.ambassadorsdk.internal.api.linkedin;

import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.api.ServiceGenerator;

import java.nio.charset.Charset;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;

/**
 * Handles LinkedIn API using Retrofit services and contains all relevant pojo classes.
 */
public final class LinkedInApi {

    /** Client for making authenticated requests such as posting */
    private LinkedInClient linkedInClient;

    /** Client for making unauthenticated requests such as logging in */
    private LinkedInAuthClient linkedInAuthClient;

    /**
     * Default constructor.
     * Instantiates BulkShareApi and automatically initializes client.
     */
    @SuppressWarnings("unused")
    public LinkedInApi() {
        this(true);
    }

    /**
     * Optionally initializes clients.
     * @param doInit whether or not to initialize clients automatically.
     */
    public LinkedInApi(boolean doInit) {
        if (doInit) {
            init();
        }
    }

    /**
     * Instantiates and sets the LinkedIn client objects using the ServiceGenerator.
     */
    public void init() {
        setLinkedInClient(ServiceGenerator.createService(LinkedInClient.class));
        setLinkedInAuthClient(ServiceGenerator.createService(LinkedInAuthClient.class));
    }

    /**
     * Sets the client for authenticated requests
     * @param linkedInClient an instantiation of LinkedInClient for this LinkedInApi to use
     */
    public void setLinkedInClient(LinkedInClient linkedInClient) {
        this.linkedInClient = linkedInClient;
    }

    /**
     * Sets the client for un-authenticated requests
     * @param linkedInAuthClient an instantiation of LinkedInAuthClient for this LinkedInApi to use
     */
    public void setLinkedInAuthClient(LinkedInAuthClient linkedInAuthClient) {
        this.linkedInAuthClient = linkedInAuthClient;
    }

    /**
     * Obtains an access token from the LinkedIn api and stores it in shared preferences.
     * @param urlParams the form url encoded request body with the request code and some other stuff
     * @param completion callback interface for request completion
     * @param listener callback interface for a valid access token being obtained
     */
    public void login(String urlParams, final RequestManager.RequestCompletion completion, final RequestManager.LinkedInAuthorizedListener listener) {
        TypedInput requestBody = new TypedByteArray("application/x-www-form-urlencoded", urlParams.getBytes(Charset.forName("UTF-8")));
        linkedInAuthClient.login(requestBody, new Callback<LinkedInLoginResponse>() {
            @Override
            public void success(LinkedInLoginResponse linkedInLoginResponse, Response response) {
                String accessToken = linkedInLoginResponse.access_token;
                if (accessToken != null) {
                    completion.onSuccess(accessToken);
                    listener.linkedInAuthorized(accessToken);
                    Utilities.debugLog("amb-request", "SUCCESS: LinkedInApi.login(...)");
                } else {
                    completion.onFailure("failure");
                    Utilities.debugLog("amb-request", "FAILURE: LinkedInApi.login(...)");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                completion.onFailure("failure");
            }
        });
    }

    /**
     * Shares a post to LinkedIn.
     * @param token the user's access token
     * @param requestBody the pojo containing the comment and visibility rule
     * @param completion callback interface for request completion
     */
    public void post(String token, LinkedInApi.LinkedInPostRequest requestBody, final RequestManager.RequestCompletion completion) {
        linkedInClient.post("Bearer " + token, requestBody, new Callback<Object>() {
            @Override
            public void success(Object linkedInPostResponse, Response response) {
                completion.onSuccess("success");
                Utilities.debugLog("amb-request", "SUCCESS: LinkedInApi.post(...)");
            }

            @Override
            public void failure(RetrofitError error) {
                completion.onFailure("failure");
                Utilities.debugLog("amb-request", "FAILURE: LinkedInApi.post(...)");
            }
        });
    }

    /**
     * Obtains profile info from LinkedIn to verify access token validity.
     * @param token the user's access token
     * @param completion callback interface for request completion
     */
    public void getProfile(String token, final RequestManager.RequestCompletion completion) {
        linkedInClient.getProfile("Bearer " + token, new retrofit.Callback<LinkedInApi.LinkedInProfileResponse>() {
            @Override
            public void success(LinkedInApi.LinkedInProfileResponse linkedInProfileResponse, Response response) {
                if (linkedInProfileResponse.firstName != null && linkedInProfileResponse.lastName != null) {
                    completion.onSuccess("success");
                    Utilities.debugLog("amb-request", "SUCCESS: LinkedInApi.getProfile(...)");
                } else {
                    completion.onFailure("failure");
                    Utilities.debugLog("amb-request", "FAILURE: LinkedInApi.getProfile(...)");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                completion.onFailure("failure");
                Utilities.debugLog("amb-request", "FAILURE: LinkedInApi.getProfile(...)");
            }
        });
    }

    /** Pojo for login request response */
    public static class LinkedInLoginResponse {

        public String access_token;

    }

    /** Pojo for post request body */
    public static class LinkedInPostRequest {

        public String comment;
        public Visibility visibility;

        public LinkedInPostRequest(String comment) {
            this.comment = comment;
            this.visibility =  new Visibility("anyone");
        }

        public static class Visibility {

            public String code;

            public Visibility(String code) {
                this.code = code;
            }

        }

    }

    /** Pojo for profile request response */
    public static class LinkedInProfileResponse {

        public String firstName;
        public String lastName;

    }

}