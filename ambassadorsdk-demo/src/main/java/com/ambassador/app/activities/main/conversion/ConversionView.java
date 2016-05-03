package com.ambassador.app.activities.main.conversion;

import com.ambassador.app.utils.Share;

public interface ConversionView {

    void toggleEnrollAsAmbassadorInputs(boolean isChecked);
    void getGroups(String preselected);
    void setGroupsText(String groupsText, boolean isHint);
    void getCampaigns();
    void setCampaignText(String campaignText);

    void notifyInvalidAmbassadorEmail();
    void notifyInvalidCustomerEmail();
    void notifyNoCampaign();
    void notifyNoRevenue();
    void notifyNoAmbassadorFound();
    void notifyConversion();

    void closeSoftKeyboard();
    void share(Share share);

}
