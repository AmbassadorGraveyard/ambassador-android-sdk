package com.ambassador.demo.exports;

import com.ambassador.demo.data.Integration;

public class IntegrationExport extends BaseExport<Integration> {

    @Override
    public String getReadme() {
        return null;
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
