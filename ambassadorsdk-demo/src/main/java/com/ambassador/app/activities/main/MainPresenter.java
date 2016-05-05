package com.ambassador.app.activities.main;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;

import com.ambassador.ambassadorsdk.internal.activities.BasePresenter;
import com.ambassador.app.activities.main.conversion.ConversionFragment;
import com.ambassador.app.activities.main.identify.IdentifyFragment;
import com.ambassador.app.activities.main.integration.IntegrationFragment;
import com.ambassador.app.activities.main.settings.SettingsFragment;

public class MainPresenter extends BasePresenter<MainModel, MainView> {

    @Override
    protected void updateView() {
        view().setIntegrationFragment(model.integrationFragment);
        view().setIdentifyFragment(model.identifyFragment);
        view().setConversionFragment(model.conversionFragment);
        view().setSettingsFragment(model.settingsFragment);

        view().setMenuItemIcon(model.selectedFragment.getActionDrawable());
        view().setMenuItemVisibility(model.selectedFragment.getActionVisibility());
        view().setToolbarTitle(Html.fromHtml("<small>" + model.selectedFragment.getTitle() + "</small>"));
    }

    @Override
    public void bindView(@NonNull MainView view) {
        super.bindView(view);
        if (model == null) {
            loadData();
        }
    }

    protected void loadData() {
        MainModel mainModel = new MainModel();
        mainModel.integrationFragment = new IntegrationFragment();
        mainModel.identifyFragment = new IdentifyFragment();
        mainModel.conversionFragment = new ConversionFragment();
        mainModel.settingsFragment = new SettingsFragment();

        mainModel.fragments = new Fragment[] {
                mainModel.integrationFragment,
                mainModel.identifyFragment,
                mainModel.conversionFragment,
                mainModel.settingsFragment
        };

        mainModel.selectedFragment = mainModel.integrationFragment;
        setModel(mainModel);
    }

    public void onPageSelected(int position) {
        try {
            model.selectedFragment = (MainActivity.TabFragment) model.fragments[position];
            updateView();
        } catch (Exception e) {
            Log.e("Ambassador", e.toString());
            // Ignore action, not compatible fragment.
        }
    }

    public void onOptionsItemSelected() {
        model.selectedFragment.onActionClicked();
        updateView();
    }

}
