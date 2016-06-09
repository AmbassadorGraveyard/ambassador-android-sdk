package com.ambassador.app.exports;

import com.ambassador.app.utils.AssetFile;

public class IdentifyExport extends BaseExport<IdentifyExport> {

    @Override
    public String getReadme() {
        return new AssetFile("exports/identify/readme.txt").getAsString();
    }

    @Override
    public String getJavaImplementation() {
        return processHandlebars(new AssetFile("exports/identify/MyApplication.java").getAsString());
    }

    @Override
    public String getSwiftImplementation() {
        return processHandlebars(new AssetFile("exports/identify/AppDelegate.swift").getAsString());
    }

    @Override
    public String getObjectiveCImplementation() {
        return processHandlebars(new AssetFile("exports/identify/AppDelegate.m").getAsString());
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

    protected String processHandlebars(String text) {


        return text;
    }

}
