package com.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.factories.ResourceFactory;
import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Stores design options set by the 3rd party developer. Used throughout codebase. Can be instantiated
 * directly using RAFOptions.Builder or indirectly with an XML file (see RAFOptionsFactory).
 */
@Singleton
public class RAFOptions {
    @Inject
    protected AmbSingleton AmbSingleton;

    /** The RAFOptions instantiation inflated with options from 3rd party and used throughout code. */
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

    private String[] channels;

    private float socialOptionCornerRadius;

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

    public String[] getChannels() {
        return channels;
    }

    public float getSocialOptionCornerRadius() {
        return socialOptionCornerRadius;
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

        private String[] channels = new String[]{"Facebook", "Twitter", "LinkedIn", "Email", "SMS"};

        private float socialOptionCornerRadius;

        public Builder() {}

        public Builder setDefaultShareMessage(String defaultShareMessage) {
            this.defaultShareMessage = defaultShareMessage;
            return this;
        }

        public Builder setTitleText(String titleText) {
            this.titleText = titleText;
            return this;
        }

        public Builder setDescriptionText(String descriptionText) {
            this.descriptionText = descriptionText;
            return this;
        }

        public Builder setToolbarTitle(String toolbarTitle) {
            this.toolbarTitle = toolbarTitle;
            return this;
        }

        public Builder setLogoPosition(String logoPosition) {
            this.logoPosition = logoPosition;
            return this;
        }

        public Builder setLogo(String logo) {
            this.logo = logo;
            return this;
        }

        public Builder setLogoResId(int logoResId) {
            this.logoResId = logoResId;
            return this;
        }

        public Builder setHomeBackgroundColor(int homeBackgroundColor) {
            this.homeBackgroundColor = homeBackgroundColor;
            return this;
        }

        public Builder setHomeWelcomeTitleColor(int homeWelcomeTitleColor) {
            this.homeWelcomeTitleColor = homeWelcomeTitleColor;
            return this;
        }

        public Builder setHomeWelcomeTitleSize(float homeWelcomeTitleSize) {
            this.homeWelcomeTitleSize = homeWelcomeTitleSize;
            return this;
        }

        public Builder setHomeWelcomeTitleFont(Typeface homeWelcomeTitleFont) {
            this.homeWelcomeTitleFont = homeWelcomeTitleFont;
            return this;
        }

        public Builder setHomeWelcomeDescriptionColor(int homeWelcomeDescriptionColor) {
            this.homeWelcomeDescriptionColor = homeWelcomeDescriptionColor;
            return this;
        }

        public Builder setHomeWelcomeDescriptionSize(float homeWelcomeDescriptionSize) {
            this.homeWelcomeDescriptionSize = homeWelcomeDescriptionSize;
            return this;
        }

        public Builder setHomeWelcomeDescriptionFont(Typeface homeWelcomeDescriptionFont) {
            this.homeWelcomeDescriptionFont = homeWelcomeDescriptionFont;
            return this;
        }

        public Builder setHomeToolbarColor(int homeToolbarColor) {
            this.homeToolbarColor = homeToolbarColor;
            return this;
        }

        public Builder setHomeToolbarTextColor(int homeToolbarTextColor) {
            this.homeToolbarTextColor = homeToolbarTextColor;
            return this;
        }

        public Builder setHomeToolbarTextFont(Typeface homeToolbarTextFont) {
            this.homeToolbarTextFont = homeToolbarTextFont;
            return this;
        }

        public Builder setHomeToolbarArrowColor(int homeToolbarArrowColor) {
            this.homeToolbarArrowColor = homeToolbarArrowColor;
            return this;
        }

        public Builder setHomeShareTextBar(int homeShareTextBar) {
            this.homeShareTextBar = homeShareTextBar;
            return this;
        }

        public Builder setHomeShareTextColor(int homeShareTextColor) {
            this.homeShareTextColor = homeShareTextColor;
            return this;
        }

        public Builder setHomeShareTextSize(float homeShareTextSize) {
            this.homeShareTextSize = homeShareTextSize;
            return this;
        }

        public Builder setHomeShareTextFont(Typeface homeShareTextFont) {
            this.homeShareTextFont = homeShareTextFont;
            return this;
        }

        public Builder setSocialGridTextFont(Typeface socialGridTextFont) {
            this.socialGridTextFont = socialGridTextFont;
            return this;
        }

        public Builder setContactsListViewBackgroundColor(int contactsListViewBackgroundColor) {
            this.contactsListViewBackgroundColor = contactsListViewBackgroundColor;
            return this;
        }

        public Builder setContactsListNameSize(float contactsListNameSize) {
            this.contactsListNameSize = contactsListNameSize;
            return this;
        }

        public Builder setContactsListNameFont(Typeface contactsListNameFont) {
            this.contactsListNameFont = contactsListNameFont;
            return this;
        }

        public Builder setContactsListValueSize(float contactsListValueSize) {
            this.contactsListValueSize = contactsListValueSize;
            return this;
        }

        public Builder setContactsListValueFont(Typeface contactsListValueFont) {
            this.contactsListValueFont = contactsListValueFont;
            return this;
        }

        public Builder setContactsSendBackground(int contactsSendBackground) {
            this.contactsSendBackground = contactsSendBackground;
            return this;
        }

        public Builder setContactSendMessageTextFont(Typeface contactSendMessageTextFont) {
            this.contactSendMessageTextFont = contactSendMessageTextFont;
            return this;
        }

        public Builder setContactsToolbarColor(int contactsToolbarColor) {
            this.contactsToolbarColor = contactsToolbarColor;
            return this;
        }

        public Builder setContactsToolbarTextColor(int contactsToolbarTextColor) {
            this.contactsToolbarTextColor = contactsToolbarTextColor;
            return this;
        }

        public Builder setContactsToolbarArrowColor(int contactsToolbarArrowColor) {
            this.contactsToolbarArrowColor = contactsToolbarArrowColor;
            return this;
        }

        public Builder setContactsSendButtonColor(int contactsSendButtonColor) {
            this.contactsSendButtonColor = contactsSendButtonColor;
            return this;
        }

        public Builder setContactsSendButtonTextColor(int contactsSendButtonTextColor) {
            this.contactsSendButtonTextColor = contactsSendButtonTextColor;
            return this;
        }

        public Builder setContactsDoneButtonTextColor(int contactsDoneButtonTextColor) {
            this.contactsDoneButtonTextColor = contactsDoneButtonTextColor;
            return this;
        }

        public Builder setContactsSearchBarColor(int contactsSearchBarColor) {
            this.contactsSearchBarColor = contactsSearchBarColor;
            return this;
        }

        public Builder setContactsSearchIconColor(int contactsSearchIconColor) {
            this.contactsSearchIconColor = contactsSearchIconColor;
            return this;
        }

        public Builder setContactNoPhotoAvailableBackgroundColor(int contactNoPhotoAvailableBackgroundColor) {
            this.contactNoPhotoAvailableBackgroundColor = contactNoPhotoAvailableBackgroundColor;
            return this;
        }

        public Builder setChannels(String[] channels) {
            this.channels = channels;
            return this;
        }

        public Builder setSocialOptionCornerRadius(float socialOptionCornerRadius) {
            this.socialOptionCornerRadius = socialOptionCornerRadius;
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
            tmp.channels = this.channels;
            tmp.socialOptionCornerRadius = this.socialOptionCornerRadius;

            return tmp;
        }

        @NonNull
        public static RAFOptions.Builder newInstance() {
            return new RAFOptions.Builder();
        }

    }

