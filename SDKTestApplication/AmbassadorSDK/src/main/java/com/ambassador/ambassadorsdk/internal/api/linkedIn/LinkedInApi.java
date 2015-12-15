package com.ambassador.ambassadorsdk.internal.api.linkedIn;

import com.ambassador.ambassadorsdk.internal.RequestManager;
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
public class LinkedInApi {

    /** Client for making authenticated requests such as posting */
    private static LinkedInClient linkedInClient;

    /** Client for making unauthenticated requests such as logging in */
    private static LinkedInAuthClient linkedInAuthClient;

    /**
     * Instantiates the LinkedIn client objects using the ServiceGenerator.
     */
    public static void init() {
        linkedInClient = ServiceGenerator.createService(LinkedInClient.class);
        linkedInAuthClient = ServiceGenerator.createService(LinkedInAuthClient.class);
    }

    /**
     * Obtains an access token from the LinkedIn api and stores it in shared preferences.
     * @param urlParams the form url encoded request body with the request code and some other stuff
     * @param completion callback interface for request completion
     * @param listener callback interface for a valid access token being obtained
     */
    public static void login(String urlParams, final RequestManager.RequestCompletion completion, final RequestManager.LinkedInAuthorizedListener listener) {
        TypedInput requestBody = new TypedByteArray("application/x-www-form-urlencoded", urlParams.getBytes(Charset.forName("UTF-8")));
        linkedInAuthClient.login(requestBody, new Callback<LinkedInLoginResponse>() {
            @Override
            public void success(LinkedInLoginResponse linkedInLoginResponse, Response response) {
                String accessToken = linkedInLoginResponse.access_token;
                if (accessToken != null) {
                    completion.onSuccess(accessToken);
                    listener.linkedInAuthorized(accessToken);
                } else {
                    completion.onFailure("failure");
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
    public static void post(String token, LinkedInApi.LinkedInPostRequest requestBody, final RequestManager.RequestCompletion completion) {
        linkedInClient.post("Bearer " + token, requestBody, new retrofit.Callback<Object>() {
            @Override
            public void success(Object linkedInPostResponse, Response response) {
                completion.onSuccess("success");
            }

            @Override
            public void failure(RetrofitError error) {
                completion.onFailure("failure");
            }
        });
    }

    /**
     * Obtains profile info from LinkedIn to verify access token validity.
     * @param token the user's access token
     * @param completion callback interface for request completion
     */
    public static void getProfile(String token, final RequestManager.RequestCompletion completion) {
        linkedInClient.getProfile("Bearer " + token, new retrofit.Callback<LinkedInApi.LinkedInProfileResponse>() {
            @Override
            public void success(LinkedInApi.LinkedInProfileResponse linkedInProfileResponse, Response response) {
                if (linkedInProfileResponse.firstName != null && linkedInProfileResponse.lastName != null) {
                    completion.onSuccess("success");
                } else {
                    completion.onFailure("failure");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                completion.onFailure("failure");
            }
        });
    }

    /** Pojo for login request response */
    public static class LinkedInLoginResponse {

        String access_token;

    }

    /** Pojo for post request body */
    public static class LinkedInPostRequest {

        String comment;
        Visibility visibility;

        public LinkedInPostRequest(String comment) {
            this.comment = comment;
            this.visibility =  new Visibility("anyone");
        }

        /** Pojo for LinkedIn post visibility rule */
        public static class Visibility {

            String code;

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
