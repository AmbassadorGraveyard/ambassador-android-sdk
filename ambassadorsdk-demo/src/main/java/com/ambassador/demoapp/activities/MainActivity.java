package com.ambassador.demoapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.WelcomeScreenDialog;
import com.ambassador.demoapp.Demo;
import com.ambassador.demoapp.R;
import com.ambassador.demoapp.data.Integration;
import com.ambassador.demoapp.data.User;
import com.ambassador.demoapp.fragments.ConversionFragment;
import com.ambassador.demoapp.fragments.IdentifyFragment;
import com.ambassador.demoapp.fragments.ReferFragment;
import com.ambassador.demoapp.fragments.SettingsFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class MainActivity extends AppCompatActivity {

    @Bind(R.id.tlTabs)      protected TabLayout     tlTabs;
    @Bind(R.id.vpPages)     protected ViewPager     vpPages;

    protected TabFragmentPagerAdapter adapter;

    protected WelcomeScreenDialog welcomeScreenDialog;

    protected MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Demo.get().runWithKeys("SDKToken " + User.get().getSdkToken(), User.get().getUniversalId());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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

        setTitle(Html.fromHtml("<small>" + adapter.getTitle(0) + "</small>"));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.actionBarColor)));
        }

        WelcomeScreenDialog.Parameters parameters =
                new WelcomeScreenDialog.Parameters()
                        .setButtonOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, "Button click", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setLink1OnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, "Link1", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setLink2OnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, "Link2", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setTopBarText("Welcome")
                        .setTitleText("{{ name }} has referred you to Ambassador!")
                        .setMessageText("You understand the value of referrals. Maybe you've even explored referral marketing software.")
                        .setButtonText("CREATE AN ACCOUNT")
                        .setLink1Text("Testimonials")
                        .setLink2Text("Request Demo")
                        .setColorTheme(Color.parseColor("#4198d1"));

        AmbassadorSDK.presentWelcomeScreen(this, new WelcomeScreenDialog.AvailabilityCallback() {
            @Override
            public void available(WelcomeScreenDialog welcomeScreenDialog) {
                welcomeScreenDialog.show();
                MainActivity.this.welcomeScreenDialog = welcomeScreenDialog;
            }
        }, parameters);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuItem = menu.findItem(R.id.action_main);
        menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.edit_icon));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, CustomizationActivity.class);
        startActivity(intent);
        return true;
    }

    public void switchToTabAtIndex(int position) {
        vpPages.setCurrentItem(position);
    }

    protected ViewPager.OnPageChangeListener vpPagesChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            Fragment fragment = adapter.getItem(position);
            fragment.onResume();
            setToolbarTitle(Html.fromHtml("<small>" + adapter.getTitle(position) + "</small>"));

            if (menuItem == null) return;

            if (fragment instanceof ReferFragment) {
                menuItem.setVisible(true);
                menuItem.setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.edit_icon));
            } else if (fragment instanceof SettingsFragment) {
                menuItem.setVisible(false);
            } else {
                menuItem.setVisible(true);
                menuItem.setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_share_white));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setToolbarTitle(Spanned title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    private final class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        private TabModel[] tabs;

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            tabs = new TabModel[4];
            String rafTitle = getResources().getDisplayMetrics().densityDpi < 300 ? "Referrals" : "Refer a Friend";
            tabs[0] = new TabModel(rafTitle, R.drawable.ic_raf, new ReferFragment())
                    .setContentDescription("referTab");
            tabs[1] = new TabModel("Identify", R.drawable.ic_identify, new IdentifyFragment())
                    .setContentDescription("loginTab");
            tabs[2] = new TabModel("Conversion", R.drawable.ic_conversion, new ConversionFragment())
                    .setContentDescription("storeTab");
            tabs[3] = new TabModel("Settings", R.drawable.ic_settings, new SettingsFragment())
                    .setContentDescription("signupTab");
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

            ImageView ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
            ivIcon.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, tabs[position].getDrawableId()));

            TextView tvTitle = (TextView) view.findViewById(R.id.tvTabTitle);
            tvTitle.setText(tabs[position].getTitle());

            view.setContentDescription(getContentDescription(position));

            return view;
        }

        @Nullable
        public String getContentDescription(int position) {
            return tabs[position].getContentDescription();
        }

        public String getTitle(int position) {
            return tabs[position].getTitle();
        }

        private final class TabModel {

            private String title;
            private int drawableId;
            private Fragment fragment;
            private String contentDescription;

            public TabModel(String title, @DrawableRes int drawableId, Fragment fragment) {
                this.title = title;
                this.drawableId = drawableId;
                this.fragment = fragment;
            }

            public String getTitle() {
                return title;
            }

            public int getDrawableId() {
                return drawableId;
            }

            public Fragment getFragment() {
                return fragment;
            }

            public TabModel setContentDescription(String contentDescription) {
                this.contentDescription = contentDescription;
                return this;
            }

            public String getContentDescription() {
                return contentDescription;
            }

        }

    }

    public interface TabFragment {

        void onActionClicked();
        Drawable getActionDrawable();

    }

}