    /**
     * Sets the instance RAFOptions. Will also be serialized into JSON and stored in SharedPreferences
     * keyed on the current campaign ID.
     * @param rafOptions the RAFOptions instantiation to store. Don't pass null.
     */
    public void set(@NonNull RAFOptions rafOptions) {
        String campaignId = new Campaign().getId();
        String data = new Gson().toJson(rafOptions);
       AmbSingleton.getInstance().getContext().getSharedPreferences("rafOptions", Context.MODE_PRIVATE).edit().putString(campaignId, data).apply();
    }

    /**
     * Returns an instance of RAFOptions. This will never be null and will always be populated. If the
     * stored instance is not null it will be returned directly. If the stored instance is null the
     * RAFOptions are either not set or the instance has been lost. First method will attempt to
     * un-serialize RAFOptions that may be stored in SharedPreferences and set as the instance. If not
     * successful the method will set instance as the default RAFOptions.
     * @return a NonNull RAFOptions object.
     */
    @NonNull
    public RAFOptions get() {
        String campaignId = new Campaign().getId();
        SharedPreferences prefs =AmbSingleton.getInstance().getContext().getSharedPreferences("rafOptions", Context.MODE_PRIVATE);
        String data = prefs.getString(campaignId, null);
        RAFOptions rafOptions = (data!= null) ? new Gson().fromJson(data, RAFOptions.class) : RAFOptions.Builder.newInstance().build();

        return rafOptions;
    }
}
