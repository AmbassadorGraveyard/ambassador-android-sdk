package com.ambassador.ambassadorsdk.internal.api.envoy;

import com.ambassador.ambassadorsdk.BuildConfig;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Defines endpoints, parameters, callbacks for EnvoyApi methods.
 */
public interface EnvoyClient {

    /** Base url for api methods */
    String ENDPOINT = new StringResource(BuildConfig.IS_RELEASE_BUILD ? R.string.envoy_api_url : R.string.envoy_api_url_dev).getValue();

    /**
     * https://api.getenvoy.co/oauth/access_token/
     * https://dev-envoy-api.herokuapp.com/oauth/access_token/
     * @param clientId the String clientId for envoy.
     * @param clientSecret the String clientSecret for envoy.
     * @param popup the String unique popup code generated during OAuth.
     * @param callback the retrofit callback.
     */
    @GET("/oauth/access_token/")
    void getAccessToken(
            @Query("client_id") String clientId,
            @Query("client_secret") String clientSecret,
            @Query("popop") String popup,
            Callback<EnvoyApi.GetAccessTokenResponse> callback
    );

    /**
     * https://api.getenvoy.co/provider/{provider}/share
     * https://dev-envoy-api.herokuapp.com/provider/{provider}/share
     * @param provider the String name of the provider to share to [facebook, twitter, linkedin].
     * @param clientId the String clientId for envoy.
     * @param clientSecret the String clientSecret for envoy.
     * @param accessToken the String accessToken to envoy obtained by getAccessToken(...).
     * @param message the String message to share to the social network.
     * @param callback the retrofit callback.
     */
    @GET("/provider/{provider}/share/")
    void share(
            @Path("provider") String provider,
            @Query("client_id") String clientId,
            @Query("client_secret") String clientSecret,
            @Query("access_token") String accessToken,
            @Query("message") String message,
            Callback<EnvoyApi.ShareResponse> callback
    );

}
