package com.ambassador.demo.exports;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;

import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.demo.data.Integration;
import com.ambassador.demo.data.User;

public class IntegrationExport extends BaseExport<Integration> {

    @Override
    public String getReadme() {
        String image = model.getRafOptions().getLogo();

        PlaintextFile readme = new PlaintextFile();
        readme.addHtmlLine("Hey! I've attached examples showing how to set up a refer-a-friend view in our mobile app with the Ambassador SDK.");
        readme.addHtmlLine("");
        readme.addHtmlLine("The attachment includes examples for");
        readme.addHtmlLine("iOS (v1.0.3): ViewControllerTest.m or ViewControllerTest.swift and ambassador-raf.plist,");
        readme.addHtmlLine("Android (v1.1.4): MyActivity.java and ambassador-raf.xml" + (image != null ? "," : "."));
        if (image != null) {
            readme.addHtmlLine("and " + image + ", a custom image used in the theme configuration.");
        }
        readme.addHtmlLine("");
        readme.addHtmlLine("For in-depth explanations on adding and integrating the SDKs check out these links:");
        readme.addHtmlLine("iOS -> https://docs.getambassador.com/v2.0.0/page/ios-sdk");
        readme.addHtmlLine("Android -> https://docs.getambassador.com/v2.0.0/page/android-sdk");
        readme.addHtmlLine("");
        readme.addHtmlLine("Let me know if you have any questions!");

        // This seems to prevent "Let me know..." from going into signature, sometimes.
        readme.addHtmlLine("");
        readme.addHtmlLine("");
        readme.addHtmlLine("");
        readme.addHtmlLine("");
        readme.addHtmlLine("");

        return readme.get();
    }

    @Override
    public String getJavaImplementation() {
        PlaintextFile java = new PlaintextFile();
        java.addLine("package com.example.example;");
        java.addLine("");
        java.addLine("import android.app.Activity;");
        java.addLine("import android.os.Bundle;");
        java.addLine("import com.ambassador.ambassadorsdk.AmbassadorSDK;");
        java.addLine("");
        java.addLine("public class MyActivity extends Activity {");
        java.addLine("");
        java.addLineWithPadding(4, "@Override");
        java.addLineWithPadding(4, "public void onCreate(Bundle savedInstanceState) {");
        java.addLineWithPadding(8, "super.onCreate(savedInstanceState);");
        java.addLineWithPadding(8, String.format("AmbassadorSDK.runWithKeys(this, \"SDKToken %s\", \"%s\");", User.get().getSdkToken(), User.get().getUniversalId()));
        java.addLineWithPadding(8, String.format("AmbassadorSDK.presentRAF(this, \"%s\", \"ambassador-raf.xml\");", model.getCampaignId()));
        java.addLineWithPadding(4, "}");
        java.addLine("");
        java.addLine("}");

        return java.get();
    }

    @Override
    public String getSwiftImplementation() {
        PlaintextFile swift = new PlaintextFile();
        swift.addLine("import UIKit");
        swift.addLine("");
        swift.addLine("class ViewController: UIViewController {");
        swift.addLine("");
        swift.addLineWithPadding(4, "override func viewDidAppear(animated: Bool) {");
        swift.addLineWithPadding(8, String.format("AmbassadorSDK.runWithUniversalToken(\"%s\", universalID: \"%s\")", User.get().getSdkToken(), User.get().getUniversalId()));
        swift.addLineWithPadding(8, String.format("AmbassadorSDK.presentRAFForCampaign(\"%s\", fromViewController: self, withThemePlist: \"ambassador-raf\")", model.getCampaignId()));
        swift.addLineWithPadding(4, "}");
        swift.addLine("");
        swift.addLine("}");

        return swift.get();
    }

    @Override
    public String getObjectiveCImplementation() {
        PlaintextFile objc = new PlaintextFile();
        objc.addLine("#import \"ViewControllerTest.h\"");
        objc.addLine("#import <Ambassador/Ambassador.h>");
        objc.addLine("");
        objc.addLine("@interface ViewControllerTest ()");
        objc.addLine("");
        objc.addLine("@end");
        objc.addLine("");
        objc.addLine("@implementation ViewControllerTest");
        objc.addLine("");
        objc.addLine("- (void)viewDidAppear:(BOOL)animated {");
        objc.addLineWithPadding(4, String.format("[AmbassadorSDK runWithUniversalToken:\"%s\" universalID:\"%s\"];", User.get().getSdkToken(), User.get().getUniversalId()));
        objc.addLineWithPadding(4, String.format("[AmbassadorSDK presentRAFForCampaign:@\"%s\" FromViewController:self withThemePlist:@\"ambassador-raf\"];", model.getCampaignId()));
        objc.addLine("}");
        objc.addLine("");
        objc.addLine("@end");

        return objc.get();
    }

