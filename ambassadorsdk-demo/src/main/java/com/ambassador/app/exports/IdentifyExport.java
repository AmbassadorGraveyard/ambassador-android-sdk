package com.ambassador.app.exports;

import com.ambassador.app.utils.AssetFile;

public class IdentifyExport extends BaseExport<IdentifyExport> {

    @Override
    public String getReadme() {
        PlaintextFile readme = new PlaintextFile();
        readme.addHtmlLine("Hey! I've attached examples showing how to identify a user in our mobile app with the Ambassador SDK.");
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
        return new AssetFile("exports/identify/MyApplication.java").getAsString();
    }

    @Override
    public String getSwiftImplementation() {
        return new AssetFile("exports/identify/AppDelegate.swift").getAsString();
    }

    @Override
    public String getObjectiveCImplementation() {
        return new AssetFile("exports/identify/AppDelegate.m").getAsString();
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
