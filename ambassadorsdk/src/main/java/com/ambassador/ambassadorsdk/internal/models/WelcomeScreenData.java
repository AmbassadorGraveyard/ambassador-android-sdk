package com.ambassador.ambassadorsdk.internal.models;

import android.graphics.Color;
import android.view.View;

/**
 * Represents data needed to properly show a WelcomeScreenDialog.
 */
public class WelcomeScreenData {

    /** WelcomeScreenData object populated with dummy data for testing */
    public static final WelcomeScreenData TEST_DATA;
    static {
        TEST_DATA = new WelcomeScreenData.Builder()
                .setTitle("John Doe has referred you Ambassador!")
                .setMessage("You understand the value of referrals. Maybe you've even explored referral marketing software.")
                .setButtonText("CREATE AN ACCOUNT")
                .setLink1Text("Testimonials")
                .setLink2Text("Request Demo")
                .setColorTheme(Color.parseColor("#4198d1"))
                .setImageUrl("https://upload.wikimedia.org/wikipedia/commons/7/77/Avatar_cat.png")
                .build();
    }

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

}
