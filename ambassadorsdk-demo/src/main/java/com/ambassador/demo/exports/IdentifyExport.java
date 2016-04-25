package com.ambassador.demo.exports;

import com.ambassador.demo.data.User;

public class IdentifyExport extends BaseExport<String> {

    @Override
    public String getReadme() {
        PlaintextFile readme = new PlaintextFile();
        readme.addHtmlLine("Hey! I've attached the examples you should need to identify users in our mobile app with the Ambassador SDK.");
        readme.addHtmlLine("");
        readme.addHtmlLine("The attachment includes examples for");
        readme.addHtmlLine("");
        readme.addHtmlLine("iOS (v1.0.3): AppDelegate.m or AppDelegate.swift");
        readme.addHtmlLine("");
        readme.addHtmlLine("Android (v1.1.4): MyApplication.java.");
        readme.addHtmlLine("");
        readme.addHtmlLine("For in-depth explanations on adding and integrating the SDKs check out the following");
        readme.addHtmlLine("");
        readme.addHtmlLine("iOS: https://docs.getambassador.com/v2.0.0/page/ios-sdk");
        readme.addHtmlLine("");
        readme.addHtmlLine("Android: https://docs.getambassador.com/v2.0.0/page/android-sdk.");
        readme.addHtmlLine("");
        readme.addHtmlLine("Let me know if you have any questions!");
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
        java.addLine("import com.ambassador.ambassadorsdk.AmbassadorSDK;");
        java.addLine("");
        java.addLine("public class MyApplication extends Application {");
        java.addLine("");
        java.addLineWithPadding(4, "@Override");
        java.addLineWithPadding(4, "public void onCreate() {");
        java.addLineWithPadding(8, "super.onCreate();");
        java.addLineWithPadding(8, String.format("AmbassadorSDK.runWithKeys(this, \"SDKToken %s\", \"%s\");", User.get().getSdkToken(), User.get().getUniversalId()));
        java.addLineWithPadding(8, String.format("AmbassadorSDK.identify(\"%s\");", model));
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
        swift.addLineWithPadding(8, String.format("AmbassadorSDK.identifyWithEmail(\"%s\")", model));
        swift.addLineWithPadding(8, "");
        swift.addLineWithPadding(8, "return true");
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
        objc.addLineWithPadding(4, String.format("[AmbassadorSDK identifyWithEmail:@\"%s\"];", model));
        objc.addLineWithPadding(4, "");
        objc.addLineWithPadding(4, "return YES;");
        objc.addLine("}");
        objc.addLine("");
        objc.addLine("@end");

        return objc.get();
    }

    @Override
    public String getZipName() {
        return "ambassador-identify.zip";
    }

    @Override
    public String javaClassName() {
        return "MyApplication";
    }

    @Override
    public String iOSClassName() {
        return "AppDelegate";
    }

}
