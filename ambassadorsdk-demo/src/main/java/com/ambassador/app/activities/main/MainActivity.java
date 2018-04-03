package com.ambassador.app.activities.main;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.app.R;
import com.ambassador.app.activities.PresenterManager;
import com.ambassador.app.activities.main.conversion.ConversionFragment;
import com.ambassador.app.activities.main.identify.IdentifyFragment;
import com.ambassador.app.activities.main.integration.IntegrationFragment;
import com.ambassador.app.activities.main.settings.SettingsFragment;
import com.ambassador.app.data.User;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class MainActivity extends AppCompatActivity implements MainView {

    protected MainPresenter mainPresenter;

    @Bind(R.id.vpPages) protected ViewPager vpPages;
    @Bind(R.id.tlTabs) protected TabLayout tlTabs;

    protected MenuItem menuItem;
    protected TabFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mainPresenter = new MainPresenter();
        } else {
            mainPresenter = PresenterManager.getInstance().restorePresenter(savedInstanceState);
            if (mainPresenter == null) mainPresenter = new MainPresenter();
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        AmbassadorSDK AmbassadorSDK = new AmbassadorSDK();
        AmbassadorSDK.runWithKeys(this, "SDKToken " + User.get().getSdkToken(), User.get().getUniversalId());

        adapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        vpPages.setOffscreenPageLimit(4);
        vpPages.setAdapter(adapter);
        vpPages.addOnPageChangeListener(vpPagesChangeListener);
        tlTabs.setupWithViewPager(vpPages);

        for (int i = 0; i < tlTabs.getTabCount(); i++) {
            TabLayout.Tab tab = tlTabs.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(adapter.getTabView(i));
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.actionBarColor)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuItem = menu.findItem(R.id.action_main);
        mainPresenter.updateView();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mainPresenter.onOptionsItemSelected();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainPresenter.bindView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainPresenter.unbindView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PresenterManager.getInstance().savePresenter(mainPresenter, outState);
    }

    public void notifyIntegrationSetInvalidated() {
        mainPresenter.updateView();
    }

    @Override
    public void setIntegrationFragment(IntegrationFragment integrationFragment) {
        this.adapter.tabs[0].fragment = integrationFragment;
    }

    @Override
    public void setIdentifyFragment(IdentifyFragment identifyFragment) {
        this.adapter.tabs[1].fragment = identifyFragment;
    }

    @Override
    public void setConversionFragment(ConversionFragment conversionFragment) {
        this.adapter.tabs[2].fragment = conversionFragment;
    }

    @Override
    public void setSettingsFragment(SettingsFragment settingsFragment) {
        this.adapter.tabs[3].fragment = settingsFragment;
    }

    @Override
    public void setToolbarTitle(Spanned title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void setMenuItemIcon(@DrawableRes int drawable) {
        if (menuItem != null) {
            menuItem.setIcon(drawable);
        }
    }

    @Override
    public void setMenuItemVisibility(boolean visible) {
        if (menuItem != null) {
            menuItem.setVisible(visible);
        }
    }

    protected ViewPager.OnPageChangeListener vpPagesChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            mainPresenter.onPageSelected(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageScrollStateChanged(int state) {}

    };

    protected final class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        protected TabModel[] tabs;

        @Bind(R.id.ivIcon) protected ImageView ivIcon;
        @Bind(R.id.tvTitle) protected TextView tvTitle;

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            tabs = new TabModel[4];
            tabs[0] = new TabModel("Refer a Friend", R.drawable.ic_integration);
            tabs[1] = new TabModel("Identify", R.drawable.ic_identify);
            tabs[2] = new TabModel("Conversion", R.drawable.ic_conversion);
            tabs[3] = new TabModel("Settings", R.drawable.ic_settings);
        }

        @Override
        public int getCount() {
            return tabs.length;
        }

        @Override
        public Fragment getItem(int position) {
            return tabs[position].getFragment();
        }

        @NonNull
        public View getTabView(int position) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.tab, null);
            ButterKnife.bind(this, view);

            ivIcon.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, tabs[position].getDrawableId()));
            tvTitle.setText(tabs[position].getTitle());

            return view;
        }

        protected final class TabModel {

            protected String title;
            protected int drawableId;
            protected Fragment fragment;

            public TabModel(String title, @DrawableRes int drawableId) {
                this.title = title;
                this.drawableId = drawableId;
            }

            public String getTitle() {
                return title;
            }

            public int getDrawableId() {
                return drawableId;
            }

            public Fragment getFragment() {
                return fragment != null ? fragment : new Fragment();
            }

        }

    }

    public interface TabFragment {

        void onActionClicked();
        boolean getActionVisibility();
        @DrawableRes int getActionDrawable();
        String getTitle();

    }


}