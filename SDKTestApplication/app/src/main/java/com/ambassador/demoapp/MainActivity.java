package com.ambassador.demoapp;

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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ambassador.demoapp.fragments.LoginFragment;
import com.ambassador.demoapp.fragments.ReferFragment;
import com.ambassador.demoapp.fragments.SignupFragment;
import com.ambassador.demoapp.fragments.StoreFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class MainActivity extends AppCompatActivity {

    @Bind(R.id.tlTabs)      protected TabLayout     tlTabs;
    @Bind(R.id.vpPages)     protected ViewPager     vpPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        vpPages.setAdapter(adapter);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void switchToTabAtIndex(int position) {
        vpPages.setCurrentItem(position);
    }

    private final class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        private TabModel[] tabs;

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            tabs = new TabModel[4];
            tabs[0] = new TabModel("Login", R.drawable.ic_login, new LoginFragment());
            tabs[1] = new TabModel("Sign Up", R.drawable.ic_signup, new SignupFragment());
            tabs[2] = new TabModel("Buy Now", R.drawable.ic_buy, new StoreFragment());
            tabs[3] = new TabModel("Refer a Friend", R.drawable.ic_raf, new ReferFragment());
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

            return view;
        }

        private final class TabModel {

            private String title;
            private int drawableId;
            private Fragment fragment;

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

        }

    }

}
