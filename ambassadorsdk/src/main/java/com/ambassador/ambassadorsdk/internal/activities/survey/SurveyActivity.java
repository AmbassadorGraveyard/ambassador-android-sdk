package com.ambassador.ambassadorsdk.internal.activities.survey;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.activities.PresenterManager;

import butterfork.Bind;
import butterfork.ButterFork;

public class SurveyActivity extends Activity implements SurveyView {

    protected SurveyPresenter surveyPresenter;

    @Bind(B.id.llSurvey) protected LinearLayout llSurvey;
    @Bind(B.id.rlLoading) protected RelativeLayout rlLoading;

    @Bind(B.id.ivExit) protected ImageView ivExit;
    @Bind(B.id.tvSurveyTitle) protected TextView tvSurveyTitle;
    @Bind(B.id.tvSurveyDescription) protected TextView tvSurveyDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            surveyPresenter = new SurveyPresenter();
        } else {
            surveyPresenter = PresenterManager.getInstance().restorePresenter(savedInstanceState);
        }

        setContentView(R.layout.activity_survey);
        ButterFork.bind(this);

        ivExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surveyPresenter.onExitButtonClicked();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        surveyPresenter.bindView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        surveyPresenter.unbindView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PresenterManager.getInstance().savePresenter(surveyPresenter, outState);
    }

    @Override
    public void showLoading() {
        ObjectAnimator loadingAnimator = ObjectAnimator.ofFloat(rlLoading, "alpha", 0, 1);
        loadingAnimator.setDuration(300);

        ObjectAnimator surveyAnimator = ObjectAnimator.ofFloat(llSurvey, "alpha", 1, 0);
        surveyAnimator.setDuration(300);

        loadingAnimator.start();
        surveyAnimator.start();
    }

    @Override
    public void showSurvey() {
        ObjectAnimator surveyAnimator = ObjectAnimator.ofFloat(llSurvey, "alpha", 0, 1);
        surveyAnimator.setDuration(300);

        ObjectAnimator loadingAnimator = ObjectAnimator.ofFloat(rlLoading, "alpha", 1, 0);
        loadingAnimator.setDuration(300);

        surveyAnimator.start();
        loadingAnimator.start();
    }

    @Override
    public void setTitle(String title) {
        tvSurveyTitle.setText(title);
    }

    @Override
    public void setDescription(String description) {
        tvSurveyDescription.setText(description);
    }

    @Override
    public void exit() {
        finish();
    }

}
