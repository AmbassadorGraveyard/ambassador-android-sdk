package com.ambassador.app.exports;

import android.os.Bundle;

import com.ambassador.app.data.User;
import com.ambassador.app.exports.models.IdentifyExportModel;
import com.ambassador.app.utils.AssetFile;

public class IdentifyExport extends BaseExport<IdentifyExportModel> {

    @Override
    public String getReadme() {
        return new AssetFile("exports/identify/readme.txt").getAsString().replaceAll("\n", "<br/>");
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
        if (text == null) return text;

        Bundle traits = model.traits;
        Bundle options = model.options;

        return text
                .replace("{{SDKTOKEN}}", User.get().getUniversalToken())
                .replace("{{UNIVERSALID}}", User.get().getUniversalId())
                .replace("{{USERID}}", model.userId != null ? model.userId : "null");
    }

}
