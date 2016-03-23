package com.ambassador.demoapp.api;

import com.ambassador.ambassadorsdk.BuildConfig;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;
import com.ambassador.demoapp.api.pojo.LoginRequest;
import com.ambassador.demoapp.api.pojo.LoginResponse;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface Client {

    String ENDPOINT = new StringResource(BuildConfig.IS_RELEASE_BUILD ?
            com.ambassador.ambassadorsdk.R.string.ambassador_api_url :
            com.ambassador.ambassadorsdk.R.string.ambassador_api_url_dev)
            .getValue();

    @POST("/v2-auth/")
    void login(
            @Body LoginRequest loginRequest,
            Callback<LoginResponse> loginResponse
    );

}