    @Override
    public String zip(Context context) {
        addExtraContent("ambassador-raf.xml", new XmlIntegrationTranscriber().transcribe(model));
        addExtraContent("ambassador-raf.plist", new plistIntegrationTranscriber().transcribe(model));
        if (model.getRafOptions().getLogo() != null) {
            addExtraFile(model.getRafOptions().getLogo());
        }
        return super.zip(context);
    }

    @Override
    public String getZipName() {
        return "ambassador-integration.zip";
    }

    protected interface IntegrationTranscriber {
        String transcribe(Integration integration);
    }

    protected static class XmlIntegrationTranscriber implements IntegrationTranscriber {

        private String getAttributeString(String tag, String name, String value) {
            String string = "";
            string += "    <";
            string += tag;
            string += " name=\"";
            string += name;
            string += "\"";
            string += ">";
            string += value;
            string += "</";
            string += tag;
            string += ">\n";
            return string;
        }

        private String getAttributeString(String tag, String name, int value) {
            switch (tag) {
                case "color":
                    return getAttributeString(tag, name, String.format("#%06X", (0xFFFFFF & value)));
                default:
                    return "";
            }
        }

        private String getAttributeString(String tag, String name, float value) {
            switch (tag) {
                case "dimen":
                    return getAttributeString(tag, name, String.valueOf((int) (value)) + ("socialOptionCornerRadius".equals(name) ? "dp" :"sp"));
                default:
                    return "";
            }
        }

        private String getAttributeString(String tag, String name, Typeface value) {
            switch (tag) {
                case "string":
                    return getAttributeString(tag, name, "sans-serif");
                default:
                    return "";
            }
        }

        @Override
        public String transcribe(Integration integration) {
            RAFOptions rafOptions = integration.getRafOptions();

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            stringBuilder.append("<resources>\n");

            stringBuilder.append(getAttributeString("string", "RAFdefaultShareMessage", rafOptions.getDefaultShareMessage()));
            stringBuilder.append(getAttributeString("string", "RAFtitleText", rafOptions.getTitleText()));
            stringBuilder.append(getAttributeString("string", "RAFdescriptionText", rafOptions.getDescriptionText()));
            stringBuilder.append(getAttributeString("string", "RAFtoolbarTitle", rafOptions.getToolbarTitle()));
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

    }

    protected static class plistIntegrationTranscriber implements IntegrationTranscriber {

        private String getLogoValue(String logoPath) {
            if (logoPath == null) return "";
            return logoPath.substring(0, logoPath.lastIndexOf(".")) + ", 1";
        }

        private String getChannelValue(String[] channels) {
            String out = "";
            for (String channel : channels) {
                out += channel;
                out += ",";
            }

            if (out.endsWith(",")) {
                return out.substring(0, out.length() - 1);
            }

            return out;
        }

        private String getColorValue(@ColorInt int color) {
            return String.format("#%06X", (0xFFFFFF & color));
        }

        private String getKeyString(String key) {
            return "    <key>" + key + "</key>\n";
        }

        private String getValueString(String value) {
            return "    <string>" + value + "</string>\n";
        }

