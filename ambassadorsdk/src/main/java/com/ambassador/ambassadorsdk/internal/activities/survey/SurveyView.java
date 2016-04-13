package com.ambassador.ambassadorsdk.internal.activities.survey;

public interface SurveyView {

    void showLoading();
    void showSurvey();
    void setTitle(String title);
    void setDescription(String description);
    void exit();

}
