package com.ambassador.ambassadorsdk.internal.api.conversions;

import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 *
 */
interface ConversionsClient {

    /** */
    String ENDPOINT = AmbassadorConfig.ambassadorApiUrl();

    @POST("/universal/action/conversion/")
    void registerConversionRequest(
            @Header("MBSY_UNIVERSAL_ID") String universalId,
            @Header("Authorization") String auth,
            @Query("u") String uid,
            @Body ConversionsApi.RegisterConversionRequestBody body,
            Callback<String> callback
    );

}
