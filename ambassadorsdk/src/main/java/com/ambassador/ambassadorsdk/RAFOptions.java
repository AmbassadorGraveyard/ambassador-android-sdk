package com.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.factories.ResourceFactory;
import com.google.gson.Gson;

/**
 * Stores design options set by the 3rd party developer. Used throughout codebase. Can be instantiated
 * directly using RAFOptions.Builder or indirectly with an XML file (see RAFOptionsFactory).
 */
public final class RAFOptions {

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

    private int linkedInToolbarColor;
    private int linkedInToolbarTextColor;
    private int linkedInToolbarArrowColor;

    private int twitterToolbarColor;
    private int twitterToolbarTextColor;
    private int twitterToolbarArrowColor;

    private String[] channels;

    private float socialOptionCornerRadius;

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

            return tmp;
        }

        @NonNull
        public static RAFOptions.Builder newInstance() {
            return new RAFOptions.Builder();
        }

    }

    /**
     * Generates a String XML representation of the RAFOptions. Same as defaultOptions.xml and what
     * is in the README.
     * @return the String XML representation.
     */
    @NonNull
    public String getXmlRepresentation() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        stringBuilder.append("<resources>\n");

        stringBuilder.append(getAttributeString("string", "RAFdefaultShareMessage", getDefaultShareMessage()));
        stringBuilder.append(getAttributeString("string", "RAFtitleText", getTitleText()));
        stringBuilder.append(getAttributeString("string", "RAFLogoPosition", getLogoPosition()));
        stringBuilder.append(getAttributeString("string", "RAFLogo", getLogo()));
        stringBuilder.append(getAttributeString("color", "homeBackground", getHomeBackgroundColor()));
        stringBuilder.append(getAttributeString("color", "homeWelcomeTitle", getHomeWelcomeTitleColor()));
        stringBuilder.append(getAttributeString("dimen", "homeWelcomeTitle", getHomeWelcomeTitleSize()));
        stringBuilder.append(getAttributeString("string", "homeWelcomeTitle", getHomeWelcomeTitleFont()));
        stringBuilder.append(getAttributeString("color", "homeWelcomeDesc", getHomeWelcomeDescriptionColor()));
        stringBuilder.append(getAttributeString("dimen", "homeWelcomeDesc", getHomeWelcomeDescriptionSize()));
        stringBuilder.append(getAttributeString("string", "homeWelcomeDesc", getHomeWelcomeDescriptionFont()));
        stringBuilder.append(getAttributeString("color", "homeToolBar", getHomeToolbarColor()));
        stringBuilder.append(getAttributeString("color", "homeToolBarText", getHomeToolbarTextColor()));
        stringBuilder.append(getAttributeString("string", "homeToolBarText", getHomeToolbarTextFont()));
        stringBuilder.append(getAttributeString("color", "homeToolBarArrow", getHomeToolbarArrowColor()));
        stringBuilder.append(getAttributeString("color", "homeShareTextBar", getHomeShareTextBar()));
        stringBuilder.append(getAttributeString("color", "homeShareText", getHomeShareTextColor()));
        stringBuilder.append(getAttributeString("dimen", "homeShareText", getHomeShareTextSize()));
        stringBuilder.append(getAttributeString("string", "homeShareText", getHomeShareTextFont()));
        stringBuilder.append(getAttributeString("string", "socialGridText", getSocialGridTextFont()));
        stringBuilder.append(getAttributeString("dimen", "socialOptionCornerRadius", getSocialOptionCornerRadius()));

        stringBuilder.append("<array name=\"channels\">\n");
            for (String channel : getChannels()) {
                stringBuilder.append("<item>");
                stringBuilder.append(channel);
                stringBuilder.append("</item>\n");
            }
        stringBuilder.append("</array>\n");

        stringBuilder.append(getAttributeString("color", "contactsListViewBackground", getContactsListViewBackgroundColor()));
        stringBuilder.append(getAttributeString("dimen", "contactsListName", getContactsListNameSize()));
        stringBuilder.append(getAttributeString("string", "contactsListName", getContactsListNameFont()));
        stringBuilder.append(getAttributeString("dimen", "contactsListValue", getContactsListValueSize()));
        stringBuilder.append(getAttributeString("string", "contactsListValue", getContactsListValueFont()));
        stringBuilder.append(getAttributeString("color", "contactsSendBackground", getContactsSendBackground()));
        stringBuilder.append(getAttributeString("string", "contactSendMessageText", getContactSendMessageTextFont()));
        stringBuilder.append(getAttributeString("color", "contactsToolBar", getContactsToolbarColor()));
        stringBuilder.append(getAttributeString("color", "contactsToolBarText", getContactsToolbarTextColor()));
        stringBuilder.append(getAttributeString("color", "contactsToolBarArrow", getContactsToolbarArrowColor()));
        stringBuilder.append(getAttributeString("color", "contactsSendButton", getContactsSendButtonColor()));
        stringBuilder.append(getAttributeString("color", "contactsSendButtonText", getContactsSendButtonTextColor()));
        stringBuilder.append(getAttributeString("color", "contactsDoneButtonText", getContactsDoneButtonTextColor()));
        stringBuilder.append(getAttributeString("color", "contactsSearchBar", getContactsSearchBarColor()));
        stringBuilder.append(getAttributeString("color", "contactsSearchIcon", getContactsSearchIconColor()));
        stringBuilder.append(getAttributeString("color", "contactNoPhotoAvailableBackground", getContactNoPhotoAvailableBackgroundColor()));
        stringBuilder.append(getAttributeString("color", "linkedinToolBar", getLinkedInToolbarColor()));
        stringBuilder.append(getAttributeString("color", "linkedinToolBarText", getLinkedInToolbarTextColor()));
        stringBuilder.append(getAttributeString("color", "linkedinToolBarArrow", getLinkedInToolbarArrowColor()));
        stringBuilder.append(getAttributeString("color", "twitterToolBar", getTwitterToolbarColor()));
        stringBuilder.append(getAttributeString("color", "twitterToolBarText", getTwitterToolbarTextColor()));
        stringBuilder.append(getAttributeString("color", "twitterToolBarArrow", getTwitterToolbarArrowColor()));

        stringBuilder.append("</resources>\n");
        return stringBuilder.toString();
    }

    /**
     * Generates a standard tag that most of the RAF options are formatted like.
     * @param tag the tag name (color, string, etc).
     * @param name the name attribute in the tag
     * @param value the value inside of the tag
     * @return the String representation of the tag with a new line at the end.
     */
    protected String getAttributeString(String tag, String name, String value) {
        StringBuilder string = new StringBuilder();
        string.append("    <");
        string.append(tag);
        string.append(" name=\"");
        string.append(name);
        string.append("\"");
        string.append(">");
        string.append(value);
        string.append("</");
        string.append(tag);
        string.append(">\n");
        return string.toString();
    }

    /**
     * Method overload for int values. Handles colors.
     * @param value a ColorInt value.
     * @return the XML representation of the attribute.
     */
    protected String getAttributeString(String tag, String name, int value) {
        switch (tag) {
            case "color":
                return getAttributeString(tag, name, String.format("#%06X", (0xFFFFFF & value)));
            default:
                return "";
        }
    }

    /**
     * Method overload for float values. Handles dimens.
     * @param value a float dimension value.
     * @return the XML representation of the attribute.
     */
    protected String getAttributeString(String tag, String name, float value) {
        switch (tag) {
            case "dimen":
                return getAttributeString(tag, name, String.valueOf(value));
            default:
                return "";
        }
    }

    /**
     * Method overload for Typeface values. Handles fonts.
     * @param value a Typeface font value.
     * @return the XML representation of the attribute.
     */
    protected String getAttributeString(String tag, String name, Typeface value) {
        switch (tag) {
            case "string":
                return getAttributeString(tag, name, "sans-serif-light");
            default:
                return "";
        }
    }

    /**
     * Sets the instance RAFOptions. Will also be serialized into JSON and stored in SharedPreferences
     * keyed on the current campaign ID.
     * @param rafOptions the RAFOptions instantiation to store. Don't pass null.
     */
    public static void set(@NonNull RAFOptions rafOptions) {
        instance = rafOptions;
        String campaignId = new Campaign().getId();
        String data = new Gson().toJson(rafOptions);
        AmbSingleton.getContext().getSharedPreferences("rafOptions", Context.MODE_PRIVATE).edit().putString(campaignId, data).apply();
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
