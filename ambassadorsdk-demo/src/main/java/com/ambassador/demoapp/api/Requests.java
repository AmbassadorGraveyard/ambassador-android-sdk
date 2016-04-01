package com.ambassador.demoapp.api;

import com.ambassador.ambassadorsdk.internal.api.ServiceGenerator;
import com.ambassador.demoapp.api.pojo.GetCampaignsResponse;
import com.ambassador.demoapp.api.pojo.LoginRequest;
import com.ambassador.demoapp.api.pojo.LoginResponse;

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

    public static Requests get() {
        if (instance == null) {
            instance = new Requests();
        }

        return instance;
    }

}
