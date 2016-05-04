package com.ambassador.app.activities.main.integration;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.activities.BasePresenter;
import com.ambassador.app.Demo;
import com.ambassador.app.data.Integration;
import com.ambassador.app.data.User;
import com.ambassador.app.exports.Export;
import com.ambassador.app.exports.IntegrationExport;
import com.ambassador.app.utils.Share;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;

public class IntegrationPresenter extends BasePresenter<IntegrationModel, IntegrationView> {

    @Override
    protected void updateView() {
        loadData();

        if (model.integrations.size() > 0) {
            view().showPopulatedListContent();
            view().hideEmptyListContent();
        } else {
            view().showEmptyListContent();
            view().hidePopulatedListContent();
        }

        view().setListContent(model.integrations);
    }

    protected void loadData() {
        if (model.integrations == null) {
            model.integrations = new ArrayList<>();
        }
        model.integrations.clear();
        SharedPreferences preferences = Demo.get().getSharedPreferences("integrations", Context.MODE_PRIVATE);
        String integrationsArrayString = preferences.getString(User.get().getUniversalId(), "[]");
        JsonArray integrationsArray = new JsonParser().parse(integrationsArrayString).getAsJsonArray();
        for (int i = 0; i < integrationsArray.size(); i++) {
            Integration integration = new Gson().fromJson(integrationsArray.get(i).getAsString(), Integration.class);
            model.integrations.add(integration);
        }

        Collections.sort(model.integrations);
    }

    @Override
    public void bindView(@NonNull IntegrationView view) {
        super.bindView(view);
        if (model == null) {
            setModel(new IntegrationModel());
        }
    }

    public void onAddClicked() {
        view().createIntegration();
    }

    public void onIntegrationClicked(int position) {
        view().present(model.integrations.get(position));
    }

    public void onShareClicked(int position) {
        Integration integration = model.integrations.get(position);
        RAFOptions rafOptions = integration.getRafOptions();
        if (rafOptions != null) {
            Export<Integration> export = new IntegrationExport();
            export.setModel(integration);
            String filename = export.zip(Demo.get());
            view().share(new Share(filename).withSubject("Ambassador RAF Integration Instructions").withBody(export.getReadme()));
        }
    }

    public void onEditClicked(int position) {
        view().edit(model.integrations.get(position));
    }

    public void onDeleteClicked(int position) {
        view().askToDelete(model.integrations.get(position));
    }

    public void onDeleteConfirmed(Integration integration) {
        integration.delete();
        model.integrations.remove(integration);
        updateView();
    }

    public void onActionClicked() {
        view().toggleEditing();
    }

}
