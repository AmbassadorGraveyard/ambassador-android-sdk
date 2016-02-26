package com.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.factories.ResourceFactory;
import com.google.gson.Gson;

/**
 *
 */
public final class RAFOptions {

    private static RAFOptions instance;
    
    private String defaultShareMessage;
    private String titleText;
    private String descriptionText;
    private String toolbarTitle;
    private String logoPosition;
    private String logo;
    private int logoResId;

    private int homeBackgroundColor;

    private int homeWelcomeTitleColor;
    private float homeWelcomeTitleSize;
    private Typeface homeWelcomeTitleFont;

    private int homeWelcomeDescriptionColor;
    private float homeWelcomeDescriptionSize;
    private Typeface homeWelcomeDescriptionFont;

    private int homeToolbarColor;
    private int homeToolbarTextColor;
    private Typeface homeToolbarTextFont;
    private int homeToolbarArrowColor;

    private int homeShareTextBar;
    private int homeShareTextColor;
    private float homeShareTextSize;
    private Typeface homeShareTextFont;

    private Typeface socialGridTextFont;

    private int contactsListViewBackgroundColor;

    private float contactsListNameSize;
    private Typeface contactsListNameFont;

    private float contactsListValueSize;
    private Typeface contactsListValueFont;

    private int contactsSendBackground;
    private Typeface contactSendMessageTextFont;

    private int contactsToolbarColor;
    private int contactsToolbarTextColor;
    private int contactsToolbarArrowColor;

    private int contactsSendButtonColor;
    private int contactsSendButtonTextColor;

    private int contactsDoneButtonTextColor;

    private int contactsSearchBarColor;
    private int contactsSearchIconColor;

    private int contactNoPhotoAvailableBackgroundColor;

    private int linkedInToolbarColor;
    private int linkedInToolbarTextColor;
    private int linkedInToolbarArrowColor;

    private int twitterToolbarColor;
    private int twitterToolbarTextColor;
    private int twitterToolbarArrowColor;

    private String[] channels;

    private float socialOptionCornerRadius;

    private String welcomeScreenTopBarText;
    private String welcomeScreenTitle;
    private String welcomeScreenMessage;
    private String welcomeScreenButtonText;
    private String welcomeScreenLink1Text;
    private String welcomeScreenLink2Text;
    private int welcomeScreenColorTheme;

    private RAFOptions() {}

    public String getDefaultShareMessage() {
        return defaultShareMessage;
    }

