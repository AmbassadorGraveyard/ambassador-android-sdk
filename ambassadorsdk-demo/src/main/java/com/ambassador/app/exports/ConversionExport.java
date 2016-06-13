package com.ambassador.app.exports;

import android.os.Bundle;

import com.ambassador.app.data.User;
import com.ambassador.app.exports.models.ConversionExportModel;
import com.ambassador.app.utils.AssetFile;

public class ConversionExport extends BaseExport<ConversionExportModel> {

    protected enum Language {
        JAVA, SWIFT, OBJ_C
    }

    @Override
    public String getReadme() {
        return new AssetFile("exports/conversion/readme.txt").getAsString().replaceAll("\n", "<br/>");
    }

    @Override
    public String getJavaImplementation() {
        return processVariableHandlebarValues(processConstantHandlebarValues(new AssetFile("exports/conversion/MyApplication.java").getAsString()), Language.JAVA);
    }

    @Override
    public String getSwiftImplementation() {
        return processVariableHandlebarValues(processConstantHandlebarValues(new AssetFile("exports/conversion/AppDelegate.swift").getAsString()), Language.SWIFT);
    }

    @Override
    public String getObjectiveCImplementation() {
        return processVariableHandlebarValues(processConstantHandlebarValues(new AssetFile("exports/conversion/AppDelegate.m").getAsString()), Language.OBJ_C);
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
        removeNullInstancesInPlace(model);

        Bundle conversionProperties = model.conversionProperties;
        Bundle conversionOptions = model.conversionOptions;
        Bundle identifyTraits = model.identifyTraits;
        Bundle identifyOptions = model.identifyOptions;

        return text
                // -- Misc. Replacements
                .replace("{{SDKTOKEN}}", User.get().getUniversalToken())
                .replace("{{UNIVERSALID}}", User.get().getUniversalId())
                .replace("{{USERID}}", "")
                // -- Identify Traits
                .replace("{{EMAIL}}", identifyTraits.getString("email"))
                .replace("{{FIRSTNAME}}", identifyTraits.getString("firstName"))
                .replace("{{LASTNAME}}", identifyTraits.getString("lastName"))
                .replace("{{GROUPS}}", identifyTraits.getString("addToGroups"))
                .replace("{{CUSTOM1}}", identifyTraits.getString("custom1"))
                .replace("{{CUSTOM2}}", identifyTraits.getString("custom2"))
                .replace("{{CUSTOM3}}", identifyTraits.getString("custom3"))
                // -- Identify Options
                .replace("{{CAMPAIGN}}", identifyOptions.getString("campaign"))
                // -- Campaign Properties
                .replace("{{REVENUE}}", String.valueOf(conversionProperties.getFloat("revenue")))
                .replace("{{EVENTDATA1}}", conversionProperties.getString("eventData1"))
                .replace("{{EVENTDATA2}}", conversionProperties.getString("eventData2"))
                .replace("{{EVENTDATA3}}", conversionProperties.getString("eventData3"))
                .replace("{{ORDERID}}", conversionProperties.getString("orderId"));
    }

    protected String processVariableHandlebarValues(String text, Language language) {
        Bundle conversionProperties = model.conversionProperties;
        Bundle conversionOptions = model.conversionOptions;
        Bundle identifyTraits = model.identifyTraits;
        Bundle identifyOptions = model.identifyOptions;

        switch (language) {
            case JAVA:
                text = text
                        .replace("{{EMAILNEWAMBASSADOR}}", conversionProperties.getInt("emailNewAmbassador") == 1 ? "1" : "0")
                        .replace("{{COMMISSIONAPPROVED}}", conversionProperties.getInt("commissionApproved") == 1 ? "1" : "0");
                break;
            case SWIFT:
                text = text
                        .replace("{{EMAILNEWAMBASSADOR}}", conversionProperties.getInt("emailNewAmbassador") == 1 ? "true" : "false")
                        .replace("{{COMMISSIONAPPROVED}}", conversionProperties.getInt("commissionApproved") == 1 ? "true" : "false");
                break;
            case OBJ_C:
                text = text
                        .replace("{{EMAILNEWAMBASSADOR}}", conversionProperties.getInt("emailNewAmbassador") == 1 ? "YES" : "NO")
                        .replace("{{COMMISSIONAPPROVED}}", conversionProperties.getInt("commissionApproved") == 1 ? "YES" : "NO");
                break;
            default:
                return null;
        }

        return text;
    }

    protected void removeNullInstancesInPlace(ConversionExportModel model) {
        if (model.conversionProperties == null) {
            model.conversionProperties = new Bundle();
        }
        Bundle conversionProperties = model.conversionProperties;

        if (model.conversionOptions == null) {
            model.conversionOptions = new Bundle();
        }
        Bundle conversionOptions = model.conversionOptions;

        if (model.identifyTraits == null) {
            model.identifyTraits = new Bundle();
        }
        Bundle identifyTraits = model.identifyTraits;

        if (model.identifyOptions == null) {
            model.identifyOptions = new Bundle();
        }
        Bundle identifyOptions = model.identifyOptions;

        removeBundleNullInstance(identifyTraits, "email");
        removeBundleNullInstance(identifyTraits, "firstName");
        removeBundleNullInstance(identifyTraits, "lastName");
        removeBundleNullInstance(identifyTraits, "addToGroups");
        removeBundleNullInstance(identifyTraits, "customLabel1");
        removeBundleNullInstance(identifyTraits, "customLabel2");
        removeBundleNullInstance(identifyTraits, "customLabel3");

        removeBundleNullInstance(identifyOptions, "campaign");

        removeBundleNullInstance(conversionProperties, "eventData1");
        removeBundleNullInstance(conversionProperties, "eventData2");
        removeBundleNullInstance(conversionProperties, "eventData3");
        removeBundleNullInstance(conversionProperties, "orderId");
    }

    protected void removeBundleNullInstance(Bundle bundle, String key) {
        if (bundle.getString(key, null) == null) {
            bundle.putString(key, "");
        }
    }

}
