package com.ambassador.app.exports;

import com.ambassador.app.exports.models.ConversionExportModel;
import com.ambassador.app.utils.AssetFile;

public class ConversionExport extends BaseExport<ConversionExportModel> {

    protected enum Language {
        JAVA, SWIFT, OBJ_C
    }

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

    protected String processConstantHandlebarValues(String text) {


        return "";
    }

    protected String processVariableHandlebarValues(String text, Language language) {
        switch (language) {
            case JAVA:
                break;
            case SWIFT:
                break;
            case OBJ_C:
                break;
            default:
                return null;
        }
        return "";
    }

}
