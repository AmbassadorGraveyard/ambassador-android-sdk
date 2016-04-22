package com.ambassador.demo.activities.main.identify;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.internal.activities.BasePresenter;
import com.ambassador.ambassadorsdk.internal.utils.Identify;

public class IdentifyPresenter extends BasePresenter<IdentifyModel, IdentifyView> {

    @Override
    protected void updateView() {

    }

    public void onSubmitClicked() {
        String emailAddress = view().getEmailAddress();
        if (emailAddress.length() == 0) {
            view().notifyNoEmail();
            view().closeSoftKeyboard();
        } else if (!(new Identify(emailAddress).isValidEmail())) {
            view().notifyInvalidEmail();
            view().closeSoftKeyboard();
        } else {
            AmbassadorSDK.identify(emailAddress);
            view().notifyIdentifying();
            view().closeSoftKeyboard();
        }
    }

}
