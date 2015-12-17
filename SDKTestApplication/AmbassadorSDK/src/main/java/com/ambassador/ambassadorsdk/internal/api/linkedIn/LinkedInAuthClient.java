package com.ambassador.ambassadorsdk.internal.api.linkedIn;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.mime.TypedInput;

/**
 *
 */
public interface LinkedInAuthClient {

    /** */
    String ENDPOINT = "https://www.linkedin.com";

    /**
     *
     * @param body
     * @param callback
     */
    @POST("/uas/oauth2/accessToken")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    void login(
            @Body TypedInput body,
            Callback<LinkedInApi.LinkedInLoginResponse> callback
    );

}
