package com.ambassador.demoapp.data;

import com.ambassador.ambassadorsdk.RAFOptions;

public class Integration {

    protected String name;
    protected int campaignId;
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
