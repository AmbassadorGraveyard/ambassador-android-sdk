package com.ambassador.app.activities.main;

import android.support.annotation.DrawableRes;
import android.text.Spanned;

import com.ambassador.app.activities.main.conversion.ConversionFragment;
import com.ambassador.app.activities.main.identify.IdentifyFragment;
import com.ambassador.app.activities.main.integration.IntegrationFragment;
import com.ambassador.app.activities.main.settings.SettingsFragment;

public interface MainView {

    void setIntegrationFragment(IntegrationFragment integrationFragment);
    void setIdentifyFragment(IdentifyFragment identifyFragment);
    void setConversionFragment(ConversionFragment conversionFragment);
    void setSettingsFragment(SettingsFragment settingsFragment);

    void setToolbarTitle(Spanned title);
    void setMenuItemIcon(@DrawableRes int drawable);
    void setMenuItemVisibility(boolean visible);

}
