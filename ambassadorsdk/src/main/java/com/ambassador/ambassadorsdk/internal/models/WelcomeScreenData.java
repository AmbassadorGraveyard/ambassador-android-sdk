package com.ambassador.ambassadorsdk.internal.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.WelcomeScreenDialog;

/**
 * Represents data needed to properly show a WelcomeScreenDialog.
 */
public class WelcomeScreenData {

    /** Text to display on the top bar where the "Close" button is. */
    protected String topBarText;

    /** URL to the image to display as the avatar. */
    protected String imageUrl;

    /** Large text to display below picture. */
    protected String title;

    /** Smaller text to display below button. */
    protected String message;

    /** Text for the main button to have. */
    protected String buttonText;

    /** Action for the main button. */
    protected View.OnClickListener buttonOnClickListener;

    /** Text for the bottom left button. */
    protected String link1Text;

    /** Action for the bottom left button. */
    protected View.OnClickListener link1OnClickListener;

    /** Text for the bottom right button. */
    protected String link2Text;

    /** Action for the bottom right button. */
    protected View.OnClickListener link2OnClickListener;

    /** Color to theme the welcome screen with. */
    protected int colorTheme;

    public WelcomeScreenData() {

    }

    public String getTopBarText() {
        return topBarText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getButtonText() {
        return buttonText;
    }

    public View.OnClickListener getButtonOnClickListener() {
        return buttonOnClickListener;
    }

    public String getLink1Text() {
        return link1Text;
    }

    public View.OnClickListener getLink1OnClickListener() {
        return link1OnClickListener;
    }

    public String getLink2Text() {
        return link2Text;
    }

    public View.OnClickListener getLink2OnClickListener() {
        return link2OnClickListener;
    }

    public int getColorTheme() {
        return colorTheme;
    }

    public static class Builder {

        protected WelcomeScreenData welcomeScreenData;

        public Builder() {
            welcomeScreenData = new WelcomeScreenData();
        }

        public Builder setTopBarText(String topBarText) {
            welcomeScreenData.topBarText = topBarText;
            return this;
        }

        public Builder setImageUrl(String imageUrl) {
            welcomeScreenData.imageUrl = imageUrl;
            return this;
        }

        public Builder setTitle(String title) {
            welcomeScreenData.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            welcomeScreenData.message = message;
            return this;
        }

        public Builder setButtonText(String buttonText) {
            welcomeScreenData.buttonText = buttonText;
            return this;
        }

        public Builder setButtonOnClickListener(View.OnClickListener buttonOnClickListener) {
            welcomeScreenData.buttonOnClickListener = buttonOnClickListener;
            return this;
        }

        public Builder setLink1Text(String link1Text) {
            welcomeScreenData.link1Text = link1Text;
            return this;
        }

        public Builder setLink1OnClickListener(View.OnClickListener link1OnClickListener) {
            welcomeScreenData.link1OnClickListener = link1OnClickListener;
            return this;
        }

        public Builder setLink2Text(String link2Text) {
            welcomeScreenData.link2Text = link2Text;
            return this;
        }

        public Builder setLink2OnClickListener(View.OnClickListener link2OnClickListener) {
            welcomeScreenData.link2OnClickListener = link2OnClickListener;
            return this;
        }

        public Builder setColorTheme(int colorTheme) {
            welcomeScreenData.colorTheme = colorTheme;
            return this;
        }

        public WelcomeScreenData build() {
            return welcomeScreenData;
        }

    }

    /**
     * Appends information from WelcomeScreenDialog.Parameters to WelcomeScreenData, and returns the
     * WelcomeScreenData object.
     * @param parameters the Parameters object with info to append.
     * @return WelcomeScreenData with info from parameters appended.
     */
    @NonNull
    public WelcomeScreenData withParameters(@Nullable WelcomeScreenDialog.Parameters parameters) {
        if (parameters == null) return this;

        buttonOnClickListener = parameters.getButtonOnClickListener();
        link1OnClickListener = parameters.getLink1OnClickListener();
        link2OnClickListener = parameters.getLink2OnClickListener();

        return this;
    }


    @NonNull
    public WelcomeScreenData withBackendData(@Nullable WelcomeScreenDialog.BackendData backendData) {
        if (backendData == null) return this;

        imageUrl = backendData.getImageUrl();

        return this;
    }

    /**
     * Creates a WelcomeScreenData object based on information in RAFOptions, and returns it.
     * @return WelcomeScreenData with information pre-filled from RAFOptions.
     */
    @NonNull
    public static WelcomeScreenData getFromOptions() {
        RAFOptions rafOptions = RAFOptions.get();
        return new WelcomeScreenData.Builder()
                .setTopBarText(rafOptions.getWelcomeScreenTopBarText())
                .setTitle(rafOptions.getWelcomeScreenTitle())
                .setMessage(rafOptions.getWelcomeScreenMessage())
                .setButtonText(rafOptions.getWelcomeScreenButtonText())
                .setLink1Text(rafOptions.getWelcomeScreenLink1Text())
                .setLink2Text(rafOptions.getWelcomeScreenLink2Text())
                .setColorTheme(rafOptions.getWelcomeScreenColorTheme())
                .build();
    }

}
