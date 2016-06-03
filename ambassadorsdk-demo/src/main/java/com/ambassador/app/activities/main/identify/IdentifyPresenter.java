package com.ambassador.app.activities.main.identify;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.internal.activities.BasePresenter;
import com.ambassador.ambassadorsdk.internal.utils.Identify;
import com.ambassador.app.Demo;
import com.ambassador.app.exports.Export;
import com.ambassador.app.exports.IdentifyExport;
import com.ambassador.app.utils.Share;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class IdentifyPresenter extends BasePresenter<IdentifyModel, IdentifyView> {

    @Override
    protected void updateView() {

    }

    @Override
    public void bindView(@NonNull IdentifyView view) {
        super.bindView(view);
        if (model == null) {
            model = new IdentifyModel();
        }
    }

    public void onSubmitClicked(Bundle traits, Bundle options) {
        if (options != null && model.selectedCampaignName != null) {
            options.putString("campaign", model.selectedCampaignId + "");
        }

        if (traits != null) {
            for (String key : traits.keySet()) {
                Log.v("AmbassadorSDK", key + "=" + traits.getString(key, ""));
            }
        }
        if (options != null) {
            for (String key : options.keySet()) {
                Log.v("AmbassadorSDK", key + "=" + traits.getString(key, ""));
            }
        }

        String emailAddress = traits.getString("email", null);
        if (emailAddress == null) {
            view().notifyNoEmail();
            view().closeSoftKeyboard();
        } else if (emailAddress.length() == 0) {
            view().notifyNoEmail();
            view().closeSoftKeyboard();
        } else if (!(new Identify(emailAddress).isValidEmail())) {
            view().notifyInvalidEmail();
            view().closeSoftKeyboard();
        } else {
            AmbassadorSDK.identify(emailAddress, traits, options);
            view().notifyIdentifying();
            view().closeSoftKeyboard();
        }
    }

    public void onCampaignChooserClicked() {
        view().getCampaigns();
    }

    public void onCampaignResult(String result) {
        if (result != null) {
            JsonObject json = new JsonParser().parse(result).getAsJsonObject();
            model.selectedCampaignName = json.get("name").getAsString();
            model.selectedCampaignId = json.get("id").getAsInt();
            view().setCampaignText(model.selectedCampaignName);
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
