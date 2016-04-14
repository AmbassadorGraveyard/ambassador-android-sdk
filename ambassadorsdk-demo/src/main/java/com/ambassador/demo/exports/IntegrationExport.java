package com.ambassador.demo.exports;

import com.ambassador.demo.data.Integration;

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
        return null;
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
        return "ambassador-integration.zip";
    }

}
