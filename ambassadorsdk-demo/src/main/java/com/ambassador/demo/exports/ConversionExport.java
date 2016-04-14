package com.ambassador.demo.exports;

import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.demo.data.User;

public class ConversionExport extends BaseExport<ConversionParameters> {

    @Override
    public String getReadme() {
        PlaintextFile readme = new PlaintextFile();
        readme.addLine("Ambassador Android SDK v1.1.4");
        readme.addLine("Take a look at the Android docs for an in-depth explanation on adding and integrating the SDK:");
        readme.addLine("https://docs.getambassador.com/v2.0.0/page/android-sdk");
        readme.addLine("Check out the MyApplication.java file for an example of this conversion request.");

        readme.addLine("");

        readme.addLine("Ambassador iOS SDK v1.0.3");
        readme.addLine("Take a look at the iOS docs for an in-depth explanation on adding and integrating the SDK:");
        readme.addLine("https://docs.getambassador.com/v2.0.0/page/ios-sdk");
        readme.addLine("Check out the AppDelegate.m or AppDelegate.swift files for examples of this conversion request.");

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
        return null;
    }

    @Override
    public String getObjectiveCImplementation() {
        return null;
    }

    @Override
    public String getZipName() {
        return "ambassador-conversion.zip";
    }

}
