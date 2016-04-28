package com.ambassador.ambassadorsdk.internal.activities.survey;

import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.internal.activities.BasePresenter;

public class SurveyPresenter extends BasePresenter<SurveyModel, SurveyView> {

    @Override
    protected void updateView() {
        view().setTitle(model.getTitle());
        view().setDescription(model.getDescription());
        view().showSurvey();
    }

    @Override
    public void bindView(@NonNull SurveyView view) {
        super.bindView(view);

        if (model == null) {
            view().showLoading();
            loadData();
        }
    }

    protected void loadData() {
        SurveyModel tmp = new SurveyModel();
        tmp.ambassadorName = "John";
        tmp.companyName = "Ambassador";
        setModel(tmp);
    }

    public void onExitButtonClicked() {
        view().exit();
    }

    public void onSubmitButtonClicked() {
        view().exit();
    }

}
