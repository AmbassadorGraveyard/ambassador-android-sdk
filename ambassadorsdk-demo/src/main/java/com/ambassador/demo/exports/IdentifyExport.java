package com.ambassador.demo.exports;

import com.ambassador.demo.data.User;

public class IdentifyExport extends BaseExport<String> {

    @Override
    public String getReadme() {
        PlaintextFile readme = new PlaintextFile();
        readme.addLine("Ambassador Android SDK v1.1.4");
        readme.addLine("Take a look at the Android docs for an in-depth explanation on adding and integrating the SDK:");
        readme.addLine("https://docs.getambassador.com/v2.0.0/page/android-sdk");
        readme.addLine("Check out the MyApplication.java file for an example of this identify request.");

        readme.addLine("");

        readme.addLine("Ambassador iOS SDK v1.0.3");
        readme.addLine("Take a look at the iOS docs for an in-depth explanation on adding and integrating the SDK:");
        readme.addLine("https://docs.getambassador.com/v2.0.0/page/ios-sdk");
        readme.addLine("Check out the AppDelegate.m or AppDelegate.swift files for examples of this identify request.");

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

}
