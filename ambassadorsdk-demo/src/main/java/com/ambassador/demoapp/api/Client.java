package com.ambassador.demoapp.api;

import com.ambassador.ambassadorsdk.BuildConfig;
import com.ambassador.demoapp.api.pojo.GetCampaignsResponse;
import com.ambassador.demoapp.api.pojo.LoginRequest;
import com.ambassador.demoapp.api.pojo.LoginResponse;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

public interface Client {

    String ENDPOINT = BuildConfig.IS_RELEASE_BUILD ?
            "https://api.getambassador.com" :
            "https://dev-ambassador-api.herokuapp.com";

    @POST("/v2-auth/")
    void login(
            @Body LoginRequest loginRequest,
            Callback<LoginResponse> loginResponse
    );

    @GET("/campaigns/")
    void getCampaigns(
            @Header("Authorization") String universalToken,
            Callback<GetCampaignsResponse> getCampaignsResponse
    );

}
