package com.ambassador.ambassadorsdk.internal.api.identify;

import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 *
 */
public interface IdentifyClient {

    /** */
    String ENDPOINT = AmbassadorConfig.ambassadorApiUrl();

    /**
     *
     * @param body
     * @param callback
     */
    @POST("/auth/session")
    void createPusherChannel(
            @Body Object body,
            Callback<Object> callback
    );

}
