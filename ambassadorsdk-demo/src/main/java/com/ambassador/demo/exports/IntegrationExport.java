package com.ambassador.demo.exports;

import android.content.Context;
import android.graphics.Typeface;

import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.demo.data.Integration;
import com.ambassador.demo.data.User;

public class IntegrationExport extends BaseExport<Integration> {

    @Override
    public String getReadme() {
        PlaintextFile readme = new PlaintextFile();
        readme.addLine("Ambassador Android SDK v1.1.4");
        readme.addLine("Take a look at the Android docs for an in-depth explanation on adding and integrating the SDK:");
        readme.addLine("https://docs.getambassador.com/v2.0.0/page/android-sdk");
        readme.addLine("Check out the MyApplication.java file for an example of this integration.");

        readme.addLine("");

        readme.addLine("Ambassador iOS SDK v1.0.3");
        readme.addLine("Take a look at the iOS docs for an in-depth explanation on adding and integrating the SDK:");
        readme.addLine("https://docs.getambassador.com/v2.0.0/page/ios-sdk");
        readme.addLine("Check out the AppDelegate.m or AppDelegate.swift files for examples of this integration.");

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
                    return getAttributeString(tag, name, String.valueOf((int) (value)) + "sp");
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

        @Override
        public String transcribe(Integration integration) {
            return "plist";
        }

    }

}
