package com.ambassador.ambassadorsdk.internal.api.identify;

import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 *
 */
interface IdentifyClient {

    /** */
    String ENDPOINT = AmbassadorConfig.ambassadorApiUrl();

    /**
     *
     * @param sessionId
     * @param requestId
     * @param request
     * @param universalId
     * @param uid
     * @param callback
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
     *
     * @param universalId
     * @param auth
     * @param body
     * @param callback
     */
    @POST("/auth/session/")
    void createPusherChannel(
            @Header("MBSY_UNIVERSAL_ID") String universalId,
            @Header("Authorization") String auth,
            @Body Object body,
            Callback<IdentifyApi.CreatePusherChannelResponse> callback
    );

}
