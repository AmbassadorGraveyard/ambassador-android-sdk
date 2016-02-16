package com.ambassador.ambassadorsdk.internal.api.identify;

import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Defines endpoints, parameters, callbacks for IdentifyApi methods
 */
public interface IdentifyClient {

    /** Base url for api methods */
    String ENDPOINT = AmbassadorConfig.ambassadorApiUrl();

    /**
     * https://api.getambassador.com/universal/action/identify/
     * https://dev-ambassador-api.herokuapp.com/universal/action/identify/
     * @param sessionId the Pusher session id
     * @param requestId the Pusher request id
     * @param universalId the Ambassador universal id
     * @param uid the Ambassador universal id
     * @param request the POST request body pojo
     * @param callback the Retrofit callback
     */
    @POST("/universal/action/identify/")
    @Headers({"Content-Type: application/json"})
    void identifyRequest(
            @Header("X-Mbsy-Client-Session-ID") String sessionId,
            @Header("X-Mbsy-Client-Request-ID") String requestId,
            @Header("MBSY_UNIVERSAL_ID") String universalId,
            @Header("Authorization") String auth,
            @Query("u") String uid,
            @Body IdentifyApi.IdentifyRequestBody request,
            Callback<IdentifyApi.IdentifyRequestResponse> callback
    );

    /**
     * https://api.getambassador.com/universal/action/identify/
     * https://dev-ambassador-api.herokuapp.com/universal/action/identify/
     * @param sessionId the Pusher session id
     * @param requestId the Pusher request id
     * @param universalId the Ambassador universal id
     * @param auth the Ambassador universal token
     * @param uid the Ambassador universal id
     * @param body the POST request body pojo
     * @param callback the Retrofit callback
     */
    @POST("/universal/action/identify/")
    void updateNameRequest(
            @Header("X-Mbsy-Client-Session-ID") String sessionId,
            @Header("X-Mbsy-Client-Request-ID") String requestId,
            @Header("MBSY_UNIVERSAL_ID") String universalId,
            @Header("Authorization") String auth,
            @Query("u") String uid,
            @Body IdentifyApi.UpdateNameRequestBody body,
            Callback<IdentifyApi.UpdateNameRequestResponse> callback
    );

    /**
     * https://api.getambassador.com/universal/action/identify/
     * https://dev-ambassador-api.herokuapp.com/universal/action/identify/
     * @param sessionId the Pusher session id
     * @param requestId the Pusher request id
     * @param universalId the Ambassador universal id
     * @param auth the Ambassador universal token
     * @param uid the Ambassador universal id
     * @param body the POST request body pojo
     * @param callback the Retrofit callback
     */
    @POST("/universal/action/identify/")
    void updateGcmToken(
            @Header("X-Mbsy-Client-Session-ID") String sessionId,
            @Header("X-Mbsy-Client-Request-ID") String requestId,
            @Header("MBSY_UNIVERSAL_ID") String universalId,
            @Header("Authorization") String auth,
            @Query("u") String uid,
            @Body IdentifyApi.UpdateGcmTokenBody body,
            Callback<IdentifyApi.UpdateGcmTokenResponse> callback
    );

    /**
     * https://api.getambassador.com/auth/session/
     * https://dev-ambassador-api.herokuapp.com/auth/session/
     * @param universalId the Ambassador universal id
     * @param auth the Ambassador universal token
     * @param body the POST request body pojo
     * @param callback the Retrofit callback
     */
    @POST("/auth/session/")
    void createPusherChannel(
            @Header("MBSY_UNIVERSAL_ID") String universalId,
            @Header("Authorization") String auth,
            @Body Object body,
            Callback<IdentifyApi.CreatePusherChannelResponse> callback
    );

}
