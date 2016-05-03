package com.ambassador.ambassadorsdk.internal.activities.survey;

import android.support.annotation.ColorInt;

public interface SurveyView {

    void showLoading();
    void showSurvey();
    void setTitle(String title);
    void setDescription(String description);
    void setBackgroundColor(@ColorInt int backgroundColor);
    void setContentColor(@ColorInt int contentColor);
    void setButtonColor(@ColorInt int buttonColor);
    void exit();

}
