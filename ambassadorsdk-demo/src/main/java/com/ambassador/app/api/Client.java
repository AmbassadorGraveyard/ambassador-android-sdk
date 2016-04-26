package com.ambassador.app.api;

import com.ambassador.ambassadorsdk.BuildConfig;
import com.ambassador.app.api.pojo.GetCampaignsResponse;
import com.ambassador.app.api.pojo.GetGroupsResponse;
import com.ambassador.app.api.pojo.GetShortCodeFromEmailResponse;
import com.ambassador.app.api.pojo.LoginRequest;
import com.ambassador.app.api.pojo.LoginResponse;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;

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

    @GET("/groups/")
    void getGroups(
            @Header("Authorization") String universalToken,
            Callback<GetGroupsResponse> getGroupsResponse
    );

    @GET("/urls/")
    void getShortCodeFromEmail(
            @Header("Authorization") String sdkToken,
            @Query("campaign_uid") int campaignUid,
            @Query("email") String referrerEmail,
            Callback<GetShortCodeFromEmailResponse> getShortCodeFromEmailResponse
    );

}
