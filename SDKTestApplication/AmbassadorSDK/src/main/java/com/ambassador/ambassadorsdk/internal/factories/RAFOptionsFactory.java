package com.ambassador.ambassadorsdk.internal.factories;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.utils.Font;
import com.google.common.base.Joiner;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public final class RAFOptionsFactory {

    @NonNull
    public static RAFOptions decodeCustomValues(Context context) {
        RAFOptions.Builder builder = new RAFOptions.Builder();

        Resources resources = context.getResources();
        String packageName = context.getPackageName();

        int id;

        id = resources.getIdentifier("RAFdefaultShareMessage", "string", packageName);
        builder.setDefaultShareMessage(resources.getString(id));

        id = resources.getIdentifier("RAFtitleText", "string", packageName);
        builder.setTitleText(resources.getString(id));

        id = resources.getIdentifier("RAFdescriptionText", "string", packageName);
        builder.setDescriptionText(resources.getString(id));

        id = resources.getIdentifier("RAFtoolbarTitle", "string", packageName);
        builder.setToolbarTitle(resources.getString(id));

        id = resources.getIdentifier("RAFLogoPosition", "string", packageName);
        builder.setLogoPosition(resources.getString(id));

        id = resources.getIdentifier("raf_logo", "drawable", packageName);
        builder.setLogoResId(id);

        id = resources.getIdentifier("homeBackground", "color", packageName);
        builder.setHomeBackgroundColor(resources.getColor(id));

        id = resources.getIdentifier("homeWelcomeTitle", "color", packageName);
        builder.setHomeWelcomeTitleColor(resources.getColor(id));

        id = resources.getIdentifier("homeWelcomeTitle", "dimen", packageName);
        builder.setHomeWelcomeTitleSize(removeDensity(context, resources.getDimension(id)));

        id = resources.getIdentifier("homeWelcomeTitle", "string", packageName);
        builder.setHomeWelcomeTitleFont(new Font(resources.getString(id)).getTypeface());

        id = resources.getIdentifier("homeWelcomeDesc", "color", packageName);
        builder.setHomeWelcomeDescriptionColor(resources.getColor(id));

        id = resources.getIdentifier("homeWelcomeDesc", "dimen", packageName);
        builder.setHomeWelcomeDescriptionSize(removeDensity(context, resources.getDimension(id)));

        id = resources.getIdentifier("homeWelcomeDesc", "string", packageName);
        builder.setHomeWelcomeDescriptionFont(new Font(resources.getString(id)).getTypeface());

        id = resources.getIdentifier("homeToolBar", "color", packageName);
        builder.setHomeToolbarColor(resources.getColor(id));

        id = resources.getIdentifier("homeToolBarText", "color", packageName);
        builder.setHomeToolbarTextColor(resources.getColor(id));

        id = resources.getIdentifier("homeToolBarText", "string", packageName);
        builder.setHomeToolbarTextFont(new Font(resources.getString(id)).getTypeface());

        id = resources.getIdentifier("homeToolBarArrow", "color", packageName);
        builder.setHomeToolbarArrowColor(resources.getColor(id));

        id = resources.getIdentifier("homeShareTextBar", "color", packageName);
        builder.setHomeShareTextBar(resources.getColor(id));

        id = resources.getIdentifier("homeShareText", "color", packageName);
        builder.setHomeShareTextColor(resources.getColor(id));

        id = resources.getIdentifier("homeShareText", "dimen", packageName);
        builder.setHomeShareTextSize(removeDensity(context, resources.getDimension(id)));

        id = resources.getIdentifier("homeShareText", "string", packageName);
        builder.setHomeShareTextFont(new Font(resources.getString(id)).getTypeface());

        id = resources.getIdentifier("socialGridText", "string", packageName);
        builder.setSocialGridTextFont(new Font(resources.getString(id)).getTypeface());

        id = resources.getIdentifier("contactsListViewBackground", "color", packageName);
        builder.setContactsListViewBackgroundColor(resources.getColor(id));

        id = resources.getIdentifier("contactsListName", "dimen", packageName);
        builder.setContactsListNameSize(removeDensity(context, resources.getDimension(id)));

        id = resources.getIdentifier("contactsListName", "string", packageName);
        builder.setContactsListNameFont(new Font(resources.getString(id)).getTypeface());

        id = resources.getIdentifier("contactsListValue", "dimen", packageName);
        builder.setContactsListValueSize(removeDensity(context, resources.getDimension(id)));

        id = resources.getIdentifier("contactsListValue", "string", packageName);
        builder.setContactsListValueFont(new Font(resources.getString(id)).getTypeface());

        id = resources.getIdentifier("contactsSendBackground", "color", packageName);
        builder.setContactsSendBackground(resources.getColor(id));

        id = resources.getIdentifier("contactSendMessageText", "string", packageName);
        builder.setContactSendMessageTextFont(new Font(resources.getString(id)).getTypeface());

        id = resources.getIdentifier("contactsToolBar", "color", packageName);
        builder.setContactsToolbarColor(resources.getColor(id));

        id = resources.getIdentifier("contactsToolBarText", "color", packageName);
        builder.setContactsToolbarTextColor(resources.getColor(id));

        id = resources.getIdentifier("contactsToolBarArrow", "color", packageName);
        builder.setContactsToolbarArrowColor(resources.getColor(id));

        id = resources.getIdentifier("contactsSendButton", "color", packageName);
        builder.setContactsSendButtonColor(resources.getColor(id));

        id = resources.getIdentifier("contactsSendButtonText", "color", packageName);
        builder.setContactsSendButtonTextColor(resources.getColor(id));

        id = resources.getIdentifier("contactsDoneButtonText", "color", packageName);
        builder.setContactsDoneButtonTextColor(resources.getColor(id));

        id = resources.getIdentifier("contactsSearchBar", "color", packageName);
        builder.setContactsSearchBarColor(resources.getColor(id));

        id = resources.getIdentifier("contactsSearchIcon", "color", packageName);
        builder.setContactsSearchIconColor(resources.getColor(id));

        id = resources.getIdentifier("contactNoPhotoAvailableBackground", "color", packageName);
        builder.setContactNoPhotoAvailableBackgroundColor(resources.getColor(id));

        id = resources.getIdentifier("linkedinToolBar", "color", packageName);
        builder.setLinkedInToolbarColor(resources.getColor(id));

        id = resources.getIdentifier("linkedinToolBarText", "color", packageName);
        builder.setLinkedInToolbarTextColor(resources.getColor(id));

        id = resources.getIdentifier("linkedinToolBarArrow", "color", packageName);
        builder.setLinkedInToolbarArrowColor(resources.getColor(id));

        id = resources.getIdentifier("socialOptionCornerRadius", "dimen", packageName);
        builder.setSocialOptionCornerRadius(removeDensity(context, resources.getDimension(id)));

        return builder.build();
    }

    private static float removeDensity(Context context, float densityPixels) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return densityPixels / scaledDensity;
    }

    @NonNull
    public static RAFOptions decodeResources(InputStream inputStream, Context context) throws Exception {
        RAFOptions.Builder rafBuilder = RAFOptions.Builder.newInstance();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
        Document document = dbBuilder.parse(inputStream);

        document.getDocumentElement().normalize();
        NodeList values = document.getFirstChild().getChildNodes();

        for (int i = 0; i < values.getLength(); i++) {
            Node entry = values.item(i);
            if (entry.getAttributes() == null)
                continue;

            String type = entry.getNodeName();
            String key = entry.getAttributes().getNamedItem("name").getNodeValue();
            String value;
            if (!"array".equals(type)) {
                value = entry.getFirstChild().getNodeValue();
            } else {
                NodeList nodes = entry.getChildNodes();
                ArrayList<String> names = new ArrayList<>();
                for (int j = 0; j < nodes.getLength(); j++) {
                    Node node = nodes.item(j);
                    if (node.getAttributes() != null) {
                        String name = node.getFirstChild().getNodeValue();
                        names.add(name);
                    }
                }

                value = Joiner.on("&").join(names);
            }

            new ResourceProcessor()
                    .withContext(context)
                    .withType(type)
                    .withKey(key)
                    .withValue(value)
                    .applyTo(rafBuilder);
        }

        return rafBuilder.build();
    }

    protected static class ResourceProcessor {

        private static final String TYPE_STRING = "string";
        private static final String TYPE_COLOR = "color";
        private static final String TYPE_DIMEN = "dimen";
        private static final String TYPE_ARRAY = "array";

        private static final String REGEX_ANDROID_RESOURCE_COLOR = "@android:color/[a-zA-Z0-9]*";
        private static final String REGEX_LOCAL_RESOURCE_COLOR = "@color/[a-zA-z0-9]*";
        private static final String REGEX_HEX_COLOR = "#[a-zA-Z0-9]*";

        private Context context;
        private String type;
        private String key;
        private String value;

        public ResourceProcessor() {

        }

        @NonNull
        public ResourceProcessor withContext(@NonNull Context context) {
            this.context = context;
            return this;
        }

        @NonNull
        public ResourceProcessor withType(@NonNull String type) {
            this.type = type;
            return this;
        }

        @NonNull
        public ResourceProcessor withKey(@NonNull String key) {
            this.key = key;
            return this;
        }

        @NonNull
        public ResourceProcessor withValue(@NonNull String value) {
            this.value = value;
            return this;
        }

        public void applyTo(@NonNull RAFOptions.Builder builder) {
            switch (type) {
                case TYPE_STRING:
                    new RAFOptionsMethod(key)
                            .withType(TYPE_STRING)
                            .withParam(value)
                            .invoke(builder);
                    break;
                case TYPE_COLOR:
                    try {
                        int processedValue = getColor(value);
                        new RAFOptionsMethod(key)
                                .withType(TYPE_COLOR)
                                .withParam(processedValue)
                                .invoke(builder);
                    } catch (Exception e) {
                        Log.e("AmbassadorSDK", "An error occurred processing key:" + key + " value:" + value);
                    }
                    break;
                case TYPE_DIMEN:
                    try {
                        float processedDimen = getDimen(value);
                        new RAFOptionsMethod(key)
                                .withType(TYPE_DIMEN)
                                .withParam(processedDimen)
                                .invoke(builder);
                    } catch (Exception e) {
                        Log.e("AmbassadorSDK", "An error occurred processing key:" + key + " value:" + value);
                    }
                    break;
                case TYPE_ARRAY:
                    try {
                        String[] array = value.split("&");
                        new RAFOptionsMethod(key)
                                .withType(TYPE_ARRAY)
                                .withParam(array)
                                .invoke(builder);
                    } catch (Exception e) {
                        Log.e("AmbassadorSDK", "An error occurred processing key:" + key + " value:" + value);
                    }
                    break;

                default:
                    Log.e("AmbassadorSDK", "Not a valid tag type:" + type);
                    break;
            }
        }

        private int getColor(@NonNull String value) {
            String resName;
            if (value.matches(REGEX_ANDROID_RESOURCE_COLOR)) {
                resName = value.substring(value.indexOf("/") + 1);
                int identifier = context.getResources().getIdentifier(resName, "color", "android");
                return ContextCompat.getColor(context, identifier);

            } else if (value.matches(REGEX_LOCAL_RESOURCE_COLOR)) {
                resName = value.substring(value.indexOf("/") + 1);
                int identifier = context.getResources().getIdentifier(resName, "color", context.getPackageName());
                return ContextCompat.getColor(context, identifier);

            } else if (value.matches(REGEX_HEX_COLOR)) {
                return Color.parseColor(value);
            }

            return 0;
        }

        private float getDimen(@NonNull String value) {
            if (value.startsWith("-")) {
                return -Float.parseFloat(value.replaceAll("\\D+", ""));
            }
            return Float.parseFloat(value.replaceAll("\\D+", ""));
        }

        protected static class RAFOptionsMethod {

            private String type;

            private String paramString;
            private int paramInt;
            private float paramFloat;
            private String[] paramStringArray;

            private String key;

            @SuppressWarnings("unused")
            private RAFOptionsMethod() {}

            public RAFOptionsMethod(@NonNull String key) {
                this.key = key;
            }

            @NonNull
            public RAFOptionsMethod withType(@NonNull String type) {
                this.type = type;
                return this;
            }

            @NonNull
            public RAFOptionsMethod withParam(@NonNull String value) {
                this.paramString = value;
                return this;
            }

            @NonNull
            public RAFOptionsMethod withParam(int value) {
                this.paramInt = value;
                return this;
            }

            @NonNull
            public RAFOptionsMethod withParam(float value) {
                this.paramFloat = value;
                return this;
            }

            @NonNull
            public RAFOptionsMethod withParam(String[] value) {
                this.paramStringArray = value;
                return this;
            }

            public void invoke(@NonNull RAFOptions.Builder builder) {
                try {
                    execute(builder);
                } catch (NullPointerException e) {
                    // Something wasn't right, it will be ignored and default left.
                }
            }

            private void execute(@NonNull RAFOptions.Builder builder) throws NullPointerException {
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

                    case "raflogo":
                        builder.setLogo(paramString);
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

                    case "channels":
                        builder.setChannels(paramStringArray);
                        break;

                    case "socialoptioncornerradius":
                        builder.setSocialOptionCornerRadius(paramFloat);
                        break;

                    default:
                        Log.e("Ambassador", "Key: " + key + " is not a valid attribute");
                        break;
                }
            }

        }

    }

}
