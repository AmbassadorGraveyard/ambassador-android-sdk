package com.ambassador.ambassadorsdk.internal.activities.survey;

import android.graphics.Color;
import android.support.annotation.ColorInt;

public class SurveyModel {

    @ColorInt protected static int defaultBackgroundColor = Color.parseColor("#24313F");
    @ColorInt protected static int defaultContentColor = Color.parseColor("#FFFFFF");
    @ColorInt protected static int defaultButtonColor = Color.parseColor("#3C97D3");

    @ColorInt protected int backgroundColor = defaultBackgroundColor;
    @ColorInt protected int contentColor = defaultContentColor;
    @ColorInt protected int buttonColor = defaultButtonColor;

    protected String ambassadorName;
    protected String companyName;

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getContentColor() {
        return contentColor;
    }

    public int getButtonColor() {
        return buttonColor;
    }

    public String getTitle() {
        return String.format("Hey %s!", ambassadorName);
    }

    public String getDescription() {
        return String.format("How likely are you to refer a friend to %s? 10 being very likely.", companyName);
    }

    public static void setDefaultColors(
            @ColorInt int backgroundColor,
            @ColorInt int contentColor,
            @ColorInt int buttonColor) {

        defaultBackgroundColor = backgroundColor;
        defaultContentColor = contentColor;
        defaultButtonColor = buttonColor;
    }

}