        @Override
        public String transcribe(Integration integration) {
            RAFOptions rafOptions = integration.getRafOptions();

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            stringBuilder.append("<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n");
            stringBuilder.append("<plist version=\"1.0\">\n");
            stringBuilder.append("<dict>\n");

            stringBuilder.append(getKeyString("NAVIGATION BAR -- - -- - - - - "));
            stringBuilder.append(getValueString(""));

            stringBuilder.append(getKeyString("NavBarColor"));
            stringBuilder.append(getValueString(getColorValue(rafOptions.getContactsToolbarColor())));

            stringBuilder.append(getKeyString("NavBarTextMessage"));
            stringBuilder.append(getValueString(rafOptions.getToolbarTitle()));

            stringBuilder.append(getKeyString("NavBarTextColor"));
            stringBuilder.append(getValueString(getColorValue(rafOptions.getContactsToolbarTextColor())));

            stringBuilder.append(getKeyString("NavBarTextFont"));
            stringBuilder.append(getValueString("Helvetica, 20"));

            stringBuilder.append(getKeyString("RAF PAGE ------------------------"));
            stringBuilder.append(getValueString(""));

            stringBuilder.append(getKeyString("RAFBackgroundColor"));
            stringBuilder.append(getValueString("#FFFFFF"));

            stringBuilder.append(getKeyString("RAFLogo"));
            stringBuilder.append(getValueString(getLogoValue(rafOptions.getLogo())));

            stringBuilder.append(getKeyString("SHARE URL FIELD ----------------"));
            stringBuilder.append(getValueString(""));

            stringBuilder.append(getKeyString("ShareFieldBackgroundColor"));
            stringBuilder.append(getValueString("#F4F4F4"));

            stringBuilder.append(getKeyString("ShareFieldHeight"));
            stringBuilder.append(getValueString("35"));

            stringBuilder.append(getKeyString("ShareFieldTextColor"));
            stringBuilder.append(getValueString("#000000"));

            stringBuilder.append(getKeyString("ShareFieldTextFont"));
            stringBuilder.append(getValueString("HelveticaNeue, 15"));

            stringBuilder.append(getKeyString("ShareFieldCornerRadius"));
            stringBuilder.append(getValueString("0"));

            stringBuilder.append(getKeyString("RAF WELCOME LABEL ----------"));
            stringBuilder.append(getValueString(""));

            stringBuilder.append(getKeyString("RAFWelcomeTextMessage"));
            stringBuilder.append(getValueString(rafOptions.getTitleText()));

            stringBuilder.append(getKeyString("RAFWelcomeTextColor"));
            stringBuilder.append(getValueString(getColorValue(rafOptions.getHomeWelcomeTitleColor())));

            stringBuilder.append(getKeyString("RAFWelcomeTextFont"));
            stringBuilder.append(getValueString("HelveticaNeue, 22"));

            stringBuilder.append(getKeyString("RAF DESCRIPTION LABEL ------"));
            stringBuilder.append(getValueString(""));

            stringBuilder.append(getKeyString("RAFDescriptionTextMessage"));
            stringBuilder.append(getValueString(rafOptions.getDescriptionText()));

            stringBuilder.append(getKeyString("RAFDescriptionTextColor"));
            stringBuilder.append(getValueString(getColorValue(rafOptions.getHomeWelcomeDescriptionColor())));

            stringBuilder.append(getKeyString("RAFDescriptionTextFont"));
            stringBuilder.append(getValueString("HelveticaNeue, 18"));

            stringBuilder.append(getKeyString("DefaultShareMessage"));
            stringBuilder.append(getValueString("I'm a fan of this company, check them out!"));

            stringBuilder.append(getKeyString("CONTACT SEND BUTTON ---------"));
            stringBuilder.append(getValueString(""));

            stringBuilder.append(getKeyString("ContactSendButtonBackgroundColor"));
            stringBuilder.append(getValueString(getColorValue(rafOptions.getContactsSendButtonColor())));

            stringBuilder.append(getKeyString("ContactSendButtonTextColor"));
            stringBuilder.append(getValueString("#FFFFFF"));

            stringBuilder.append(getKeyString("ContactSendButtonTextFont"));
            stringBuilder.append(getValueString("HelveticaNeue, 16"));

            stringBuilder.append(getKeyString("CONTACT SEARCH BAR ----------"));
            stringBuilder.append(getValueString(""));

            stringBuilder.append(getKeyString("ContactSearchBackgroundColor"));
            stringBuilder.append(getValueString("#DEDEDE"));

            stringBuilder.append(getKeyString("ContactSearchDoneButtonTextColor"));
            stringBuilder.append(getValueString("#979797"));

            stringBuilder.append(getKeyString("CONTACT TABLEVIEW ------------"));
            stringBuilder.append(getValueString(""));

            stringBuilder.append(getKeyString("ContactTableBackgroundColor"));
            stringBuilder.append(getValueString("#FFFFFF"));

            stringBuilder.append(getKeyString("ContactTableCheckMarkColor"));
            stringBuilder.append(getValueString(getColorValue(rafOptions.getContactsSendButtonColor())));

            stringBuilder.append(getKeyString("ContactTableNameTextColor"));
            stringBuilder.append(getValueString("#000000"));

            stringBuilder.append(getKeyString("ContactTableNameTextFont"));
            stringBuilder.append(getValueString("Helvetica, 17"));

            stringBuilder.append(getKeyString("ContactTableInfoTextColor"));
            stringBuilder.append(getValueString("#C9C9C9"));

            stringBuilder.append(getKeyString("ContactTableInfoTextFont"));
            stringBuilder.append(getValueString("Helvetica, 13"));

            stringBuilder.append(getKeyString("ContactAvatarBackgroundColor"));
            stringBuilder.append(getValueString(getColorValue(rafOptions.getContactsSendButtonColor())));

            stringBuilder.append(getKeyString("ContactAvatarColor"));
            stringBuilder.append(getValueString("#FFFFFF"));

            stringBuilder.append(getKeyString("ALERT BUTTON ---------"));
            stringBuilder.append(getValueString(""));

            stringBuilder.append(getKeyString("AlertButtonBackgroundColor"));
            stringBuilder.append(getValueString(getColorValue(rafOptions.getContactsSendButtonColor())));

            stringBuilder.append(getKeyString("AlertButtonTextColor"));
            stringBuilder.append(getValueString("#FFFFFF"));

            stringBuilder.append(getKeyString("SOCIAL SHARE GRID ------------"));
            stringBuilder.append(getValueString(""));

            stringBuilder.append(getKeyString("Channels"));
            stringBuilder.append(getValueString(getChannelValue(rafOptions.getChannels())));

            stringBuilder.append("</dict>\n");
            stringBuilder.append("</plist>\n");
            return stringBuilder.toString();
        }

    }

    @Override
    public String javaClassName() {
        return "MyActivity";
    }

    @Override
    public String iOSClassName() {
        return "ViewControllerTest";
    }

}
