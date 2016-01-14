package com.ambassador.ambassadorsdk;

import android.graphics.Typeface;

public final class RAFOptions {

    private static RAFOptions instance;
    
    private String defaultShareMessage;
    private String titleText;
    private String descriptionText;
    private String toolbarTitle;
    private String logoPosition;

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

    private String[] channels;

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

    public int getLinkedinToolbarColor() {
        return linkedInToolbarColor;
    }

    public int getLinkedInToolbarTextColor() {
        return linkedInToolbarTextColor;
    }

    public int getLinkedInToolbarArrowColor() {
        return linkedInToolbarArrowColor;
    }

    public String[] getChannels() {
        return channels;
    }

    public static class Builder {

        private String defaultShareMessage = "Check out this company!";
        private String titleText = "RAF Params Welcome Title";
        private String descriptionText = "RAF Params Welcome Description";
        private String toolbarTitle = "RAF Params Toolbar Title";
        private String logoPosition = "0";

        private int homeBackgroundColor = android.R.color.white;

        private int homeWelcomeTitleColor = R.color.lightGray;
        private float homeWelcomeTitleSize = 22;
        private Typeface homeWelcomeTitleFont;

        private int homeWelcomeDescriptionColor = R.color.lightGray;
        private float homeWelcomeDescriptionSize = 18;
        private Typeface homeWelcomeDescriptionFont;

        private int homeToolbarColor = R.color.ambassador_blue;
        private int homeToolbarTextColor = android.R.color.white;
        private Typeface homeToolbarTextFont;
        private int homeToolbarArrowColor = android.R.color.white;

        private int homeShareTextBar = R.color.ultraUltraUltraLightGray;
        private int homeShareTextColor = R.color.ultraLightGray;
        private float homeShareTextSize = 12;
        private Typeface homeShareTextFont;

        private Typeface socialGridTextFont;

        private int contactsListViewBackgroundColor = android.R.color.white;

        private float contactsListNameSize = 15;
        private Typeface contactsListNameFont;

        private float contactsListValueSize = 12;
        private Typeface contactsListValueFont;

        private int contactsSendBackground = android.R.color.white;
        private Typeface contactSendMessageTextFont;

        private int contactsToolbarColor = R.color.ambassador_blue;
        private int contactsToolbarTextColor = android.R.color.white;
        private int contactsToolbarArrowColor = android.R.color.white;

        private int contactsSendButtonColor = R.color.ambassador_blue;
        private int contactsSendButtonTextColor = android.R.color.white;

        private int contactsDoneButtonTextColor = R.color.ambassador_blue;

        private int contactsSearchBarColor = android.R.color.transparent;
        private int contactsSearchIconColor = android.R.color.white;

        private int contactNoPhotoAvailableBackgroundColor = R.color.ambassador_blue;

        private int linkedInToolbarColor = R.color.linkedin_blue;
        private int linkedInToolbarTextColor = android.R.color.white;
        private int linkedInToolbarArrowColor = android.R.color.white;

        private String[] channels = new String[]{"Facebook", "Twitter", "LinkedIn", "Email", "SMS"};

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

        public void setChannels(String[] channels) {
            this.channels = channels;
        }

        public RAFOptions build() {
            RAFOptions tmp = new RAFOptions();

            tmp.defaultShareMessage = this.defaultShareMessage;
            tmp.titleText = this.titleText;
            tmp.descriptionText = this.descriptionText;
            tmp.toolbarTitle = this.toolbarTitle;
            tmp.logoPosition = this.logoPosition;
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
            tmp.channels = this.channels;

            return tmp;
        }

    }

    public static void set(RAFOptions rafOptions) {
        instance = rafOptions;
    }

    public static RAFOptions get() {
        return instance;
    }

}
