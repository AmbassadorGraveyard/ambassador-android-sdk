package com.example.ambassador.sdktestapplication;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ambassador.sdktestapplication.fragments.LoginFragment;
import com.example.ambassador.sdktestapplication.fragments.ReferFragment;
import com.example.ambassador.sdktestapplication.fragments.SignupFragment;
import com.example.ambassador.sdktestapplication.fragments.StoreFragment;

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

        vpPages.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));
        tlTabs.setupWithViewPager(vpPages);

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

    private final class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        private TabModel[] tabs;

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            tabs = new TabModel[4];
            tabs[0] = new TabModel("Login", new LoginFragment());
            tabs[1] = new TabModel("Sign Up", new SignupFragment());
            tabs[2] = new TabModel("Buy Now", new StoreFragment());
            tabs[3] = new TabModel("Refer a Friend", new ReferFragment());
        }

        @Override
        public int getCount() {
            return tabs.length;
        }

        @Override
        public Fragment getItem(int position) {
            return tabs[position].getFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position].getTitle();
        }

        private final class TabModel {

            private String title;
            private Fragment fragment;

            public TabModel(String title, Fragment fragment) {
                this.title = title;
                this.fragment = fragment;
            }

            public String getTitle() {
                return title;
            }

            public Fragment getFragment() {
                return fragment;
            }

        }

    }

}
