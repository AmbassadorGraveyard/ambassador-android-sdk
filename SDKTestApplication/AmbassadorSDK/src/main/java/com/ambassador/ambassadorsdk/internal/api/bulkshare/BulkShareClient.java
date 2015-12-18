package com.ambassador.ambassadorsdk.internal.api.bulkshare;

import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Defines endpoints, parameters, callbacks for BulkShareApi methods
 */
public interface BulkShareClient {

    /** Base url for api methods */
    String ENDPOINT = AmbassadorConfig.ambassadorApiUrl();

    /**
     * https://api.getambassador.com/share/sms/
     * https://dev-ambassador-api.herokuapp.com/share/sms/
     * @param universalId the Ambasasdor universal id
     * @param auth the Ambasador universal token
     * @param request the POST request body pojo
     * @param callback the Retrofit callback
     */
    @POST("/share/sms/")
    void bulkShareSms(
            @Header("MBSY_UNIVERSAL_ID") String universalId,
            @Header("Authorization") String auth,
            @Body BulkShareApi.BulkShareSmsBody request,
            Callback<BulkShareApi.BulkShareSmsResponse> callback
    );

    /**
     * https://api.getambassador.com/share/email/
     * https://dev-ambassador-api.herokuapp.com/share/email/
     * @param universalId the Ambassador universal id
     * @param auth the Ambassador universal token
     * @param request the POST request body pojo
     * @param callback the Retrofit callback
     */
    @POST("/share/email/")
    void bulkShareEmail(
            @Header("MBSY_UNIVERSAL_ID") String universalId,
            @Header("Authorization") String auth,
            @Body BulkShareApi.BulkShareEmailBody request,
            Callback<BulkShareApi.BulkShareEmailResponse> callback
    );

    /**
     * https://api.getambassador.com/track/share/
     * https://dev-ambassador-api.herokuapp.com/track/share/
     * @param universalId the Ambassador universal id
     * @param auth the Ambassador universal token
     * @param request the POST request body pojo
     * @param callback the Retrofit callback
     */
    @POST("/track/share/")
    void bulkShareTrack(
            @Header("MBSY_UNIVERSAL_ID") String universalId,
            @Header("Authorization") String auth,
            @Body BulkShareApi.BulkShareTrackBody[] request,
            Callback<String> callback
    );

}
