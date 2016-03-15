package com.ambassador.demoapp;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.WelcomeScreenDialog;
import com.ambassador.demoapp.fragments.LoginFragment;
import com.ambassador.demoapp.fragments.ReferFragment;
import com.ambassador.demoapp.fragments.SignupFragment;
import com.ambassador.demoapp.fragments.StoreFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class MainActivity extends AppCompatActivity {

    @Bind(R.id.tlTabs)      protected TabLayout     tlTabs;
    @Bind(R.id.vpPages)     protected ViewPager     vpPages;

    protected TabFragmentPagerAdapter adapter;

    protected WelcomeScreenDialog welcomeScreenDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        setTitle(Html.fromHtml("<small>Ambassador Demo</small>"));
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

    public void switchToTabAtIndex(int position) {
        vpPages.setCurrentItem(position);
    }

    protected ViewPager.OnPageChangeListener vpPagesChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            adapter.getItem(position).onResume();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private final class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        private TabModel[] tabs;

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            tabs = new TabModel[4];
            tabs[0] = new TabModel("Login", R.drawable.ic_login, new LoginFragment())
                    .setContentDescription("loginTab");
            tabs[1] = new TabModel("Sign Up", R.drawable.ic_signup, new SignupFragment())
                    .setContentDescription("signupTab");
            tabs[2] = new TabModel("Buy Now", R.drawable.ic_buy, new StoreFragment())
                    .setContentDescription("storeTab");
            String rafTitle = getResources().getDisplayMetrics().densityDpi < 300 ? "Referrals" : "Refer a Friend";
            tabs[3] = new TabModel(rafTitle, R.drawable.ic_raf, new ReferFragment())
                    .setContentDescription("referTab");
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

            TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvTitle.setText(tabs[position].getTitle());

            view.setContentDescription(getContentDescription(position));

            return view;
        }

        @Nullable
        public String getContentDescription(int position) {
            return tabs[position].getContentDescription();
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

}
