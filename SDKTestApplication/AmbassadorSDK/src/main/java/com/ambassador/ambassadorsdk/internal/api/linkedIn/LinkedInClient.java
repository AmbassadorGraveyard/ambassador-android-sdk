package com.ambassador.ambassadorsdk.internal.api.linkedIn;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 *
 */
public interface LinkedInClient {

    /** */
    String ENDPOINT = "https://api.linkedin.com";

    /**
     *
     * @param authorization
     * @param body
     * @param callback
     */
    @POST("/v1/people/~/shares?format=json")
    @Headers({"Content-Type: application/json", "Host: api.linkedin.com", "x-li-format: json"})
    void post(
            @Header("Authorization") String authorization,
            @Body LinkedInApi.LinkedInPostRequest body,
            Callback<Object> callback
    );

    /**
     *
     * @param authorization
     * @param callback
     */
    @GET("/v1/people/~?format=json")
    void getProfile(
            @Header("Authorization") String authorization,
            Callback<LinkedInApi.LinkedInProfileResponse> callback
    );

}
