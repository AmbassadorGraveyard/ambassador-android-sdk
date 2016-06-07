package com.ambassador.app.activities.main.identify;

import com.ambassador.app.utils.Share;

public interface IdentifyView {

    void notifyNoEmail();
    void notifyInvalidEmail();
    void notifyIdentifying();

    void getCampaigns();
    void setCampaignText(String campaignText);

    void closeSoftKeyboard();

    void share(Share share);

}
