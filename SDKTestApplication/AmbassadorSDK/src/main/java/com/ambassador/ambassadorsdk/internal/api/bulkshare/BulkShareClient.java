package com.ambassador.ambassadorsdk.internal.api.bulkshare;

import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 *
 */
interface BulkShareClient {

    /** */
    String ENDPOINT = AmbassadorConfig.ambassadorApiUrl();

    /**
     *
     * @param universalId
     * @param auth
     * @param request
     * @param callback
     */
    @POST("/share/sms/")
    void bulkShareSms(
            @Header("MBSY_UNIVERSAL_ID") String universalId,
            @Header("Authorization") String auth,
            @Body BulkShareApi.BulkShareSmsBody request,
            Callback<String> callback
    );

    /**
     *
     * @param universalId
     * @param auth
     * @param request
     * @param callback
     */
    @POST("/share/email/")
    void bulkShareEmail(
            @Header("MBSY_UNIVERSAL_ID") String universalId,
            @Header("Authorization") String auth,
            @Body BulkShareApi.BulkShareEmailBody request,
            Callback<String> callback
    );

    /**
     *
     * @param universalId
     * @param auth
     * @param request
     * @param callback
     */
    @POST("/track/share/")
    void bulkShareTrack(
            @Header("MBSY_UNIVERSAL_ID") String universalId,
            @Header("Authorization") String auth,
            @Body BulkShareApi.BulkShareTrackBody[] request,
            Callback<String> callback
    );

}
