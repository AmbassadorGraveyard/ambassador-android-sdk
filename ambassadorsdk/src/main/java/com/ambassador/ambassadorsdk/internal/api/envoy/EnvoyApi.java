package com.ambassador.ambassadorsdk.internal.api.envoy;

import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.ServiceGenerator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Handles Envoy API methods using Retrofit services and contains all relevant pojo classes.
 */
public class EnvoyApi {

    /** Client for making requests to the Envoy API */
    private EnvoyClient envoyClient;

    /**
     * Default constructor.
     * Instantiates EnvoyApi and automatically initializes client.
     */
    @SuppressWarnings("unused")
    public EnvoyApi() {
        this(true);
    }

    /**
     * Optionally initializes client.
     * @param doInit whether or not to automatically initialize client.
     */
    public EnvoyApi(boolean doInit) {
        if (doInit) {
            init();
        }
    }

    /**
     * Instantiates and sets the Identify client objects using the ServiceGenerator.
     */
    public void init() {
        setEnvoyClient(ServiceGenerator.createService(EnvoyClient.class));
    }

    /**
     * Sets the client for envoy requests
     * @param envoyClient an instantiation of EnvoyClient for this EnvoyApi to use
     */
    public void setEnvoyClient(EnvoyClient envoyClient) {
        this.envoyClient = envoyClient;
    }

    /**
     * Gets access token from the Envoy API.
     * Uses envoyClient to handle the request, and using the envoyClient retrofit callback to callback
     * through the RequestCompletion.
     * @param clientId the String clientId for envoy.
     * @param clientSecret the String clientSecret for envoy.
     * @param popup the String unique popup code generated during OAuth.
     * @param completion the RequestCompletion to callback through.
     */
    public void getAccessToken(String clientId, String clientSecret, String popup, final RequestManager.RequestCompletion completion) {
        envoyClient.getAccessToken(clientId, clientSecret, popup, new Callback<GetAccessTokenResponse>() {
            @Override
            public void success(GetAccessTokenResponse getAccessTokenResponse, Response response) {
                completion.onSuccess(getAccessTokenResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                completion.onFailure(null);
            }
        });
    }

    /**
     * Shares a social message to a users account using the Envoy API.
     * Uses envoyClient to handle the request, and using the envoyClient retrofit callback to callback
     * through the RequestCompletion.
     * @param provider the String name of the provider to share to [facebook, twitter, linkedin].
     * @param clientId the String clientId for envoy.
     * @param clientSecret the String clientSecret for envoy.
     * @param accessToken the String accessToken to envoy obtained by getAccessToken(...).
     * @param message the String message to share to the social network.
     * @param completion the RequestCompletion to callback through.
     */
    public void share(String provider, String clientId, String clientSecret, String accessToken, String message, final RequestManager.RequestCompletion completion) {
        envoyClient.share(provider, clientId, clientSecret, accessToken, message, new Callback<ShareResponse>() {
            @Override
            public void success(ShareResponse shareResponse, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    /** Pojo for get access token response */
    public static class GetAccessTokenResponse {

    }

    /** Pojo for share response */
    public static class ShareResponse {

    }

}
