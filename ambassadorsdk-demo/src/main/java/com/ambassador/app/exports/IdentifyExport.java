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

        removeNullInstancesInPlace(model);

        Bundle traits = model.traits;
        Bundle address = traits.getBundle("address");
        Bundle options = model.options;

        return text
                .replace("{{SDKTOKEN}}", User.get().getUniversalToken())
                .replace("{{UNIVERSALID}}", User.get().getUniversalId())
                .replace("{{USERID}}", model.userId)
                .replace("{{EMAIL}}", traits.getString("email"))
                .replace("{{FIRSTNAME}}", traits.getString("firstName"))
                .replace("{{LASTNAME}}", traits.getString("lastName"))
                .replace("{{COMPANY}}", traits.getString("company"))
                .replace("{{PHONE}}", traits.getString("phone"))
                .replace("{{STREET}}", address.getString("street"))
                .replace("{{CITY}}", address.getString("city"))
                .replace("{{STATE}}", address.getString("state"))
                .replace("{{POSTALCODE}}", address.getString("postalCode"))
                .replace("{{COUNTRY}}", address.getString("country"))
                .replace("{{CAMPAIGN}}", options.getString("campaign"));
    }

    protected void removeNullInstancesInPlace(IdentifyExportModel model) {
        model.userId = model.userId != null ? model.userId : "";
        Bundle traits = model.traits;
        if (traits == null) {
            model.traits = new Bundle();
            traits = model.traits;
        }

        removeBundleNullInstance(traits, "email");
        removeBundleNullInstance(traits, "firstName");
        removeBundleNullInstance(traits, "lastName");
        removeBundleNullInstance(traits, "company");
        removeBundleNullInstance(traits, "phone");

        Bundle address = traits.getBundle("address");
        if (address == null) {
            traits.putBundle("address", new Bundle());
            address = traits.getBundle("address");
        }

        removeBundleNullInstance(address, "street");
        removeBundleNullInstance(address, "city");
        removeBundleNullInstance(address, "state");
        removeBundleNullInstance(address, "postalCode");
        removeBundleNullInstance(address, "country");

        Bundle options = model.options;
        if (options == null) {
            model.options = new Bundle();
            options = model.options;
        }

        removeBundleNullInstance(options, "campaign");
    }

    protected void removeBundleNullInstance(Bundle bundle, String key) {
        if (bundle.getString(key, null) == null) {
            bundle.putString(key, "");
        }
    }

}
