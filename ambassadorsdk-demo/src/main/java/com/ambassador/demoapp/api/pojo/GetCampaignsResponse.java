package com.ambassador.demoapp.api.pojo;

public class GetCampaignsResponse {

    public CampaignResponse[] results;

    public static class CampaignResponse {

        public int uid;
        public String name;

    }

}
