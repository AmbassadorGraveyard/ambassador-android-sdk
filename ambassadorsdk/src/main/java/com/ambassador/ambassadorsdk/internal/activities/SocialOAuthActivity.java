package com.ambassador.ambassadorsdk.internal.activities;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.Utilities;

import butterfork.Bind;
import butterfork.ButterFork;

/**
 * Overall activity to handle a social networks oauth authentication using a web view.
 */
public class SocialOAuthActivity extends AppCompatActivity {

    /** The automatically added Toolbar for the Activity */
    @Bind(B.id.action_bar) protected Toolbar toolbar;

    /** WebView used for loading all urls handled by the AuthInterface. */
    @Bind(B.id.wvLogin) protected WebView wvLogin;

    /** The AuthInterface implementation to use when evaluating URLs */
    protected AuthInterface authInterface;

    /**
     * Gets and sets an AuthInterface implementation based on data passed in the intent. Configures
     * a WebView and loads the login url on the WebView. The AuthInterface will then handle everything.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterFork.bind(this);

        String socialNetwork = getIntent().getStringExtra("socialNetwork");
        if (socialNetwork == null) {
            finish();
            return;
        }

        authInterface = getAuthInterfaceForIntentData(socialNetwork);

        if (authInterface == null) {
            finish();
            return;
        }

        setUpToolbar();

        wvLogin.setWebViewClient(new OAuthWebClient());
        wvLogin.loadUrl(authInterface.getLoginUrl());
    }

    /**
     * Processes intent data passed to it and will return the AuthInterface implementation appropriate
     * to use for the lifetime of the Activity.
     * @param intentData the String intent data keyed on "socialNetwork".
     * @return an AuthInterface object implementation to use in the Activity.
     */
    protected AuthInterface getAuthInterfaceForIntentData(String intentData) {
        switch (intentData.toLowerCase()) {
            case "facebook":
                return new FacebookAuth();
            case "twitter":
                return new TwitterAuth();
            case "linkedin":
                return new LinkedInAuth();
            default:
                return null;
        }
    }

    /**
     * Sets the toolbar color and title, and sets a back arrow in the top left.
     */
    protected void setUpToolbar() {
        RAFOptions raf = RAFOptions.get();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(authInterface.getToolbarText());
        }

        Drawable arrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        arrow.setColorFilter(raf.getTwitterToolbarArrowColor(), PorterDuff.Mode.SRC_ATOP);

        if (toolbar == null) return;

        toolbar.setNavigationIcon(arrow);
        toolbar.setBackgroundColor(raf.getTwitterToolbarColor());
        toolbar.setTitleTextColor(raf.getTwitterToolbarTextColor());

        Utilities.setStatusBar(getWindow(), raf.getTwitterToolbarColor());
    }

    /**
     * WebViewClient extension to allow AuthInterface implementations to handle URL changes.
     */
    protected class OAuthWebClient extends WebViewClient {

        /**
         * Allows handling of a url request before it is loaded.
         * @param view the WebView that is loading the url.
         * @param url the url that the WebView wants to load.
         * @return true if handled by an AuthInterface, else false.
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (authInterface.canHandleUrl(url)) {
                authInterface.handleUrl(url);
                return true;
            }

            return false;
        }

        /**
         * Allows handling of a url request after it is loaded.
         * @param view the WebView that loaded the url.
         * @param url the url that the WebView loaded.
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

    }

    /**
     * Provides definition for what a social oauth implementation needs to do.
     */
    protected interface AuthInterface {

        /**
         * Will return the AuthInterface implementation's set toolbar text String.
         * @return a String to set as the toolbar title.
         */
        @NonNull
        String getToolbarText();

        /**
         * Will get a url to a login page to initially load into the web view.
         * @return a url loadable by the web view.
         */
        @NonNull
        String getLoginUrl();

        /**
         * Will handle the url, assuming it knows how to.
         * @param url the url loaded by the web view.
         */
        void handleUrl(@Nullable String url);

        /**
         * Determines if the url passed is one that the oauth implementation can handle.
         * @param url the url loaded by the web view.
         */
        boolean canHandleUrl(@Nullable String url);

    }

    /**
     * Handles login with Facebook.
     */
    protected static class FacebookAuth implements AuthInterface {

        @NonNull
        @Override
        public String getToolbarText() {
            return "Login to Facebook";
        }

        @NonNull
        @Override
        public String getLoginUrl() {
            return null;
        }

        @Override
        public void handleUrl(@Nullable String url) {

        }

        @Override
        public boolean canHandleUrl(@Nullable String url) {
            return false;
        }

    }

    /**
     * Handles login with Twitter.
     */
    protected static class TwitterAuth implements AuthInterface {

        @NonNull
        @Override
        public String getToolbarText() {
            return "Login to Twitter";
        }

        @NonNull
        @Override
        public String getLoginUrl() {
            return null;
        }

        @Override
        public void handleUrl(@Nullable String url) {

        }

        @Override
        public boolean canHandleUrl(@Nullable String url) {
            return false;
        }

    }

    /**
     * Handles login with LinkedIn.
     */
    protected static class LinkedInAuth implements AuthInterface {

        @NonNull
        @Override
        public String getToolbarText() {
            return "Login to LinkedIn";
        }

        @NonNull
        @Override
        public String getLoginUrl() {
            return null;
        }

        @Override
        public void handleUrl(@Nullable String url) {

        }

        @Override
        public boolean canHandleUrl(@Nullable String url) {
            return false;
        }

    }

}
