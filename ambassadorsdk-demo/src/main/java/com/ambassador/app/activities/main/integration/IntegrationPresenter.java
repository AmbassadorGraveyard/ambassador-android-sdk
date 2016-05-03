package com.ambassador.app.activities.main.integration;

import com.ambassador.ambassadorsdk.internal.activities.BasePresenter;

public class IntegrationPresenter extends BasePresenter<IntegrationModel, IntegrationView> {

    @Override
    protected void updateView() {

    }

    public void onAddClicked() {
        view().createIntegration();
    }

    public void onIntegrationClicked() {

    }

    public void onShareClicked() {

    }

    public void onEditClicked() {

    }

    public void onDeleteClicked() {

    }

}
