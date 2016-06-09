package com.ambassador.app.exports;

import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.app.utils.AssetFile;

public class ConversionExport extends BaseExport<ConversionParameters> {

    @Override
    public String getReadme() {
        return new AssetFile("exports/conversion/readme.txt").getAsString();
    }

    @Override
    public String getJavaImplementation() {
        return new AssetFile("exports/conversion/MyApplication.java").getAsString();
    }

    @Override
    public String getSwiftImplementation() {
        return new AssetFile("exports/conversion/AppDelegate.swift").getAsString();
    }

    @Override
    public String getObjectiveCImplementation() {
        return new AssetFile("exports/conversion/AppDelegate.m").getAsString();
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
