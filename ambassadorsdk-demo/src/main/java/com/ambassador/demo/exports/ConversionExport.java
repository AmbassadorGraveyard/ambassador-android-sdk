package com.ambassador.demo.exports;

import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.demo.data.User;

public class ConversionExport extends BaseExport<ConversionParameters> {

    @Override
    public String getReadme() {
        PlaintextFile readme = new PlaintextFile();
        readme.addHtmlLine("Hey! I've attached examples showing how to set up a conversion in our mobile app with the Ambassador SDK.");
        readme.addHtmlLine("");
        readme.addHtmlLine("The attachment includes examples for");
        readme.addHtmlLine("iOS (v1.0.3): AppDelegate.m or AppDelegate.swift,");
        readme.addHtmlLine("Android (v1.1.4): MyApplication.java.");
        readme.addHtmlLine("");
        readme.addHtmlLine("For in-depth explanations on adding and integrating the SDKs check out these links:");
        readme.addHtmlLine("iOS -> https://docs.getambassador.com/v2.0.0/page/ios-sdk");
        readme.addHtmlLine("Android -> https://docs.getambassador.com/v2.0.0/page/android-sdk");
        readme.addHtmlLine("");
        readme.addHtmlLine("Let me know if you have any questions!");
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
        java.addLine("import android.app.Application;");
        java.addLine("import com.ambassador.ambassadorsdk.ConversionParameters;");
        java.addLine("import com.ambassador.ambassadorsdk.AmbassadorSDK;");
        java.addLine("");
        java.addLine("public class MyApplication extends Application {");
        java.addLine("");
        java.addLineWithPadding(4, "@Override");
        java.addLineWithPadding(4, "public void onCreate() {");
        java.addLineWithPadding(8, "super.onCreate();");
        java.addLineWithPadding(8, String.format("AmbassadorSDK.runWithKeys(this, \"SDKToken %s\", \"%s\");", User.get().getSdkToken(), User.get().getUniversalId()));
        java.addLineWithPadding(8, "ConversionParameters conversionParameters = new ConversionParameters.Builder()");

        java.addLineWithPadding(12, ".setEmail(\"" + model.email + "\")");
        java.addLineWithPadding(12, ".setRevenue(" + model.revenue + ")");
        java.addLineWithPadding(12, ".setCampaign(" + model.campaign + ")");

        java.addLineWithPadding(12, ".setAddToGroupId(\"" + model.addToGroupId + "\")");
        java.addLineWithPadding(12, ".setFirstName(\"" + model.firstName + "\")");
        java.addLineWithPadding(12, ".setLastName(\"" + model.lastName + "\")");
        java.addLineWithPadding(12, ".setUID(\"" + model.uid + "\")");
        java.addLineWithPadding(12, ".setCustom1(\"" + model.custom1 + "\")");
        java.addLineWithPadding(12, ".setCustom2(\"" + model.custom2 + "\")");
        java.addLineWithPadding(12, ".setCustom3(\"" + model.custom3 + "\")");
        java.addLineWithPadding(12, ".setTransactionUID(\"" + model.transactionUid + "\")");
        java.addLineWithPadding(12, ".setEventData1(\"" + model.eventData1 + "\")");
        java.addLineWithPadding(12, ".setEventData2(\"" + model.eventData2 + "\")");
        java.addLineWithPadding(12, ".setEventData3(\"" + model.eventData3 + "\")");

        java.addLineWithPadding(12, ".setIsApproved(" + model.isApproved + ")");
        java.addLineWithPadding(12, ".setAutoCreate(" + model.autoCreate + ")");
        java.addLineWithPadding(12, ".setDeactivateNewAmbassador(" + model.deactivateNewAmbassador + ")");
        java.addLineWithPadding(12, ".setEmailNewAmbassador(" + model.emailNewAmbassador + ")");

        java.addLineWithPadding(12, ".build();\n");
        java.addLineWithPadding(8, "AmbassadorSDK.registerConversion(conversionParameters, false);");
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
        swift.addLine("@UIApplicationMainclass AppDelegate: UIResponder, UIApplicationDelegate {");
        swift.addLine("");
        swift.addLineWithPadding(4, "var window: UIWindow?");
        swift.addLine("");
        swift.addLineWithPadding(4, "func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {");
        swift.addLineWithPadding(8, String.format("AmbassadorSDK.runWithUniversalToken(\"%s\", universalID: \"%s\")", User.get().getSdkToken(), User.get().getUniversalId()));

        swift.addLine("");

        swift.addLineWithPadding(8, "let conversionParameters = AMBConversionParameters()");
        swift.addLine("");
        swift.addLineWithPadding(8, String.format("conversionParameters.mbsy_campaign = %s", model.getCampaign()));
        swift.addLineWithPadding(8, String.format("conversionParameters.mbsy_email = \"%s\"", model.getEmail()));
        swift.addLineWithPadding(8, String.format("conversionParameters.mbsy_revenue = %s", model.getRevenue()));

        swift.addLineWithPadding(8, String.format("conversionParameters.mbsy_first_name = \"%s\"", model.getFirstName()));
        swift.addLineWithPadding(8, String.format("conversionParameters.mbsy_last_name = \"%s\"", model.getLastName()));
        swift.addLineWithPadding(8, String.format("conversionParameters.mbsy_email_new_ambassador = %s", model.getCampaign() == 1));
        swift.addLineWithPadding(8, String.format("conversionParameters.mbsy_uid = \"%s\"", model.getUid()));
        swift.addLineWithPadding(8, String.format("conversionParameters.mbsy_custom1 = \"%s\"", model.getCustom1()));
        swift.addLineWithPadding(8, String.format("conversionParameters.mbsy_custom2 = \"%s\"", model.getCustom2()));
        swift.addLineWithPadding(8, String.format("conversionParameters.mbsy_custom3 = \"%s\"", model.getCustom3()));
        swift.addLineWithPadding(8, String.format("conversionParameters.mbsy_auto_create = %s", model.getAutoCreate() == 1));
        swift.addLineWithPadding(8, String.format("conversionParameters.mbsy_deactivate_new_ambassador = %s", model.getDeactivateNewAmbassador() == 1));
        swift.addLineWithPadding(8, String.format("conversionParameters.mbsy_transaction_uid = \"%s\"", model.getTransactionUid()));
        swift.addLineWithPadding(8, String.format("conversionParameters.mbsy_add_to_group_id = \"%s\"", model.getAddToGroupId()));
        swift.addLineWithPadding(8, String.format("conversionParameters.mbsy_event_data1 = \"%s\"", model.getEventData1()));
        swift.addLineWithPadding(8, String.format("conversionParameters.mbsy_event_data2 = \"%s\"", model.getEventData2()));
        swift.addLineWithPadding(8, String.format("conversionParameters.mbsy_event_data3 = \"%s\"", model.getEventData3()));
        swift.addLineWithPadding(8, String.format("conversionParameters.mbsy_is_approved = %s", model.getIsApproved() == 1));

        swift.addLine("");

        swift.addLineWithPadding(8, "AmbassadorSDK.registerConversion(conversionParameters, restrictToInstall: false) { (error) -> Void in");
        swift.addLineWithPadding(12, "if ((error) != nil) {");
        swift.addLineWithPadding(16, "print(\"Error \\(error)\")");
        swift.addLineWithPadding(12, "} else {");
        swift.addLineWithPadding(16, "print(\"All conversion parameters are set properly\")");
        swift.addLineWithPadding(12, "}");
        swift.addLineWithPadding(8, "}");
        swift.addLineWithPadding(4, "}");
        swift.addLine("");
        swift.addLine("}");

        return swift.get();
    }

