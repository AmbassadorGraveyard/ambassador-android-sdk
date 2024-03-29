package com.ambassador.ambassadorsdk.internal.activities.survey;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.internal.activities.BasePresenter;

public class SurveyPresenter extends BasePresenter<SurveyModel, SurveyView> {

    @Override
    protected void updateView() {
        view().setTitle(model.getTitle());
        view().setDescription(model.getDescription());
        view().setBackgroundColor(model.getBackgroundColor());
        view().setContentColor(model.getContentColor());
        view().setButtonColor(model.getButtonColor());
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
        final SurveyModel tmp = new SurveyModel();
        tmp.ambassadorName = "John";
        tmp.companyName = "Ambassador";

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setModel(tmp);
            }
        }, 500);
    }

    public void onExitButtonClicked() {
        view().exit();
    }

    public void onSubmitButtonClicked() {
        view().exit();
    }

}
