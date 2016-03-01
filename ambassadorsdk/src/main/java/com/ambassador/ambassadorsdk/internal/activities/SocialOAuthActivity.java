package com.ambassador.ambassadorsdk.internal.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;

import butterfork.Bind;
import butterfork.ButterFork;

/**
 * Overall activity to handle a social networks oauth authentication using a web view.
 */
public class SocialOAuthActivity extends AppCompatActivity {

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
            return super.shouldOverrideUrlLoading(view, url);
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
        String getToolbarText();

        /**
         * Will get a url to a login page to initially load into the web view.
         * @return a url loadable by the web view.
         */
        String getLoginUrl();

        /**
         * Will handle the url, assuming it knows how to.
         * @param url the url loaded by the web view.
         */
        void handleUrl(String url);

        /**
         * Determines if the url passed is one that the oauth implementation can handle.
         * @param url the url loaded by the web view.
         */
        boolean canHandleUrl(String url);

    }

    /**
     * Handles login with Facebook.
     */
    protected static class FacebookAuth implements AuthInterface {

        @Override
        public String getToolbarText() {
            return "Login to Facebook";
        }

        @Override
        public String getLoginUrl() {
            return null;
        }

        @Override
        public void handleUrl(String url) {

        }

        @Override
        public boolean canHandleUrl(String url) {
            return false;
        }

    }

    /**
     * Handles login with Twitter.
     */
    protected static class TwitterAuth implements AuthInterface {

        @Override
        public String getToolbarText() {
            return "Login to Twitter";
        }

        @Override
        public String getLoginUrl() {
            return null;
        }

        @Override
        public void handleUrl(String url) {

        }

        @Override
        public boolean canHandleUrl(String url) {
            return false;
        }

    }

    /**
     * Handles login with LinkedIn.
     */
    protected static class LinkedInAuth implements AuthInterface {

        @Override
        public String getToolbarText() {
            return "Login to LinkedIn";
        }

        @Override
        public String getLoginUrl() {
            return null;
        }

        @Override
        public void handleUrl(String url) {

        }

        @Override
        public boolean canHandleUrl(String url) {
            return false;
        }

    }

}
