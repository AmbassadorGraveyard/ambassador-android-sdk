package com.ambassador.demoapp.data;

import com.ambassador.ambassadorsdk.RAFOptions;

public class Integration {

    protected String name;
    protected int campaignId;
    protected String campaignName;
    protected RAFOptions rafOptions;
    protected long createdAtDate;

    public Integration() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public RAFOptions getRafOptions() {
        return rafOptions;
    }

    public void setRafOptions(RAFOptions rafOptions) {
        this.rafOptions = rafOptions;
    }

    public long getCreatedAtDate() {
        return createdAtDate;
    }

    public void setCreatedAtDate(long createdAtDate) {
        this.createdAtDate = createdAtDate;
    }

}
