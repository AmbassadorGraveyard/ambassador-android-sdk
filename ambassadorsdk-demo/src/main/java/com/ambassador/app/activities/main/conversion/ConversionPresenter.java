package com.ambassador.app.activities.main.conversion;

import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.activities.BasePresenter;
import com.ambassador.ambassadorsdk.internal.utils.Identify;
import com.ambassador.app.Demo;
import com.ambassador.app.api.Requests;
import com.ambassador.app.api.pojo.GetShortCodeFromEmailResponse;
import com.ambassador.app.data.User;
import com.ambassador.app.exports.ConversionExport;
import com.ambassador.app.exports.Export;
import com.ambassador.app.utils.Share;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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
        if (result != null) {
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
        if (result != null) {
            JsonObject json = new JsonParser().parse(result).getAsJsonObject();
            model.selectedCampaignName = json.get("name").getAsString();
            model.selectedCampaignId = json.get("id").getAsInt();
            view().setCampaignText(model.selectedCampaignName);
        }
    }

    public void onSubmitClicked(String ambassadorEmail, final ConversionParameters.Builder conversionParametersBuilder) {
        conversionParametersBuilder.setCampaign(model.selectedCampaignId);
        conversionParametersBuilder.setAddToGroupId(model.selectedGroups != null ? model.selectedGroups.replaceAll(" ", "") : null);
        final ConversionParameters conversionParameters = conversionParametersBuilder.build();

        if (!(new Identify(ambassadorEmail).isValidEmail())) {
            view().notifyInvalidAmbassadorEmail();
            return;
        }

        if (!(new Identify(conversionParameters.getEmail()).isValidEmail())) {
            view().notifyInvalidCustomerEmail();
            return;
        }

        if (conversionParameters.getCampaign() == 0) {
            view().notifyNoCampaign();
            return;
        }

        if (conversionParameters.getRevenue() < 0) {
            view().notifyNoRevenue();
            return;
        }

        Requests.get().getShortCodeFromEmail(User.get().getSdkToken(), conversionParameters.getCampaign(), ambassadorEmail, new Callback<GetShortCodeFromEmailResponse>() {
            @Override
            public void success(GetShortCodeFromEmailResponse getShortCodeFromEmailResponse, Response response) {
                if (getShortCodeFromEmailResponse.results.length > 0) {
                    String shortCode = getShortCodeFromEmailResponse.results[0].short_code;
                    conversionParameters.updateShortCode(shortCode);
                    AmbassadorSDK.registerConversion(conversionParameters, false);
                    view().notifyConversion();
                } else {
                    failure(null);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                view().notifyNoAmbassadorFound();
            }
        });
    }

    public void onActionClicked(ConversionParameters.Builder conversionParametersBuilder) {
        conversionParametersBuilder.setCampaign(model.selectedCampaignId);
        conversionParametersBuilder.setAddToGroupId(model.selectedGroups != null ? model.selectedGroups.replaceAll(" ", "") : null);
        ConversionParameters conversionParameters = conversionParametersBuilder.build();

        if (!(new Identify(conversionParameters.getEmail()).isValidEmail())) {
            view().notifyInvalidCustomerEmail();
            return;
        }

        if (conversionParameters.getCampaign() == 0) {
            view().notifyNoCampaign();
            return;
        }

        if (conversionParameters.getRevenue() < 0) {
            view().notifyNoRevenue();
            return;
        }

        Export<ConversionParameters> export = new ConversionExport();
        export.setModel(conversionParameters);
        String filename = export.zip(Demo.get());
        Share share = new Share(filename).withSubject("Ambassador Conversion Example Implementation").withBody(export.getReadme());
        view().share(share);
    }

}