    public String getTitleText() {
        return titleText;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public String getToolbarTitle() {
        return toolbarTitle;
    }

    public String getLogoPosition() {
        return logoPosition;
    }

    public String getLogo() {
        return logo;
    }

    public int getLogoResId() {
        return logoResId;
    }

    public int getHomeBackgroundColor() {
        return homeBackgroundColor;
    }

    public int getHomeWelcomeTitleColor() {
        return homeWelcomeTitleColor;
    }

    public float getHomeWelcomeTitleSize() {
        return homeWelcomeTitleSize;
    }

    public Typeface getHomeWelcomeTitleFont() {
        return homeWelcomeTitleFont;
    }

    public int getHomeWelcomeDescriptionColor() {
        return homeWelcomeDescriptionColor;
    }

    public float getHomeWelcomeDescriptionSize() {
        return homeWelcomeDescriptionSize;
    }

    public Typeface getHomeWelcomeDescriptionFont() {
        return homeWelcomeDescriptionFont;
    }

    public int getHomeToolbarColor() {
        return homeToolbarColor;
    }

    public int getHomeToolbarTextColor() {
        return homeToolbarTextColor;
    }

    public Typeface getHomeToolbarTextFont() {
        return homeToolbarTextFont;
    }

    public int getHomeToolbarArrowColor() {
        return homeToolbarArrowColor;
    }

    public int getHomeShareTextBar() {
        return homeShareTextBar;
    }

    public int getHomeShareTextColor() {
        return homeShareTextColor;
    }

    public float getHomeShareTextSize() {
        return homeShareTextSize;
    }

    public Typeface getHomeShareTextFont() {
        return homeShareTextFont;
    }

    public Typeface getSocialGridTextFont() {
        return socialGridTextFont;
    }

    public int getContactsListViewBackgroundColor() {
        return contactsListViewBackgroundColor;
    }

    public float getContactsListNameSize() {
        return contactsListNameSize;
    }

    public Typeface getContactsListNameFont() {
        return contactsListNameFont;
    }

    public float getContactsListValueSize() {
        return contactsListValueSize;
    }

    public Typeface getContactsListValueFont() {
        return contactsListValueFont;
    }

    public int getContactsSendBackground() {
        return contactsSendBackground;
    }

    public Typeface getContactSendMessageTextFont() {
        return contactSendMessageTextFont;
    }

    public int getContactsToolbarColor() {
        return contactsToolbarColor;
    }

    public int getContactsToolbarTextColor() {
        return contactsToolbarTextColor;
    }

    public int getContactsToolbarArrowColor() {
        return contactsToolbarArrowColor;
    }

    public int getContactsSendButtonColor() {
        return contactsSendButtonColor;
    }

    public int getContactsSendButtonTextColor() {
        return contactsSendButtonTextColor;
    }

    public int getContactsDoneButtonTextColor() {
        return contactsDoneButtonTextColor;
    }

    public int getContactsSearchBarColor() {
        return contactsSearchBarColor;
    }

    public int getContactsSearchIconColor() {
        return contactsSearchIconColor;
    }

    public int getContactNoPhotoAvailableBackgroundColor() {
        return contactNoPhotoAvailableBackgroundColor;
    }

    public int getLinkedInToolbarColor() {
        return linkedInToolbarColor;
    }

    public int getLinkedInToolbarTextColor() {
        return linkedInToolbarTextColor;
    }

    public int getLinkedInToolbarArrowColor() {
        return linkedInToolbarArrowColor;
    }

    public int getTwitterToolbarColor() {
        return twitterToolbarColor;
    }

    public int getTwitterToolbarTextColor() {
        return twitterToolbarTextColor;
    }

    public int getTwitterToolbarArrowColor() {
        return twitterToolbarArrowColor;
    }

    public String[] getChannels() {
        return channels;
    }

    public float getSocialOptionCornerRadius() {
        return socialOptionCornerRadius;
    }

    public String getWelcomeScreenTopBarText() {
        return welcomeScreenTopBarText;
    }

    public String getWelcomeScreenTitle() {
        return welcomeScreenTitle;
    }

    public String getWelcomeScreenMessage() {
        return welcomeScreenMessage;
    }

    public String getWelcomeScreenButtonText() {
        return welcomeScreenButtonText;
    }

    public String getWelcomeScreenLink1Text() {
        return welcomeScreenLink1Text;
    }

    public String getWelcomeScreenLink2Text() {
        return welcomeScreenLink2Text;
    }

    public int getWelcomeScreenColorTheme() {
        return welcomeScreenColorTheme;
    }

    public void setDefaultShareMessage(String defaultShareMessage) {
        this.defaultShareMessage = defaultShareMessage;
    }

    public static class Builder {

        private String defaultShareMessage = "Check out this company!";
        private String titleText = "RAF Params Welcome Title";
        private String descriptionText = "RAF Params Welcome Description";
        private String toolbarTitle = "RAF Params Toolbar Title";
        private String logoPosition = "0";
        private String logo = null;
        private int logoResId = -555;

        private int homeBackgroundColor = ResourceFactory.getColor(android.R.color.white).getColor();

        private int homeWelcomeTitleColor = ResourceFactory.getColor(R.color.lightGray).getColor();
        private float homeWelcomeTitleSize = 22;
        private Typeface homeWelcomeTitleFont;

        private int homeWelcomeDescriptionColor = ResourceFactory.getColor(R.color.lightGray).getColor();
        private float homeWelcomeDescriptionSize = 18;
        private Typeface homeWelcomeDescriptionFont;

        private int homeToolbarColor = ResourceFactory.getColor(R.color.ambassador_blue).getColor();
        private int homeToolbarTextColor = ResourceFactory.getColor(android.R.color.white).getColor();
        private Typeface homeToolbarTextFont;
        private int homeToolbarArrowColor = ResourceFactory.getColor(android.R.color.white).getColor();

        private int homeShareTextBar = ResourceFactory.getColor(R.color.ultraUltraUltraLightGray).getColor();
        private int homeShareTextColor = ResourceFactory.getColor(R.color.ultraLightGray).getColor();
        private float homeShareTextSize = 12;
        private Typeface homeShareTextFont;

        private Typeface socialGridTextFont;

        private int contactsListViewBackgroundColor = ResourceFactory.getColor(android.R.color.white).getColor();

        private float contactsListNameSize = 15;
        private Typeface contactsListNameFont;

        private float contactsListValueSize = 12;
        private Typeface contactsListValueFont;

        private int contactsSendBackground = ResourceFactory.getColor(android.R.color.white).getColor();
        private Typeface contactSendMessageTextFont;

        private int contactsToolbarColor = ResourceFactory.getColor(R.color.ambassador_blue).getColor();
        private int contactsToolbarTextColor = ResourceFactory.getColor(android.R.color.white).getColor();
        private int contactsToolbarArrowColor = ResourceFactory.getColor(android.R.color.white).getColor();

        private int contactsSendButtonColor = ResourceFactory.getColor(R.color.ambassador_blue).getColor();
        private int contactsSendButtonTextColor = ResourceFactory.getColor(android.R.color.white).getColor();

        private int contactsDoneButtonTextColor = ResourceFactory.getColor(R.color.ambassador_blue).getColor();

        private int contactsSearchBarColor = ResourceFactory.getColor(android.R.color.transparent).getColor();
        private int contactsSearchIconColor = ResourceFactory.getColor(android.R.color.white).getColor();

        private int contactNoPhotoAvailableBackgroundColor = ResourceFactory.getColor(R.color.ambassador_blue).getColor();

        private int linkedInToolbarColor = ResourceFactory.getColor(R.color.linkedin_blue).getColor();
        private int linkedInToolbarTextColor = ResourceFactory.getColor(android.R.color.white).getColor();
        private int linkedInToolbarArrowColor = ResourceFactory.getColor(android.R.color.white).getColor();

        private int twitterToolbarColor = ResourceFactory.getColor(R.color.twitter_blue).getColor();
        private int twitterToolbarTextColor = ResourceFactory.getColor(android.R.color.white).getColor();
        private int twitterToolbarArrowColor = ResourceFactory.getColor(android.R.color.white).getColor();

        private String[] channels = new String[]{"Facebook", "Twitter", "LinkedIn", "Email", "SMS"};

        private float socialOptionCornerRadius;

        private String welcomeScreenTopBarText = "Welcome";
        private String welcomeScreenTitle = "John Doe has referred you to Ambassador!";
        private String welcomeScreenMessage = "You understand the value of referrals. Maybe you've even explored referral marketing software.";
        private String welcomeScreenButtonText = "CREATE AN ACCOUNT";
        private String welcomeScreenLink1Text = "Testimonials";
        private String welcomeScreenLink2Text = "Request Demo";
        private int welcomeScreenColorTheme = Color.parseColor("#4198d1");

        public Builder() {}

        public void setDefaultShareMessage(String defaultShareMessage) {
            this.defaultShareMessage = defaultShareMessage;
        }

        public void setTitleText(String titleText) {
            this.titleText = titleText;
        }

        public void setDescriptionText(String descriptionText) {
            this.descriptionText = descriptionText;
        }

        public void setToolbarTitle(String toolbarTitle) {
            this.toolbarTitle = toolbarTitle;
        }

        public void setLogoPosition(String logoPosition) {
            this.logoPosition = logoPosition;
        }

        public Builder setLogo(String logo) {
            this.logo = logo;
            return this;
        }

        public Builder setLogoResId(int logoResId) {
            this.logoResId = logoResId;
            return this;
        }

        public void setHomeBackgroundColor(int homeBackgroundColor) {
            this.homeBackgroundColor = homeBackgroundColor;
        }

        public void setHomeWelcomeTitleColor(int homeWelcomeTitleColor) {
            this.homeWelcomeTitleColor = homeWelcomeTitleColor;
        }

        public void setHomeWelcomeTitleSize(float homeWelcomeTitleSize) {
            this.homeWelcomeTitleSize = homeWelcomeTitleSize;
        }

        public void setHomeWelcomeTitleFont(Typeface homeWelcomeTitleFont) {
            this.homeWelcomeTitleFont = homeWelcomeTitleFont;
        }

        public void setHomeWelcomeDescriptionColor(int homeWelcomeDescriptionColor) {
            this.homeWelcomeDescriptionColor = homeWelcomeDescriptionColor;
        }

        public void setHomeWelcomeDescriptionSize(float homeWelcomeDescriptionSize) {
            this.homeWelcomeDescriptionSize = homeWelcomeDescriptionSize;
        }

        public void setHomeWelcomeDescriptionFont(Typeface homeWelcomeDescriptionFont) {
            this.homeWelcomeDescriptionFont = homeWelcomeDescriptionFont;
        }

        public void setHomeToolbarColor(int homeToolbarColor) {
            this.homeToolbarColor = homeToolbarColor;
        }

        public void setHomeToolbarTextColor(int homeToolbarTextColor) {
            this.homeToolbarTextColor = homeToolbarTextColor;
        }

        public void setHomeToolbarTextFont(Typeface homeToolbarTextFont) {
            this.homeToolbarTextFont = homeToolbarTextFont;
        }

        public void setHomeToolbarArrowColor(int homeToolbarArrowColor) {
            this.homeToolbarArrowColor = homeToolbarArrowColor;
        }

        public void setHomeShareTextBar(int homeShareTextBar) {
            this.homeShareTextBar = homeShareTextBar;
        }

        public void setHomeShareTextColor(int homeShareTextColor) {
            this.homeShareTextColor = homeShareTextColor;
        }

        public void setHomeShareTextSize(float homeShareTextSize) {
            this.homeShareTextSize = homeShareTextSize;
        }

        public void setHomeShareTextFont(Typeface homeShareTextFont) {
            this.homeShareTextFont = homeShareTextFont;
        }

        public void setSocialGridTextFont(Typeface socialGridTextFont) {
            this.socialGridTextFont = socialGridTextFont;
        }

        public void setContactsListViewBackgroundColor(int contactsListViewBackgroundColor) {
            this.contactsListViewBackgroundColor = contactsListViewBackgroundColor;
        }

        public void setContactsListNameSize(float contactsListNameSize) {
            this.contactsListNameSize = contactsListNameSize;
        }

        public void setContactsListNameFont(Typeface contactsListNameFont) {
            this.contactsListNameFont = contactsListNameFont;
        }

        public void setContactsListValueSize(float contactsListValueSize) {
            this.contactsListValueSize = contactsListValueSize;
        }

        public void setContactsListValueFont(Typeface contactsListValueFont) {
            this.contactsListValueFont = contactsListValueFont;
        }

        public void setContactsSendBackground(int contactsSendBackground) {
            this.contactsSendBackground = contactsSendBackground;
        }

        public void setContactSendMessageTextFont(Typeface contactSendMessageTextFont) {
            this.contactSendMessageTextFont = contactSendMessageTextFont;
        }

        public void setContactsToolbarColor(int contactsToolbarColor) {
            this.contactsToolbarColor = contactsToolbarColor;
        }

        public void setContactsToolbarTextColor(int contactsToolbarTextColor) {
            this.contactsToolbarTextColor = contactsToolbarTextColor;
        }

        public void setContactsToolbarArrowColor(int contactsToolbarArrowColor) {
            this.contactsToolbarArrowColor = contactsToolbarArrowColor;
        }

        public void setContactsSendButtonColor(int contactsSendButtonColor) {
            this.contactsSendButtonColor = contactsSendButtonColor;
        }

        public void setContactsSendButtonTextColor(int contactsSendButtonTextColor) {
            this.contactsSendButtonTextColor = contactsSendButtonTextColor;
        }

        public void setContactsDoneButtonTextColor(int contactsDoneButtonTextColor) {
            this.contactsDoneButtonTextColor = contactsDoneButtonTextColor;
        }

        public void setContactsSearchBarColor(int contactsSearchBarColor) {
            this.contactsSearchBarColor = contactsSearchBarColor;
        }

        public void setContactsSearchIconColor(int contactsSearchIconColor) {
            this.contactsSearchIconColor = contactsSearchIconColor;
        }

        public void setContactNoPhotoAvailableBackgroundColor(int contactNoPhotoAvailableBackgroundColor) {
            this.contactNoPhotoAvailableBackgroundColor = contactNoPhotoAvailableBackgroundColor;
        }

        public void setLinkedInToolbarColor(int linkedInToolbarColor) {
            this.linkedInToolbarColor = linkedInToolbarColor;
        }

        public void setLinkedInToolbarTextColor(int linkedInToolbarTextColor) {
            this.linkedInToolbarTextColor = linkedInToolbarTextColor;
        }

        public void setLinkedInToolbarArrowColor(int linkedInToolbarArrowColor) {
            this.linkedInToolbarArrowColor = linkedInToolbarArrowColor;
        }

        public void setTwitterToolbarColor(int twitterToolbarColor) {
            this.twitterToolbarColor = twitterToolbarColor;
        }

        public void setTwitterToolbarTextColor(int twitterToolbarTextColor) {
            this.twitterToolbarTextColor = twitterToolbarTextColor;
        }

        public void setTwitterToolbarArrowColor(int twitterToolbarArrowColor) {
            this.twitterToolbarArrowColor = twitterToolbarArrowColor;
        }

        public void setChannels(String[] channels) {
            this.channels = channels;
        }

        public void setSocialOptionCornerRadius(float socialOptionCornerRadius) {
            this.socialOptionCornerRadius = socialOptionCornerRadius;
        }

        public Builder setWelcomeScreenTopBarText(String welcomeScreenTopBarText) {
            this.welcomeScreenTopBarText = welcomeScreenTopBarText;
            return this;
        }

        public Builder setWelcomeScreenTitle(String welcomeScreenTitle) {
            this.welcomeScreenTitle = welcomeScreenTitle;
            return this;
        }

        public Builder setWelcomeScreenMessage(String welcomeScreenMessage) {
            this.welcomeScreenMessage = welcomeScreenMessage;
            return this;
        }

        public Builder setWelcomeScreenButtonText(String welcomeScreenButtonText) {
            this.welcomeScreenButtonText = welcomeScreenButtonText;
            return this;
        }

        public Builder setWelcomeScreenLink1Text(String welcomeScreenLink1Text) {
            this.welcomeScreenLink1Text = welcomeScreenLink1Text;
            return this;
        }

        public Builder setWelcomeScreenLink2Text(String welcomeScreenLink2Text) {
            this.welcomeScreenLink2Text = welcomeScreenLink2Text;
            return this;
        }

        public Builder setWelcomeScreenColorTheme(int welcomeScreenColorTheme) {
            this.welcomeScreenColorTheme = welcomeScreenColorTheme;
            return this;
        }

        @NonNull
        public RAFOptions build() {
            RAFOptions tmp = new RAFOptions();

            tmp.defaultShareMessage = this.defaultShareMessage;
            tmp.titleText = this.titleText;
            tmp.descriptionText = this.descriptionText;
            tmp.toolbarTitle = this.toolbarTitle;
            tmp.logoPosition = this.logoPosition;
            tmp.logo = this.logo;
            tmp.logoResId = this.logoResId;
            tmp.homeBackgroundColor = this.homeBackgroundColor;
            tmp.homeWelcomeTitleColor = this.homeWelcomeTitleColor;
            tmp.homeWelcomeTitleSize = this.homeWelcomeTitleSize;
            tmp.homeWelcomeTitleFont = this.homeWelcomeTitleFont;
            tmp.homeWelcomeDescriptionColor = this.homeWelcomeDescriptionColor;
            tmp.homeWelcomeDescriptionSize = this.homeWelcomeDescriptionSize;
            tmp.homeWelcomeDescriptionFont = this.homeWelcomeDescriptionFont;
            tmp.homeToolbarColor = this.homeToolbarColor;
            tmp.homeToolbarTextColor = this.homeToolbarTextColor;
            tmp.homeToolbarTextFont = this.homeToolbarTextFont;
            tmp.homeToolbarArrowColor = this.homeToolbarArrowColor;
            tmp.homeShareTextBar = this.homeShareTextBar;
            tmp.homeShareTextColor = this.homeShareTextColor;
            tmp.homeShareTextSize = this.homeShareTextSize;
            tmp.homeShareTextFont = this.homeShareTextFont;
            tmp.socialGridTextFont = this.socialGridTextFont;
            tmp.contactsListViewBackgroundColor = this.contactsListViewBackgroundColor;
            tmp.contactsListNameSize = this.contactsListNameSize;
            tmp.contactsListNameFont = this.contactsListNameFont;
            tmp.contactsListValueSize = this.contactsListValueSize;
            tmp.contactsListValueFont = this.contactsListValueFont;
            tmp.contactsSendBackground = this.contactsSendBackground;
            tmp.contactSendMessageTextFont = this.contactSendMessageTextFont;
            tmp.contactsToolbarColor = this.contactsToolbarColor;
            tmp.contactsToolbarTextColor = this.contactsToolbarTextColor;
            tmp.contactsToolbarArrowColor = this.contactsToolbarArrowColor;
            tmp.contactsSendButtonColor = this.contactsSendButtonColor;
            tmp.contactsSendButtonTextColor = this.contactsSendButtonTextColor;
            tmp.contactsDoneButtonTextColor = this.contactsDoneButtonTextColor;
            tmp.contactsSearchBarColor = this.contactsSearchBarColor;
            tmp.contactsSearchIconColor = this.contactsSearchIconColor;
            tmp.contactNoPhotoAvailableBackgroundColor = this.contactNoPhotoAvailableBackgroundColor;
            tmp.linkedInToolbarColor = this.linkedInToolbarColor;
            tmp.linkedInToolbarTextColor = this.linkedInToolbarTextColor;
            tmp.linkedInToolbarArrowColor = this.linkedInToolbarArrowColor;
            tmp.twitterToolbarColor = this.twitterToolbarColor;
            tmp.twitterToolbarTextColor = this.twitterToolbarTextColor;
            tmp.twitterToolbarArrowColor = this.twitterToolbarArrowColor;
            tmp.channels = this.channels;
            tmp.socialOptionCornerRadius = this.socialOptionCornerRadius;
            tmp.welcomeScreenTopBarText = this.welcomeScreenTopBarText;
            tmp.welcomeScreenTitle = this.welcomeScreenTitle;
            tmp.welcomeScreenMessage = this.welcomeScreenMessage;
            tmp.welcomeScreenButtonText = this.welcomeScreenButtonText;
            tmp.welcomeScreenLink1Text = this.welcomeScreenLink1Text;
            tmp.welcomeScreenLink2Text = this.welcomeScreenLink2Text;
            tmp.welcomeScreenColorTheme = this.welcomeScreenColorTheme;

            return tmp;
        }

        @NonNull
        public static RAFOptions.Builder newInstance() {
            return new RAFOptions.Builder();
        }

    }

    public static void set(@NonNull RAFOptions rafOptions) {
        instance = rafOptions;
        String campaignId = new Campaign().getId();
        String data = new Gson().toJson(rafOptions);
        if (AmbSingleton.getContext() != null) {
            AmbSingleton.getContext().getSharedPreferences("rafOptions", Context.MODE_PRIVATE).edit().putString(campaignId, data).apply();
        }
    }

    @NonNull
    public static RAFOptions get() {
        if (instance == null) {
            String campaignId = new Campaign().getId();
            SharedPreferences prefs = AmbSingleton.getContext().getSharedPreferences("rafOptions", Context.MODE_PRIVATE);
            String data = prefs.getString(campaignId, null);
            if (data != null) {
                instance = new Gson().fromJson(data, RAFOptions.class);
            } else {
                instance = RAFOptions.Builder.newInstance().build();
            }
        }
        return instance;
    }

}
