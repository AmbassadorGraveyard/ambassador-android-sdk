package com.ambassador.demo.exports;

import com.ambassador.demo.data.Integration;

public class IntegrationExport implements Export<Integration> {

    @Override
    public void setModel(Integration integration) {
        
    }

    @Override
    public String zip() {
        return null;
    }

}
