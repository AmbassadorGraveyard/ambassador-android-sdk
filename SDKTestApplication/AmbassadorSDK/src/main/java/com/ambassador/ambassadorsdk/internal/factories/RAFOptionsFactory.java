package com.ambassador.ambassadorsdk.internal.factories;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.utils.Font;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public final class RAFOptionsFactory {

    public static RAFOptions decodeResources(InputStream inputStream, Context context) throws Exception {
        RAFOptions.Builder rafBuilder = new RAFOptions.Builder();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
        Document document = dbBuilder.parse(inputStream);

        document.getDocumentElement().normalize();
        NodeList values = document.getFirstChild().getChildNodes();

        for (int i = 0; i < values.getLength(); i++) {
            Node entry = values.item(i);
            if (entry.getAttributes() != null) {
                String type = entry.getNodeName();
                String key = entry.getAttributes().getNamedItem("name").getNodeValue();
                String value = entry.getFirstChild().getNodeValue();

                new ResourceProcessor()
                        .withContext(context)
                        .withType(type)
                        .withKey(key)
                        .withValue(value)
                        .applyTo(rafBuilder);
            }
        }

        return rafBuilder.build();
    }

    private static final class ResourceProcessor {

        private static final String TYPE_STRING = "string";
        private static final String TYPE_COLOR = "color";
        private static final String TYPE_DIMEN = "dimen";
        private static final String TYPE_ARRAY = "array";

        private static final String REGEX_ANDROID_RESOURCE_COLOR = "@android:color/[a-zA-Z]*";
        private static final String REGEX_LOCAL_RESOURCE_COLOR = "@color/[a-zA-z]*";
        private static final String REGEX_HEX_COLOR = "#[a-zA-Z0-9]*";

        private Context context;
        private String type;
        private String key;
        private String value;

        public ResourceProcessor() {

        }

        public ResourceProcessor withContext(Context context) {
            this.context = context;
            return this;
        }

        public ResourceProcessor withType(String type) {
            this.type = type;
            return this;
        }

        public ResourceProcessor withKey(String key) {
            this.key = key;
            return this;
        }

        public ResourceProcessor withValue(String value) {
            this.value = value;
            return this;
        }

        public void applyTo(RAFOptions.Builder builder) {
            switch (type) {
                case TYPE_STRING:
                    new RAFOptionsMethod(key)
                            .withType("string")
                            .withParam(value)
                            .invoke(builder);
                    break;
                case TYPE_COLOR:
                    int processedValue = getColor(value);
                    new RAFOptionsMethod(key)
                            .withType("color")
                            .withParam(processedValue)
                            .invoke(builder);
                    break;
                case TYPE_DIMEN:
                    break;
                case TYPE_ARRAY:
                    break;
            }
        }

        private int getColor(String value) {
            String resName;
            if (value.matches(REGEX_ANDROID_RESOURCE_COLOR)) {
                resName = value.substring(value.indexOf("/") + 1);
                int identifier = context.getResources().getIdentifier(resName, "color", "android");
                return context.getResources().getColor(identifier);

            } else if (value.matches(REGEX_LOCAL_RESOURCE_COLOR)) {
                resName = value.substring(value.indexOf("/") + 1);
                int identifer = context.getResources().getIdentifier(resName, "color", context.getPackageName());
                return context.getResources().getColor(identifer);

            } else if (value.matches(REGEX_HEX_COLOR)) {
                return Color.parseColor(value);
            }

            return 0;
        }

        private static final class RAFOptionsMethod {

            private String type;

            private String paramString;
            private int paramInt;
            private float paramFloat;

            private String key;

            private RAFOptionsMethod() {}

            public RAFOptionsMethod(String key) {
                this.key = key;
            }

            public RAFOptionsMethod withType(String type) {
                this.type = type;
                return this;
            }

            public RAFOptionsMethod withParam(String value) {
                this.paramString = value;
                return this;
            }

            public RAFOptionsMethod withParam(int value) {
                this.paramInt = value;
                return this;
            }

            public RAFOptionsMethod withParam(float value) {
                this.paramFloat = value;
                return this;
            }

            public void invoke(RAFOptions.Builder builder) {
                try {
                    execute(builder);
                } catch (NullPointerException e) {
                    // Something wasn't right, it will be ignored and default left.
                }
            }

            private void execute(RAFOptions.Builder builder) throws NullPointerException {
                switch (key.toLowerCase()) {
                    case "rafdefaultsharemessage":
                        builder.setDefaultShareMessage(paramString);
                        break;

                    case "raftitletext":
                        builder.setTitleText(paramString);
                        break;

                    case "rafdescriptiontext":
                        builder.setDescriptionText(paramString);
                        break;

                    case "raftoolbartitle":
                        builder.setToolbarTitle(paramString);
                        break;

                    case "raflogoposition":
                        builder.setLogoPosition(paramString);
                        break;

                    case "homebackground":
                        builder.setHomeBackgroundColor(paramInt);
                        break;

                    case "homewelcometitle":
                        switch (type) {
                            case TYPE_COLOR:
                                builder.setHomeWelcomeTitleColor(paramInt);
                                break;
                            case TYPE_DIMEN:
                                builder.setHomeWelcomeTitleSize(paramFloat);
                                break;
                            case TYPE_STRING:
                                builder.setHomeWelcomeTitleFont(new Font(paramString).getTypeface());
                                break;
                            default: break;
                        }
                        break;

                    case "homewelcomedesc":
                        switch (type) {
                            case TYPE_COLOR:
                                builder.setHomeWelcomeDescriptionColor(paramInt);
                                break;
                            case TYPE_DIMEN:
                                builder.setHomeWelcomeDescriptionSize(paramFloat);
                                break;
                            case TYPE_STRING:
                                builder.setHomeWelcomeDescriptionFont(new Font(paramString).getTypeface());
                                break;
                            default: break;
                        }
                        break;

                    case "hometoolbar":
                        builder.setHomeToolbarColor(paramInt);
                        break;

                    case "hometoolbartext":
                        switch (type) {
                            case TYPE_COLOR:
                                builder.setHomeToolbarTextColor(paramInt);
                                break;
                            case TYPE_STRING:
                                builder.setHomeToolbarTextFont(new Font(paramString).getTypeface());
                                break;
                            default: break;
                        }
                        break;

                    case "hometoolbararrow":
                        builder.setHomeToolbarArrowColor(paramInt);
                        break;

                    case "homesharetextbar":
                        builder.setHomeShareTextBar(paramInt);
                        break;

                    case "homesharetext":
                        switch (type) {
                            case TYPE_COLOR:
                                builder.setHomeShareTextColor(paramInt);
                                break;
                            case TYPE_DIMEN:
                                builder.setHomeShareTextSize(paramFloat);
                                break;
                            case TYPE_STRING:
                                builder.setHomeShareTextFont(new Font(paramString).getTypeface());
                                break;
                            default: break;
                        }
                        break;

                    case "socialgridtext":
                        builder.setSocialGridTextFont(new Font(paramString).getTypeface());
                        break;

                    case "contactslistviewbackground":
                        builder.setContactsListViewBackgroundColor(paramInt);
                        break;

                    case "contactslistname":
                        switch (type) {
                            case TYPE_DIMEN:
                                builder.setContactsListNameSize(paramFloat);
                                break;
                            case TYPE_STRING:
                                builder.setContactsListNameFont(new Font(paramString).getTypeface());
                                break;
                            default: break;
                        }
                        break;

                    case "contactslistvalue":
                        switch (type) {
                            case TYPE_DIMEN:
                                builder.setContactsListValueSize(paramFloat);
                                break;
                            case TYPE_STRING:
                                builder.setContactsListValueFont(new Font(paramString).getTypeface());
                                break;
                            default: break;
                        }
                        break;

                    case "contactssendbackground":
                        builder.setContactsSendBackground(paramInt);
                        break;

                    case "contactsendmessagetext":
                        builder.setContactSendMessageTextFont(new Font(paramString).getTypeface());
                        break;

                    case "contactstoolbar":
                        builder.setContactsToolbarColor(paramInt);
                        break;

                    case "contactstoolbartext":
                        builder.setContactsToolbarTextColor(paramInt);
                        break;

                    case "contactstoolbararrow":
                        builder.setContactsToolbarArrowColor(paramInt);
                        break;

                    case "contactssendbutton":
                        builder.setContactsSendButtonColor(paramInt);
                        break;

                    case "contactssendbuttontext":
                        builder.setContactsSendButtonTextColor(paramInt);
                        break;

                    case "contactsdonebuttontext":
                        builder.setContactsDoneButtonTextColor(paramInt);
                        break;

                    case "contactssearchbar":
                        builder.setContactsSearchBarColor(paramInt);
                        break;

                    case "contactssearchicon":
                        builder.setContactsSearchIconColor(paramInt);
                        break;

                    case "contactnophotoavailablebackground":
                        builder.setContactNoPhotoAvailableBackgroundColor(paramInt);
                        break;

                    case "linkedintoolbar":
                        builder.setLinkedInToolbarColor(paramInt);
                        break;

                    case "linkedintoolbartext":
                        builder.setLinkedInToolbarTextColor(paramInt);
                        break;

                    case "linkedintoolbararrow":
                        builder.setLinkedInToolbarArrowColor(paramInt);
                        break;

                    default:
                        Log.e("Ambassador", "Key: " + key + " is not a valid attribute");
                        break;
                }
            }

        }

    }

}
