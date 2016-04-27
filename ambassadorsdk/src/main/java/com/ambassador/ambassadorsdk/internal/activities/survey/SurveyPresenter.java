package com.ambassador.ambassadorsdk.internal.activities.survey;

import android.os.Handler;
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
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SurveyModel tmp = new SurveyModel();
                tmp.ambassadorName = "John";
                tmp.companyName = "Ambassador";
                setModel(tmp);
            }
        }, 3000);
    }

    public void onExitButtonClicked() {
        view().exit();
    }

    public void onSubmitButtonClicked() {
        view().exit();
    }

}
