package com.ambassador.demo.activities.main.identify;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.internal.activities.BasePresenter;
import com.ambassador.ambassadorsdk.internal.utils.Identify;
import com.ambassador.demo.Demo;
import com.ambassador.demo.exports.Export;
import com.ambassador.demo.exports.IdentifyExport;
import com.ambassador.demo.utils.Share;

public class IdentifyPresenter extends BasePresenter<IdentifyModel, IdentifyView> {

    @Override
    protected void updateView() {

    }

    public void onSubmitClicked(String emailAddress) {
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

    public void onActionClicked(String emailAddress) {
        if (!new Identify(emailAddress).isValidEmail()) {
            view().notifyInvalidEmail();
            return;
        }

        Export<String> export = new IdentifyExport();
        export.setModel(emailAddress);
        String filename = export.zip(Demo.get());
        Share share = new Share(filename).withSubject("Ambassador Identify Example Implementation").withBody(export.getReadme());
        view().share(share);
    }

}
