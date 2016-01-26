package com.ambassador.ambassadorsdk.internal.api.linkedin2;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.mime.TypedInput;

/**
 * Defines endpoints, parameters, callbacks for LinkedInApi unauthenticated methods
 */
public interface LinkedInAuthClient {

    /** Base url for api methods */
    String ENDPOINT = "https://www.linkedin.com";

    /**
     * https://www.linkedin.com/uas/oauth2/accessToken
     * @param body the POST request body pojo
     * @param callback the Retrofit callback
     */
    @POST("/uas/oauth2/accessToken")
    @Headers({"Content-Type: application/x-www-form-urlencoded"})
    void login(
            @Body TypedInput body,
            Callback<LinkedInApi.LinkedInLoginResponse> callback
    );

}
