package com.ambassador.demo.activities.main.conversion;

import com.ambassador.ambassadorsdk.internal.activities.BasePresenter;

public class ConversionPresenter extends BasePresenter<ConversionModel, ConversionView> {

    @Override
    protected void updateView() {

    }

    public void onEnrollAsAmbassadorToggled(boolean isChecked) {
        view().toggleEnrollAsAmbassadorInputs(isChecked);
    }

    public void onGroupChooserClicked() {

    }

    public void onCampaignChooserClicked() {

    }

    public void onSubmitClicked() {

    }

    public void onActionClicked() {

    }

}
