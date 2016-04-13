package com.ambassador.ambassadorsdk.internal.activities.survey;

public class SurveyModel {

    protected String ambassadorName;
    protected String companyName;

    public String getTitle() {
        return String.format("Hey %s!", ambassadorName);
    }

    public String getDescription() {
        return String.format("How likely are you to refer a friend to %s? 10 being very likely.", companyName);
    }

}
