package com.ambassador.demoapp;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.RAFOptions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Handles custom packaging in the demo app for sharing integrations. Class can be used to add files
 * at a given path + filename and then zip it. Has special logic for stuff like RAFOptions and ConversionParameters.
 */
public class CustomizationPackage {

    /** A context reference passed into the class constructor used for saving and reading files properly. */
    protected Context context;

    /** List of String path+file that need to be packaged into the zip. */
    protected List<String> files;

    /**
     * Default constructor.
     * @param context a reference to a valid Context.
     */
    public CustomizationPackage(@NonNull Context context) {
        this.context = context;
        this.files = new ArrayList<>();
    }

     /**
     * Adds a plaintext file to the package with a passed in path + name and the String content. Saves
     * it and will later be packaged into the zip.
     * @param pathWithName the String relative path inside assets with the filename on the end.
     * @param content the String content to write in the file.
     * @return this CustomizationPackage, useful for chaining methods.
     */
    @NonNull
    public CustomizationPackage add(@NonNull String pathWithName, @NonNull String content) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(pathWithName, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        files.add(pathWithName);
        return this;
    }

    /**
     * Converts RAFOptions to a String XML representation and calls through to add(String pathWithName, String content).
     * @param pathWithName the String relative path inside assets with the filename on the end.
     * @param rafOptions the RAFOptions object to convert to XML and write to the file.
     * @return this CustomizationPackage, useful for chaining methods.
     */
    @NonNull
    public CustomizationPackage add(@NonNull String pathWithName, @NonNull RAFOptions rafOptions) {
        String logo = rafOptions.getLogo();
        if (logo != null) {
            if (copyFromAssetsToInternal(logo)) {
                files.add(logo);
            }
        }

        return add(pathWithName, new OptionXmlTranscriber(rafOptions).transcribe());
    }

    /**
     * Zips all the added files and returns the path.
     * @return the String path + filename of the zip file.
     */
    @NonNull
    public String zip() {
        try {
            BufferedInputStream origin;
            FileOutputStream dest = context.openFileOutput("android-raf.zip", Context.MODE_PRIVATE);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[1024];

            for (int i = 0; i < files.size(); i++) {
                FileInputStream fi = context.openFileInput(files.get(i));
                origin = new BufferedInputStream(fi, 1024);

                ZipEntry entry = new ZipEntry(files.get(i).substring(files.get(i).lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, 1024)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "android-raf.zip";
    }

    /**
     * Takes a file stored in assets and puts it in internal storage. This makes it usable for zipping.
     * @param assetsPath the String path in assets of the file to copy.
     */
    protected boolean copyFromAssetsToInternal(String assetsPath) {
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(assetsPath);
            FileOutputStream outputStream = context.openFileOutput(assetsPath, Context.MODE_PRIVATE);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 1024);
            byte[] data = new byte[1024];
            int count;
            while ((count = bufferedInputStream.read(data, 0, 1024)) != -1) {
                outputStream.write(data, 0, count);
            }
            bufferedInputStream.close();
            outputStream.close();
        } catch (IOException e) {
            return false;
        }

        return true;
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
            this.rafOptions = rafOptions;
        }

        /**
         * Generates a String XML representation of the RAFOptions. Same as defaultOptions.xml and what
         * is in the README.
         * @return the String XML representation.
         */
        @NonNull
        private String getXmlRepresentation() {
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

            stringBuilder.append("    <array name=\"channels\">\n");
            for (String channel : rafOptions.getChannels()) {
                stringBuilder.append("        <item>");
                stringBuilder.append(channel);
                stringBuilder.append("</item>\n");
            }
            stringBuilder.append("    </array>\n");

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
        private String getAttributeString(String tag, String name, String value) {
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
        private String getAttributeString(String tag, String name, int value) {
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
        private String getAttributeString(String tag, String name, float value) {
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
        private String getAttributeString(String tag, String name, Typeface value) {
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
