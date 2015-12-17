package com.ambassador.ambassadorsdk.internal.api.linkedIn;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Defines endpoints, parameters, callbacks for LinkedInApi methods
 */
public interface LinkedInClient {

    /** Base url for api methods */
    String ENDPOINT = "https://api.linkedin.com";

    /**
     * https://api.linkedin.com/v1/people/~/shares?format=json
     * @param authorization String authorization header for LinkedIn
     * @param body the POST request body pojo
     * @param callback the Retrofit callback
     */
    @POST("/v1/people/~/shares?format=json")
    @Headers({"Content-Type: application/json", "Host: api.linkedin.com", "x-li-format: json"})
    void post(
            @Header("Authorization") String authorization,
            @Body LinkedInApi.LinkedInPostRequest body,
            Callback<Object> callback
    );

    /**
     * https://api.linkedin.com/v1/people/~?format=json
     * @param authorization String authorization header for LinkedIn
     * @param callback the Retrofit callback
     */
    @GET("/v1/people/~?format=json")
    void getProfile(
            @Header("Authorization") String authorization,
            Callback<LinkedInApi.LinkedInProfileResponse> callback
    );

}
