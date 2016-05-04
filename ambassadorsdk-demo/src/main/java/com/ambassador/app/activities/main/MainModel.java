package com.ambassador.app.activities.main;

import android.support.v4.app.Fragment;

import com.ambassador.app.activities.main.conversion.ConversionFragment;
import com.ambassador.app.activities.main.identify.IdentifyFragment;
import com.ambassador.app.activities.main.integration.IntegrationFragment;
import com.ambassador.app.activities.main.settings.SettingsFragment;

public class MainModel {

    protected IntegrationFragment integrationFragment;
    protected IdentifyFragment identifyFragment;
    protected ConversionFragment conversionFragment;
    protected SettingsFragment settingsFragment;
    protected Fragment[] fragments;

    protected MainActivity.TabFragment selectedFragment;

}
