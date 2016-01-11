package com.ambassador.ambassadorsdk;

import android.graphics.Typeface;

public class RAFOptions {
    
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

    public RAFOptions() {}

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

        RAFOptions rafOptions;

        public Builder() {
            rafOptions = new RAFOptions();
        }

        public void setDefaultShareMessage(String defaultShareMessage) {
            rafOptions.defaultShareMessage = defaultShareMessage;
        }

        public void setTitleText(String titleText) {
            rafOptions.titleText = titleText;
        }

        public void setDescriptionText(String descriptionText) {
            rafOptions.descriptionText = descriptionText;
        }

        public void setToolbarTitle(String toolbarTitle) {
            rafOptions.toolbarTitle = toolbarTitle;
        }

        public void setLogoPosition(String logoPosition) {
            rafOptions.logoPosition = logoPosition;
        }

        public void setHomeBackgroundColor(int homeBackgroundColor) {
            rafOptions.homeBackgroundColor = homeBackgroundColor;
        }

        public void setHomeWelcomeTitleColor(int homeWelcomeTitleColor) {
            rafOptions.homeWelcomeTitleColor = homeWelcomeTitleColor;
        }

        public void setHomeWelcomeTitleSize(float homeWelcomeTitleSize) {
            rafOptions.homeWelcomeTitleSize = homeWelcomeTitleSize;
        }

        public void setHomeWelcomeTitleFont(Typeface homeWelcomeTitleFont) {
            rafOptions.homeWelcomeTitleFont = homeWelcomeTitleFont;
        }

        public void setHomeWelcomeDescriptionColor(int homeWelcomeDescriptionColor) {
            rafOptions.homeWelcomeDescriptionColor = homeWelcomeDescriptionColor;
        }

        public void setHomeWelcomeDescriptionSize(float homeWelcomeDescriptionSize) {
            rafOptions.homeWelcomeDescriptionSize = homeWelcomeDescriptionSize;
        }

        public void setHomeWelcomeDescriptionFont(Typeface homeWelcomeDescriptionFont) {
            rafOptions.homeWelcomeDescriptionFont = homeWelcomeDescriptionFont;
        }

        public void setHomeToolbarColor(int homeToolbarColor) {
            rafOptions.homeToolbarColor = homeToolbarColor;
        }

        public void setHomeToolbarTextColor(int homeToolbarTextColor) {
            rafOptions.homeToolbarTextColor = homeToolbarTextColor;
        }

        public void setHomeToolbarTextFont(Typeface homeToolbarTextFont) {
            rafOptions.homeToolbarTextFont = homeToolbarTextFont;
        }

        public void setHomeToolbarArrowColor(int homeToolbarArrowColor) {
            rafOptions.homeToolbarArrowColor = homeToolbarArrowColor;
        }

        public void setHomeShareTextBar(int homeShareTextBar) {
            rafOptions.homeShareTextBar = homeShareTextBar;
        }

        public void setHomeShareTextColor(int homeShareTextColor) {
            rafOptions.homeShareTextColor = homeShareTextColor;
        }

        public void setHomeShareTextSize(float homeShareTextSize) {
            rafOptions.homeShareTextSize = homeShareTextSize;
        }

        public void setHomeShareTextFont(Typeface homeShareTextFont) {
            rafOptions.homeShareTextFont = homeShareTextFont;
        }

        public void setSocialGridTextFont(Typeface socialGridTextFont) {
            rafOptions.socialGridTextFont = socialGridTextFont;
        }

        public void setContactsListViewBackgroundColor(int contactsListViewBackgroundColor) {
            rafOptions.contactsListViewBackgroundColor = contactsListViewBackgroundColor;
        }

        public void setContactsListNameSize(float contactsListNameSize) {
            rafOptions.contactsListNameSize = contactsListNameSize;
        }

        public void setContactsListNameFont(Typeface contactsListNameFont) {
            rafOptions.contactsListNameFont = contactsListNameFont;
        }

        public void setContactsListValueSize(float contactsListValueSize) {
            rafOptions.contactsListValueSize = contactsListValueSize;
        }

        public void setContactsListValueFont(Typeface contactsListValueFont) {
            rafOptions.contactsListValueFont = contactsListValueFont;
        }

        public void setContactsSendBackground(int contactsSendBackground) {
            rafOptions.contactsSendBackground = contactsSendBackground;
        }

        public void setContactSendMessageTextFont(Typeface contactSendMessageTextFont) {
            rafOptions.contactSendMessageTextFont = contactSendMessageTextFont;
        }

        public void setContactsToolbarColor(int contactsToolbarColor) {
            rafOptions.contactsToolbarColor = contactsToolbarColor;
        }

        public void setContactsToolbarTextColor(int contactsToolbarTextColor) {
            rafOptions.contactsToolbarTextColor = contactsToolbarTextColor;
        }

        public void setContactsToolbarArrowColor(int contactsToolbarArrowColor) {
            rafOptions.contactsToolbarArrowColor = contactsToolbarArrowColor;
        }

        public void setContactsSendButtonColor(int contactsSendButtonColor) {
            rafOptions.contactsSendButtonColor = contactsSendButtonColor;
        }

        public void setContactsSendButtonTextColor(int contactsSendButtonTextColor) {
            rafOptions.contactsSendButtonTextColor = contactsSendButtonTextColor;
        }

        public void setContactsDoneButtonTextColor(int contactsDoneButtonTextColor) {
            rafOptions.contactsDoneButtonTextColor = contactsDoneButtonTextColor;
        }

        public void setContactsSearchBarColor(int contactsSearchBarColor) {
            rafOptions.contactsSearchBarColor = contactsSearchBarColor;
        }

        public void setContactsSearchIconColor(int contactsSearchIconColor) {
            rafOptions.contactsSearchIconColor = contactsSearchIconColor;
        }

        public void setContactNoPhotoAvailableBackgroundColor(int contactNoPhotoAvailableBackgroundColor) {
            rafOptions.contactNoPhotoAvailableBackgroundColor = contactNoPhotoAvailableBackgroundColor;
        }

        public void setLinkedInToolbarColor(int linkedInToolbarColor) {
            rafOptions.linkedInToolbarColor = linkedInToolbarColor;
        }

        public void setLinkedInToolbarTextColor(int linkedInToolbarTextColor) {
            rafOptions.linkedInToolbarTextColor = linkedInToolbarTextColor;
        }

        public void setLinkedInToolbarArrowColor(int linkedInToolbarArrowColor) {
            rafOptions.linkedInToolbarArrowColor = linkedInToolbarArrowColor;
        }

        public RAFOptions build() {
            return rafOptions;
        }

    }

}
