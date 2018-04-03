package com.ambassador.app.activities.main.conversion;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.activities.BasePresenter;
import com.ambassador.ambassadorsdk.internal.conversion.ConversionStatusListener;
import com.ambassador.ambassadorsdk.internal.identify.AmbIdentify;
import com.ambassador.ambassadorsdk.internal.utils.Identify;
import com.ambassador.app.Demo;
import com.ambassador.app.api.Requests;
import com.ambassador.app.api.pojo.GetShortCodeFromEmailResponse;
import com.ambassador.app.data.User;
import com.ambassador.app.exports.ConversionExport;
import com.ambassador.app.exports.Export;
import com.ambassador.app.exports.models.ConversionExportModel;
import com.ambassador.app.utils.Share;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ConversionPresenter extends BasePresenter<ConversionModel, ConversionView> {

    protected AmbassadorSDK AmbassadorSDK;

    protected ConversionPresenter() {
        AmbassadorSDK = new AmbassadorSDK();
    }

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
        conversionParametersBuilder.setAddToGroupId(model.selectedGroups != null ? model.selectedGroups.replaceAll(" ", "") : "");
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
                    Intent data = new Intent();
                    data.putExtra("referrer", "mbsy_cookie_code=" + shortCode + "&device_id=test1234");
                    new InstallReceiver().onReceive(Demo.get(), data);

                    AmbassadorSDK.identify(conversionParameters.getEmail());
                    AmbIdentify.getRunningInstance().setCompletionListener(new AmbIdentify.CompletionListener() {
                        @Override
                        public void complete() {
                            AmbassadorSDK.registerConversion(conversionParameters, false, new ConversionStatusListener() {
                                @Override
                                public void success() {
                                    Log.v("AMBASSADOR CONVERSION", "success()");
                                }

                                @Override
                                public void pending() {
                                    Log.v("AMBASSADOR CONVERSION", "pending()");
                                }

                                @Override
                                public void error() {
                                    Log.v("AMBASSADOR CONVERSION", "error()");
                                }
                            });
                        }

                        @Override
                        public void noSDK() {
                            // Not handled. Conversion won't be registered.
                        }

                        @Override
                        public void networkError() {
                            // Not handled. Conversion won't be registered.
                        }
                    });
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

    public void onActionClicked(String userId, Bundle identifyTraits, Bundle conversionProperties) {
        identifyTraits.putString("addToGroups", model.selectedGroups != null ? model.selectedGroups : "");

        Bundle identifyOptions = new Bundle();
        identifyOptions.putString("campaign", model.selectedCampaignId + "");

        Export<ConversionExportModel> export = new ConversionExport();

        ConversionExportModel conversionExportModel = new ConversionExportModel();
        conversionExportModel.identifyTraits = identifyTraits;
        conversionExportModel.identifyOptions = identifyOptions;
        conversionExportModel.userId = userId;
        conversionExportModel.conversionProperties = conversionProperties;

        export.setModel(conversionExportModel);
        String filename = export.zip(Demo.get());
        Share share = new Share(filename).withSubject("Ambassador Conversion Example Implementation").withBody(export.getReadme());
        view().share(share);
    }

}
