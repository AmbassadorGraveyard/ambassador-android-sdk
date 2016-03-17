package com.ambassador.demoapp;

import android.graphics.Typeface;
import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.RAFOptions;

import java.io.File;

/**
 * Handles custom RAF option packaging in the demo app for sharing. Class can be used to add files
 * at a given path + filename and then zip it.
 */
public class CustomizationPackage {

    /**
     * Default constructor.
     */
    public CustomizationPackage() {}

    /**
     * Adds a plaintext file to the package with a passed in path + name and the String content. Saves
     * it and will later be packaged into the zip.
     * @param pathWithName the String relative path inside assets with the filename on the end.
     * @param content the String content to write in the file.
     * @return this CustomizationPackage, useful for chaining methods.
     */
    @NonNull
    public CustomizationPackage add(@NonNull String pathWithName, @NonNull String content) {

        return this;
    }

    /**
     * Zips all the added files and returns the File.
     * @return the File object pointing to the zip file.
     */
    @NonNull
    public File zip() {
        return new File(".");
    }

    /**
     * Class containing logic to convert a RAFOptions object into XML usable for creating RAFOptions
     * in the 3rd party app.
     */
    protected class OptionXmlTranscriber {

        /** The RAFOptions object to transcribe from. */
        protected RAFOptions rafOptions;

        /**
         * Default constructor declared protected to disallow direct instantiation.
         */
        @SuppressWarnings("unused")
        protected OptionXmlTranscriber() {}

        /**
         * Instantiates the OptionXmlTranscriber with a RAFOptions object dependency.
         * @param rafOptions the RAFOptions object to use for all transcribing.
         */
        public OptionXmlTranscriber(@NonNull RAFOptions rafOptions) {

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

            stringBuilder.append(getAttributeString("string", "RAFdefaultShareMessage", rafOptions.getDefaultShareMessage()));
            stringBuilder.append(getAttributeString("string", "RAFtitleText", rafOptions.getTitleText()));
            stringBuilder.append(getAttributeString("string", "RAFLogoPosition", rafOptions.getLogoPosition()));
            stringBuilder.append(getAttributeString("string", "RAFLogo", rafOptions.getLogo()));
            stringBuilder.append(getAttributeString("color", "homeBackground", rafOptions.getHomeBackgroundColor()));
            stringBuilder.append(getAttributeString("color", "homeWelcomeTitle", rafOptions.getHomeWelcomeTitleColor()));
            stringBuilder.append(getAttributeString("dimen", "homeWelcomeTitle", rafOptions.getHomeWelcomeTitleSize()));
            stringBuilder.append(getAttributeString("string", "homeWelcomeTitle", rafOptions.getHomeWelcomeTitleFont()));
            stringBuilder.append(getAttributeString("color", "homeWelcomeDesc", rafOptions.getHomeWelcomeDescriptionColor()));
            stringBuilder.append(getAttributeString("dimen", "homeWelcomeDesc", rafOptions.getHomeWelcomeDescriptionSize()));
            stringBuilder.append(getAttributeString("string", "homeWelcomeDesc", rafOptions.getHomeWelcomeDescriptionFont()));
            stringBuilder.append(getAttributeString("color", "homeToolBar", rafOptions.getHomeToolbarColor()));
            stringBuilder.append(getAttributeString("color", "homeToolBarText", rafOptions.getHomeToolbarTextColor()));
            stringBuilder.append(getAttributeString("string", "homeToolBarText", rafOptions.getHomeToolbarTextFont()));
            stringBuilder.append(getAttributeString("color", "homeToolBarArrow", rafOptions.getHomeToolbarArrowColor()));
            stringBuilder.append(getAttributeString("color", "homeShareTextBar", rafOptions.getHomeShareTextBar()));
            stringBuilder.append(getAttributeString("color", "homeShareText", rafOptions.getHomeShareTextColor()));
            stringBuilder.append(getAttributeString("dimen", "homeShareText", rafOptions.getHomeShareTextSize()));
            stringBuilder.append(getAttributeString("string", "homeShareText", rafOptions.getHomeShareTextFont()));
            stringBuilder.append(getAttributeString("string", "socialGridText", rafOptions.getSocialGridTextFont()));
            stringBuilder.append(getAttributeString("dimen", "socialOptionCornerRadius", rafOptions.getSocialOptionCornerRadius()));

            stringBuilder.append("<array name=\"channels\">\n");
            for (String channel : rafOptions.getChannels()) {
                stringBuilder.append("<item>");
                stringBuilder.append(channel);
                stringBuilder.append("</item>\n");
            }
            stringBuilder.append("</array>\n");

            stringBuilder.append(getAttributeString("color", "contactsListViewBackground", rafOptions.getContactsListViewBackgroundColor()));
            stringBuilder.append(getAttributeString("dimen", "contactsListName", rafOptions.getContactsListNameSize()));
            stringBuilder.append(getAttributeString("string", "contactsListName", rafOptions.getContactsListNameFont()));
            stringBuilder.append(getAttributeString("dimen", "contactsListValue", rafOptions.getContactsListValueSize()));
            stringBuilder.append(getAttributeString("string", "contactsListValue", rafOptions.getContactsListValueFont()));
            stringBuilder.append(getAttributeString("color", "contactsSendBackground", rafOptions.getContactsSendBackground()));
            stringBuilder.append(getAttributeString("string", "contactSendMessageText", rafOptions.getContactSendMessageTextFont()));
            stringBuilder.append(getAttributeString("color", "contactsToolBar", rafOptions.getContactsToolbarColor()));
            stringBuilder.append(getAttributeString("color", "contactsToolBarText", rafOptions.getContactsToolbarTextColor()));
            stringBuilder.append(getAttributeString("color", "contactsToolBarArrow", rafOptions.getContactsToolbarArrowColor()));
            stringBuilder.append(getAttributeString("color", "contactsSendButton", rafOptions.getContactsSendButtonColor()));
            stringBuilder.append(getAttributeString("color", "contactsSendButtonText", rafOptions.getContactsSendButtonTextColor()));
            stringBuilder.append(getAttributeString("color", "contactsDoneButtonText", rafOptions.getContactsDoneButtonTextColor()));
            stringBuilder.append(getAttributeString("color", "contactsSearchBar", rafOptions.getContactsSearchBarColor()));
            stringBuilder.append(getAttributeString("color", "contactsSearchIcon", rafOptions.getContactsSearchIconColor()));
            stringBuilder.append(getAttributeString("color", "contactNoPhotoAvailableBackground", rafOptions.getContactNoPhotoAvailableBackgroundColor()));
            stringBuilder.append(getAttributeString("color", "linkedinToolBar", rafOptions.getLinkedInToolbarColor()));
            stringBuilder.append(getAttributeString("color", "linkedinToolBarText", rafOptions.getLinkedInToolbarTextColor()));
            stringBuilder.append(getAttributeString("color", "linkedinToolBarArrow", rafOptions.getLinkedInToolbarArrowColor()));
            stringBuilder.append(getAttributeString("color", "twitterToolBar", rafOptions.getTwitterToolbarColor()));
            stringBuilder.append(getAttributeString("color", "twitterToolBarText", rafOptions.getTwitterToolbarTextColor()));
            stringBuilder.append(getAttributeString("color", "twitterToolBarArrow", rafOptions.getTwitterToolbarArrowColor()));

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
         * Gets the XML representation of the RAFOptions and returns it.
         * @return the String XML representation.
         */
        @NonNull
        public String transcribe() {
            return getXmlRepresentation();
        }

    }

}
