package com.ambassador.app.api;

import com.ambassador.ambassadorsdk.internal.api.ServiceGenerator;
import com.ambassador.app.api.pojo.GetCampaignsResponse;
import com.ambassador.app.api.pojo.GetGroupsResponse;
import com.ambassador.app.api.pojo.GetShortCodeFromEmailResponse;
import com.ambassador.app.api.pojo.LoginRequest;
import com.ambassador.app.api.pojo.LoginResponse;

import retrofit.Callback;

public class Requests {

    public static Requests instance = null;

    protected Client client;

    public Requests() {
        client = ServiceGenerator.createService(Client.class);
    }

    public void login(String email, String password, Callback<LoginResponse> callback) {
        client.login(new LoginRequest(email, password), callback);
    }

    public void getCampaigns(String universalToken, Callback<GetCampaignsResponse> callback) {
        client.getCampaigns("UniversalToken " + universalToken, callback);
    }

    public void getGroups(String universalToken, Callback<GetGroupsResponse> callback) {
        client.getGroups("UniversalToken " + universalToken, callback);
    }

    public void getShortCodeFromEmail(String sdkToken, int campaignId, String email, Callback<GetShortCodeFromEmailResponse> callback) {
        client.getShortCodeFromEmail("SDKToken " + sdkToken, campaignId, email, callback);
    }

    public static Requests get() {
        if (instance == null) {
            instance = new Requests();
        }

        return instance;
    }

}
