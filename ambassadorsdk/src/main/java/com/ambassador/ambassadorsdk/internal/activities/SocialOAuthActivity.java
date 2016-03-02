package com.ambassador.ambassadorsdk.internal.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;

import javax.inject.Inject;

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

    /** Object used to perform all requests. */
    @Inject protected RequestManager requestManager;

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
        AmbSingleton.inject(this);

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

        authInterface.getLoginUrl(new AuthInterface.LoginUrlListener() {
            @Override
            public void onLoginUrlReceived(@NonNull String url) {
                wvLogin.loadUrl(url);
            }

            @Override
            public void onLoginUrlFailed() {
                finish();
            }
        });
    }

    /**
     * Finish the Activity when options item is selected (this is the back arrow). No other MenuItems
     * exist or need to be considered.
     * @param item the MenuItem selected, this doesn't matter.
     * @return true to disallow further processing by super.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
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
     * Sets the toolbar color and title, and sets a back arrow in the top left. Sets colors based
     * on the AuthInterface set.
     */
    protected void setUpToolbar() {
        RAFOptions raf = RAFOptions.get();
        @ColorInt int toolbarArrowColor =
                (authInterface instanceof FacebookAuth) ? raf.getTwitterToolbarArrowColor() :
                        (authInterface instanceof TwitterAuth) ? raf.getTwitterToolbarArrowColor() :
                                (authInterface instanceof LinkedInAuth) ? raf.getLinkedInToolbarArrowColor() : -1;

        @ColorInt int toolbarColor =
                (authInterface instanceof FacebookAuth) ? raf.getTwitterToolbarColor() :
                        (authInterface instanceof TwitterAuth) ? raf.getTwitterToolbarColor() :
                                (authInterface instanceof LinkedInAuth) ? raf.getLinkedInToolbarColor() : -1;

        @ColorInt int toolbarTextColor =
                (authInterface instanceof FacebookAuth) ? raf.getTwitterToolbarTextColor() :
                        (authInterface instanceof TwitterAuth) ? raf.getTwitterToolbarTextColor() :
                                (authInterface instanceof LinkedInAuth) ? raf.getLinkedInToolbarTextColor() : -1;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(authInterface.getToolbarText());
        }

        Drawable arrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        arrow.setColorFilter(toolbarArrowColor, PorterDuff.Mode.SRC_ATOP);

        if (toolbar == null) return;

        toolbar.setNavigationIcon(arrow);

        toolbar.setBackgroundColor(toolbarColor);
        toolbar.setTitleTextColor(toolbarTextColor);
        Utilities.setStatusBar(getWindow(), toolbarColor);
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
                return authInterface.handleUrl(url);
            } else {
                view.stopLoading();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }

            return true;
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
         * @param loginUrlListener the callback interface to pass the url back through (can be async).
         */
        void getLoginUrl(@NonNull LoginUrlListener loginUrlListener);

        /**
         * Will handle the url, assuming it knows how to.
         * @param url the url loaded by the web view.
         */
        boolean handleUrl(@Nullable String url);

        /**
         * Determines if the url passed is one that the oauth implementation can handle.
         * @param url the url loaded by the web view.
         */
        boolean canHandleUrl(@Nullable String url);

        /**
         * Asynchronous callback interface for passing a login URL back.
         */
        interface LoginUrlListener {

            /**
             * Called only when a login url is successfully received and can be passed back.
             * @param url the url to load into the WebView, never null.
             */
            void onLoginUrlReceived(@NonNull String url);

            /**
             * Called if a login url cannot be fetched. This tells the whole login Activity to fail.
             */
            void onLoginUrlFailed();

        }

    }

    /**
     * Handles login with Facebook.
     */
    protected class FacebookAuth implements AuthInterface {

        @NonNull
        @Override
        public String getToolbarText() {
            return "Login to Facebook";
        }

        @Override
        public void getLoginUrl(@NonNull LoginUrlListener loginUrlListener) {
            loginUrlListener.onLoginUrlReceived("https://www.facebook.com/dialog/oauth?client_id=1527118794178815&redirect_uri=https://api.getenvoy.co/auth/facebook/auth");
        }

        @Override
        public boolean handleUrl(@Nullable String url) {
            return false;
        }

        @Override
        public boolean canHandleUrl(@Nullable String url) {
            return false;
        }

    }

    /**
     * Handles login with Twitter.
     */
    protected class TwitterAuth implements AuthInterface {

        @NonNull
        @Override
        public String getToolbarText() {
            return "Login to Twitter";
        }

        @Override
        public void getLoginUrl(@NonNull final LoginUrlListener loginUrlListener) {
            requestManager.getTwitterLoginUrl(new RequestManager.RequestCompletion() {
                @Override
                public void onSuccess(Object successResponse) {
                    final String url = (String) successResponse;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginUrlListener.onLoginUrlReceived(url);
                        }
                    });
                }

                @Override
                public void onFailure(Object failureResponse) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginUrlListener.onLoginUrlFailed();
                        }
                    });
                }
            });
        }

        @Override
        public boolean handleUrl(@Nullable String url) {
            return false;
        }

        @Override
        public boolean canHandleUrl(@Nullable String url) {
            return false;
        }

    }

    /**
     * Handles login with LinkedIn.
     */
    protected class LinkedInAuth implements AuthInterface {

        @NonNull
        @Override
        public String getToolbarText() {
            return "Login to LinkedIn";
        }

        @Override
        public void getLoginUrl(@NonNull LoginUrlListener loginUrlListener) {
            String authorizeUrl = "https://www.linkedin.com/uas/oauth2/authorization";
            String responseType = new StringResource(R.string.linked_in_login_response_type).getValue();
            String clientId = new StringResource(R.string.linked_in_client_id).getValue();
            String callbackUrl = new StringResource(R.string.linked_in_callback_url).getValue();
            String rProfilePermission = new StringResource(R.string.linked_in_r_profile_permission).getValue();
            String wSharePermission = new StringResource(R.string.linked_in_w_share_permission).getValue();
            String scopes = rProfilePermission + " " + wSharePermission;

            Uri authUri = Uri.parse(authorizeUrl)
                    .buildUpon()
                    .appendQueryParameter("response_type", responseType)
                    .appendQueryParameter("client_id", clientId)
                    .appendQueryParameter("redirect_uri", "REDIRECT")
                    .appendQueryParameter("state", "987654321")
                    .appendQueryParameter("scope", scopes)
                    .build();

            String authUrl = authUri.toString().replace("REDIRECT", callbackUrl);
            loginUrlListener.onLoginUrlReceived(authUrl);
        }

        @Override
        public boolean handleUrl(@Nullable String url) {
            return false;
        }

        @Override
        public boolean canHandleUrl(@Nullable String url) {
            return false;
        }

    }

}
