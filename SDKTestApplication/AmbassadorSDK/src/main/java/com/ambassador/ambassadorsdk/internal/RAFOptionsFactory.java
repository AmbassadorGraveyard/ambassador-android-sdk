package com.ambassador.ambassadorsdk.internal;

import android.content.Context;
import android.graphics.Color;

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
                            .withParam(value)
                            .invoke(builder);
                    break;
                case TYPE_COLOR:
                    int processedValue = getColor(value);
                    new RAFOptionsMethod(key)
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
            if (value.matches(REGEX_ANDROID_RESOURCE_COLOR)) {
                value = value.substring(value.indexOf("/"));
                int identifier = context.getResources().getIdentifier(value, "color", "android");
                return context.getResources().getColor(identifier);

            } else if (value.matches(REGEX_LOCAL_RESOURCE_COLOR)) {
                value = value.substring(value.indexOf("/"));
                int identifer = context.getResources().getIdentifier(value, "color", context.getPackageName());
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
                execute(builder);
            }

            private void execute(RAFOptions.Builder builder) {
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
                            case "color":
                                builder.setHomeWelcomeTitleColor(paramInt);
                                break;
                            case "dimen":
                                builder.setHomeWelcomeTitleSize(paramFloat);
                                break;
                            case "string":
                                builder.setHomeWelcomeTitleFont(new Font(paramString).getTypeface());
                                break;
                        }
                        break;

                    case "homeWelcomeDesc":
                        switch (type) {
                            case "color":
                                builder.setHomeWelcomeDescriptionColor(paramInt);
                                break;
                            case "dimen":
                                builder.setHomeWelcomeDescriptionSize(paramFloat);
                                break;
                            case "string":
                                builder.setHomeWelcomeDescriptionFont(new Font(paramString).getTypeface());
                                break;
                        }
                        break;

                    case "homeToolBar":
                        builder.setHomeToolbarColor(paramInt);
                        break;

                    case "homeToolBarText":
                        // color, string
                        break;

                    case "homeToolBarArrow":
                        break;

                    case "homeShareTextBar":
                        break;

                    case "homeShareText":
                        // color, dimen, string
                        break;

                    case "socialGridText":
                        break;

                    case "contactsListViewBackground":
                        break;

                    case "contactsListName":
                        // dimen, string
                        break;

                    case "contactsListValue":
                        // dimen, string
                        break;

                    case "contactsSendBackground":
                        break;

                    case "contactSendMessageText":
                        break;

                    case "contactsToolBar":
                        break;

                    case "contactsToolBarText":
                        break;

                    case "contactsToolBarArrow":
                        break;

                    case "contactsSendButton":
                        break;

                    case "contactsSendButtonText":
                        break;

                    case "contactsDoneButtonText":
                        break;

                    case "contactsSearchBar":
                        break;

                    case "contactsSearchIcon":
                        break;

                    case "contactNoPhotoAvailableBackground":
                        break;

                    case "linkedinToolBar":
                        break;

                    case "linkedinToolBarText":
                        break;

                    case "linkedinToolBarArrow":
                        break;
                }
            }

        }

    }

}