    @Override
    public String getObjectiveCImplementation() {
        PlaintextFile objc = new PlaintextFile();
        objc.addLine("#import \"AppDelegate.h\"");
        objc.addLine("#import <Ambassador/Ambassador.h>");
        objc.addLine("");
        objc.addLine("@interface AppDelegate ()");
        objc.addLine("");
        objc.addLine("@end");
        objc.addLine("");
        objc.addLine("@implementation AppDelegate");
        objc.addLine("");
        objc.addLine("- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {");

        objc.addLineWithPadding(4, String.format("[AmbassadorSDK runWithUniversalToken:\"%s\" universalID:\"%s\"];", User.get().getSdkToken(), User.get().getUniversalId()));
        objc.addLine("");
        objc.addLineWithPadding(4, "AMBConversionParameters *conversionParameters = [[AMBConversionParameters alloc] init];");
        objc.addLine("");
        objc.addLineWithPadding(4, String.format("conversionParameters.mbsy_campaign = @%s;", model.getCampaign()));
        objc.addLineWithPadding(4, String.format("conversionParameters.mbsy_email = @\"%s\";", model.getEmail()));
        objc.addLineWithPadding(4, String.format("conversionParameters.mbsy_revenue = @%s;", model.getRevenue()));
        objc.addLineWithPadding(4, String.format("conversionParameters.mbsy_first_name = @\"%s\";", model.getFirstName()));
        objc.addLineWithPadding(4, String.format("conversionParameters.mbsy_last_name = @\"%s\";", model.getLastName()));
        objc.addLineWithPadding(4, String.format("conversionParameters.mbsy_email_new_ambassador = @%s;", model.getCampaign() == 1 ? "YES" : "NO"));
        objc.addLineWithPadding(4, String.format("conversionParameters.mbsy_uid = @\"%s\";", model.getUid()));
        objc.addLineWithPadding(4, String.format("conversionParameters.mbsy_custom1 = @\"%s\";", model.getCustom1()));
        objc.addLineWithPadding(4, String.format("conversionParameters.mbsy_custom2 = @\"%s\";", model.getCustom2()));
        objc.addLineWithPadding(4, String.format("conversionParameters.mbsy_custom3 = @\"%s\";", model.getCustom3()));
        objc.addLineWithPadding(4, String.format("conversionParameters.mbsy_auto_create = @%s;", model.getAutoCreate() == 1 ? "YES" : "NO"));
        objc.addLineWithPadding(4, String.format("conversionParameters.mbsy_deactivate_new_ambassador = @%s;", model.getDeactivateNewAmbassador() == 1 ? "YES" : "NO"));
        objc.addLineWithPadding(4, String.format("conversionParameters.mbsy_transaction_uid = @\"%s\";", model.getTransactionUid()));
        objc.addLineWithPadding(4, String.format("conversionParameters.mbsy_add_to_group_id = @\"%s\";", model.getAddToGroupId()));
        objc.addLineWithPadding(4, String.format("conversionParameters.mbsy_event_data1 = @\"%s\";", model.getEventData1()));
        objc.addLineWithPadding(4, String.format("conversionParameters.mbsy_event_data2 = @\"%s\";", model.getEventData2()));
        objc.addLineWithPadding(4, String.format("conversionParameters.mbsy_event_data3 = @\"%s\";", model.getEventData3()));
        objc.addLineWithPadding(4, String.format("conversionParameters.mbsy_is_approved = @%s;", model.getIsApproved() == 1 ? "YES" : "NO"));
        objc.addLine("");

        objc.addLineWithPadding(4, "[AmbassadorSDK registerConversion:conversionParameters restrictToInstall:NO completion:^(NSError *error) {");
        objc.addLineWithPadding(8, "if (error) {");
        objc.addLineWithPadding(12, "NSLog(@\"Error registering conversion - %@\", error);");
        objc.addLineWithPadding(8, "} else {");
        objc.addLineWithPadding(12, "NSLog(@\"Conversion registered successfully!\");");
        objc.addLineWithPadding(8, "};");
        objc.addLineWithPadding(4, "}];");

        objc.addLine("");
        objc.addLineWithPadding(4, "return YES;");

        objc.addLine("}");
        objc.addLine("");
        objc.addLine("@end");

        return objc.get();
    }

    @Override
    public String getZipName() {
        return "ambassador-conversion.zip";
    }

    @Override
    public String javaClassName() {
        return  "MyApplication";
    }

    @Override
    public String iOSClassName() {
        return "AppDelegate";
    }

}
