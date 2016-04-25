package com.ambassador.demo.activities.main.conversion;

import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.activities.BasePresenter;
import com.ambassador.demo.Demo;
import com.ambassador.demo.exports.ConversionExport;
import com.ambassador.demo.exports.Export;
import com.ambassador.demo.utils.Share;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ConversionPresenter extends BasePresenter<ConversionModel, ConversionView> {

    @Override
    protected void updateView() {

    }

    @Override
    public void bindView(@NonNull ConversionView view) {
        super.bindView(view);
        if (model == null) {
            model = new ConversionModel();
        }
    }

    public void onEnrollAsAmbassadorToggled(boolean isChecked) {
        view().toggleEnrollAsAmbassadorInputs(isChecked);
    }

    public void onGroupChooserClicked() {
        String preselected = null;
        if (model.selectedGroups != null && !model.selectedGroups.equals("")) {
            preselected = model.selectedGroups.replaceAll(" ", "");
        }

        view().getGroups(preselected);
    }

    public void onGroupsResult(String result) {
        if (!(result == null)) {
            if ("".equals(result)) {
                view().setGroupsText("Add to groups", true);
            } else {
                view().setGroupsText(result, false);
            }

            model.selectedGroups = result;
        }
    }

    public void onCampaignChooserClicked() {
        view().getCampaigns();
    }

    public void onCampaignResult(String result) {
        if (!(result == null)) {
            JsonObject json = new JsonParser().parse(result).getAsJsonObject();
            model.selectedCampaignName = json.get("name").getAsString();
            model.selectedCampaignId = json.get("id").getAsInt();
            view().setCampaignText(model.selectedCampaignName);
        }
    }

    public void onSubmitClicked() {

    }

    public void onActionClicked() {
        Export<ConversionParameters> export = new ConversionExport();
        export.setModel(getConversionParametersBasedOnInputs());
        String filename = export.zip(Demo.get());
        Share share = new Share(filename).withSubject("Ambassador Conversion Example Implementation").withBody(export.getReadme());
        
    }

}
