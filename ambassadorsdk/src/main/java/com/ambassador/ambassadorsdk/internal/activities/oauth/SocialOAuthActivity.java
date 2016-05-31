package com.ambassador.ambassadorsdk.internal.activities.oauth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.BuildConfig;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.envoy.EnvoyApi;
import com.ambassador.ambassadorsdk.internal.api.identify.IdentifyApi;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;
import com.ambassador.ambassadorsdk.internal.views.LoadingView;

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

    /** LoadingView to display between pages loading */
    @Bind(B.id.lvLoading) protected LoadingView lvLoading;

    /** Object used to perform all requests. */
    @Inject protected RequestManager requestManager;

    /** The global Auth object that stores envoy tokens. */
    @Inject protected Auth auth;

    /** The global User object that stores social access tokens. */
    @Inject protected User user;

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
        configureWebView();

        if (auth.getEnvoyId() == null || auth.getEnvoySecret() == null) {
            getEnvoyKeys();
            return;
        }

        executionReady();
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
     * Call this method when all details are retrieved (envoy keys). Starts off the process by grabbing
     * an OAuth url via the AuthInterface.
     */
    protected void executionReady() {
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
     * Attempts to request envoy keys from the Ambassador backend and set them in auth.
     */
    protected void getEnvoyKeys() {
        requestManager.getCompanyInfo(new RequestManager.RequestCompletion() {
            @Override
            public void onSuccess(Object successResponse) {
                if (!(successResponse instanceof IdentifyApi.GetCompanyInfoResponse)) {
                    onFailure(null);
                    return;
                }

                IdentifyApi.GetCompanyInfoResponse response = (IdentifyApi.GetCompanyInfoResponse) successResponse;

                if (response.results == null || response.results.length == 0 || response.results[0] == null) {
                    onFailure(null);
                    return;
                }

                IdentifyApi.GetCompanyInfoResponse.Result result = response.results[0];
                String uid = result.uid;

                requestManager.getEnvoyKeys(uid, new RequestManager.RequestCompletion() {
                    @Override
                    public void onSuccess(Object successResponse) {
                        if (!(successResponse instanceof IdentifyApi.GetEnvoyKeysResponse)) {
                            onFailure(null);
                            return;
                        }

                        IdentifyApi.GetEnvoyKeysResponse envoyKeysResponse = (IdentifyApi.GetEnvoyKeysResponse) successResponse;
                        if (envoyKeysResponse.envoy_client_id == null || envoyKeysResponse.envoy_client_secret == null) {
                            onFailure(null);
                            return;
                        }

                        auth.setEnvoyId(envoyKeysResponse.envoy_client_id);
                        auth.setEnvoySecret(envoyKeysResponse.envoy_client_secret);

                        executionReady();
                    }

                    @Override
                    public void onFailure(Object failureResponse) {
                        finish();
                        return;
                    }
                });
            }

            @Override
            public void onFailure(Object failureResponse) {
                finish();
                return;
            }
        });
    }

    /**
     * Returns a new unique popup code to append to oauth url.
     * @return the unique String popup code.
     */
    protected String getPopupCode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(auth.getEnvoyId());
        stringBuilder.append(System.currentTimeMillis());
        for (int i = 0; i < 32; i++) {
            stringBuilder.append((int)(Math.random() * 10));
        }
        return stringBuilder.toString();
    }

    /**
     * Sets the toolbar color and title, and sets a back arrow in the top left. Sets colors based
     * on the AuthInterface set.
     */
    protected void setUpToolbar() {
        @ColorInt int themeColor = authInterface.getThemeColor();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(authInterface.getToolbarText());
        }

        Drawable arrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        arrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        if (toolbar == null) return;

        toolbar.setNavigationIcon(arrow);

        toolbar.setBackgroundColor(themeColor);
        toolbar.setTitleTextColor(Color.WHITE);
        Utilities.setStatusBar(getWindow(), themeColor);
    }

    /**
     * Sets the WebViewClient on the WebView and performs other performance optimizations.
     */
    protected void configureWebView() {
        wvLogin.setVerticalScrollBarEnabled(false);
        wvLogin.setHorizontalScrollBarEnabled(true);
        wvLogin.setWebViewClient(new OAuthWebClient());
    }


    /**
     * WebViewClient extension to allow AuthInterface implementations to handle URL changes.
     */
    protected class OAuthWebClient extends WebViewClient {

        protected boolean disappearedLoadingScreen;

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
            authInterface.onPageFinished(url);

            if (!disappearedLoadingScreen) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(lvLoading, "alpha", 1, 0);
                animator.setDuration(300);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        lvLoading.setVisibility(View.GONE);
                    }
                });
                animator.start();
            }

            disappearedLoadingScreen = true;
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
         * Will return the AuthInterface implementation's theme color to set on the toolbar.
         * @return an int representing a color to set on the toolbar.
         */
        @ColorRes
        int getThemeColor();

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
         * Should be called by a WebView's onPageFinished(...) method without any checks.
         * @param url the url finished loading by the web view.
         */
        void onPageFinished(@Nullable String url);

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

        protected String popup;
        protected boolean handleFinish;

        @NonNull
        @Override
        public String getToolbarText() {
            return "Login to Facebook";
        }

        @Override
        public int getThemeColor() {
            return ContextCompat.getColor(SocialOAuthActivity.this, R.color.facebook_blue);
        }

        @Override
        public void getLoginUrl(@NonNull final LoginUrlListener loginUrlListener) {
            String host = (BuildConfig.IS_RELEASE_BUILD) ? new StringResource(R.string.envoy_api_url).getValue() : new StringResource(R.string.envoy_api_url_dev).getValue();
            String frameworkUrl = host + "/oauth/authenticate/?client_id={id}&client_secret={secret}&provider={provider}&popup={popup}";
            this.popup = getPopupCode();
            String actualUrl =
                    frameworkUrl
                        .replace("{id}", auth.getEnvoyId())
                        .replace("{secret}", auth.getEnvoySecret())
                        .replace("{provider}", "facebook")
                        .replace("{popup}", popup);
            loginUrlListener.onLoginUrlReceived(actualUrl);
        }

        @Override
        public boolean handleUrl(@Nullable String url) {
            Uri uri = Uri.parse(url);

            if (isAllowedRedirectUrl(uri)) {
                return false;
            } else if (isLoginRedirect(uri)) {
                return false;
            } else if (isSuccessUrl(uri)) {
                wvLogin.stopLoading();
            } else if (isSuccessRedirectUrl(uri)) {
                lvLoading.setVisibility(View.VISIBLE);
                ObjectAnimator animator = ObjectAnimator.ofFloat(lvLoading, "alpha", 0, 1);
                animator.setDuration(150);
                animator.start();
                handleFinish = true;
            } else if (isFailureUrl(uri)) {
                wvLogin.stopLoading();
                Toast.makeText(SocialOAuthActivity.this, "Incorrect Username/Password!", Toast.LENGTH_SHORT).show();
            } else if (isCancelUrl(uri)) {
                finish();
            }

            return false;
        }

        @Override
        public boolean canHandleUrl(@Nullable String url) {
            Uri uri = Uri.parse(url);
            return isAllowedRedirectUrl(uri) || isLoginRedirect(uri) || isSuccessUrl(uri) || isSuccessRedirectUrl(uri) || isFailureUrl(uri) || isCancelUrl(uri);
        }

        @Override
        public void onPageFinished(@Nullable String url) {
            if (handleFinish) {
                requestAccessToken(Uri.parse(url));
            }
            handleFinish = false;
        }

        protected boolean isAllowedRedirectUrl(Uri uri) {
            boolean hostCheck = uri.getHost().equals("www.facebook.com") || uri.getHost().equals("facebook.com");
            boolean pathCheck = uri.getPath().equals("/dialog/oauth") || uri.getPath().equals("/dialog/oauth/");
            return hostCheck || pathCheck;
        }

        protected boolean isLoginRedirect(Uri uri) {
            return (uri.getHost().equals("m.facebook.com")) && (uri.getPath().equals("/v2.0/dialog/oauth") || uri.getPath().equals("/login.php"));
        }

        protected boolean isSuccessUrl(Uri uri) {
            return uri.getHost().equals("m.facebook.com") && uri.getPath().equals("/v2.0/dialog/oauth") && uri.getQueryParameter("redirect_uri") != null;
        }

        protected boolean isSuccessRedirectUrl(Uri uri) {
            boolean hostCheck = uri.getHost().equals("api.getenvoy.co") || uri.getHost().equals("dev-envoy-api.herokuapp.com");
            return hostCheck && uri.getPath().equals("/auth/facebook/auth") && uri.getQueryParameter("code") != null;
        }

        protected boolean isCancelUrl(Uri uri) {
            boolean hostCheck = uri.getHost().equals("api.getenvoy.co") || uri.getHost().equals("dev-envoy-api.herokuapp.com");
            return hostCheck && uri.getQueryParameter("error") != null;
        }

        protected boolean isFailureUrl(Uri uri) {
            return uri.getHost().equals("m.facebook.com") && uri.getPath().equals("/login/") && uri.getQueryParameter("api_key") != null && uri.getQueryParameter("auth_token") != null;
        }

        protected void requestAccessToken(Uri uri) {
            requestManager.getEnvoyAccessToken(popup, new RequestManager.RequestCompletion() {
                @Override
                public void onSuccess(Object successResponse) {
                    if (!(successResponse instanceof EnvoyApi.GetAccessTokenResponse)) {
                        finish();
                        return;
                    }

                    EnvoyApi.GetAccessTokenResponse response = (EnvoyApi.GetAccessTokenResponse) successResponse;
                    user.setFacebookAccessToken(response.access_token);
                    finish();
                }

                @Override
                public void onFailure(Object failureResponse) {
                    finish();
                }
            });
        }

    }

    /**
     * Handles login with Twitter.
     */
    protected class TwitterAuth implements AuthInterface {

        protected String popup;
        protected boolean handleFinish;

        @NonNull
        @Override
        public String getToolbarText() {
            return "Login to Twitter";
        }

        @Override
        public int getThemeColor() {
            return ContextCompat.getColor(SocialOAuthActivity.this, R.color.twitter_blue);
        }

        @Override
        public void getLoginUrl(@NonNull final LoginUrlListener loginUrlListener) {
            String host = (BuildConfig.IS_RELEASE_BUILD) ? new StringResource(R.string.envoy_api_url).getValue() : new StringResource(R.string.envoy_api_url_dev).getValue();
            String frameworkUrl = host + "/oauth/authenticate/?client_id={id}&client_secret={secret}&provider={provider}&popup={popup}";
            this.popup = getPopupCode();
            String actualUrl =
                    frameworkUrl
                            .replace("{id}", auth.getEnvoyId())
                            .replace("{secret}", auth.getEnvoySecret())
                            .replace("{provider}", "twitter")
                            .replace("{popup}", popup);
            loginUrlListener.onLoginUrlReceived(actualUrl);
        }

        @Override
        public boolean handleUrl(@Nullable String url) {
            Uri uri = Uri.parse(url);

            if (isAllowedRedirectUrl(uri)) {
                return false;
            } else if (isSuccessUrl(uri)) {
                lvLoading.setVisibility(View.VISIBLE);
                ObjectAnimator animator = ObjectAnimator.ofFloat(lvLoading, "alpha", 0, 1);
                animator.setDuration(150);
                animator.start();
                handleFinish = true;
            } else if (isCancelUrl(uri)) {
                wvLogin.stopLoading();
                finish();
            } else if (isFailureUrl(uri)) {
                wvLogin.stopLoading();
                Toast.makeText(SocialOAuthActivity.this, "Incorrect Username/Password!", Toast.LENGTH_SHORT).show();
            }

            return false;
        }

        @Override
        public boolean canHandleUrl(@Nullable String url) {
            Uri uri = Uri.parse(url);
            return isAllowedRedirectUrl(uri) || isSuccessUrl(uri) || isCancelUrl(uri) || isFailureUrl(uri);
        }

        @Override
        public void onPageFinished(@Nullable String url) {
            if (handleFinish) {
                requestAccessToken(Uri.parse(url));
            }
            handleFinish = false;
        }

        protected boolean isAllowedRedirectUrl(Uri uri) {
            boolean hostCheck = uri.getHost().equals("api.twitter.com");
            boolean pathCheck = uri.getPath().equals("/oauth/authorize") || uri.getPath().equals("/oauth/authorize/");
            return hostCheck && pathCheck;
        }

        protected boolean isSuccessUrl(Uri uri) {
            boolean hostCheck = uri.getHost().equals("api.getenvoy.co") || uri.getHost().equals("dev-envoy-api.herokuapp.com");
            return hostCheck && uri.getQueryParameter("oauth_token") != null && uri.getQueryParameter("oauth_verifier") != null;
        }

        protected boolean isCancelUrl(Uri uri) {
            return uri.getQueryParameter("denied") != null;
        }

        protected boolean isFailureUrl(Uri uri) {
            return uri.getPath().equals("/login/error");
        }

        /**
         * Takes the redirected URL with an oauth_token and oauth_verifier query param attached, extracts
         * the values, and uses them to request an access token from the Twitter API, then stores it.
         * When completion is called the Activity finishes.
         * @param uri the URL overridden from the WebView client, this will be a url with ?oauth_token=x&oauth_verifier=x attached.
         */
        protected void requestAccessToken(Uri uri) {
            requestManager.getEnvoyAccessToken(popup, new RequestManager.RequestCompletion() {
                @Override
                public void onSuccess(Object successResponse) {
                    if (!(successResponse instanceof EnvoyApi.GetAccessTokenResponse)) {
                        finish();
                        return;
                    }

                    EnvoyApi.GetAccessTokenResponse response = (EnvoyApi.GetAccessTokenResponse) successResponse;
                    user.setTwitterAccessToken(response.access_token);
                    finish();
                }

                @Override
                public void onFailure(Object failureResponse) {
                    finish();
                }
            });
        }

    }

    /**
     * Handles login with LinkedIn.
     */
    protected class LinkedInAuth implements AuthInterface {

        protected String popup;
        protected boolean handleFinish;

        @NonNull
        @Override
        public String getToolbarText() {
            return "Login to LinkedIn";
        }

        @Override
        public int getThemeColor() {
            return ContextCompat.getColor(SocialOAuthActivity.this, R.color.linkedin_blue);
        }

        @Override
        public void getLoginUrl(@NonNull LoginUrlListener loginUrlListener) {
            String host = (BuildConfig.IS_RELEASE_BUILD) ? new StringResource(R.string.envoy_api_url).getValue() : new StringResource(R.string.envoy_api_url_dev).getValue();
            String frameworkUrl = host + "/oauth/authenticate/?client_id={id}&client_secret={secret}&provider={provider}&popup={popup}";
            this.popup = getPopupCode();
            String actualUrl =
                    frameworkUrl
                            .replace("{id}", auth.getEnvoyId())
                            .replace("{secret}", auth.getEnvoySecret())
                            .replace("{provider}", "linkedin")
                            .replace("{popup}", popup);
            loginUrlListener.onLoginUrlReceived(actualUrl);
        }

        @Override
        public boolean handleUrl(@Nullable String url) {
            Uri uri = Uri.parse(url);

            if (isAllowedRedirectUrl(uri)) {
                return false;
            } else if (isSuccessUrl(uri)) {
                lvLoading.setVisibility(View.VISIBLE);
                ObjectAnimator animator = ObjectAnimator.ofFloat(lvLoading, "alpha", 0, 1);
                animator.setDuration(150);
                animator.start();
                handleFinish = true;
            } else if (isCancelUrl(uri)) {
                wvLogin.stopLoading();
                finish();
            }

            return false;
        }

        @Override
        public boolean canHandleUrl(@Nullable String url) {
            Uri uri = Uri.parse(url);
            return isAllowedRedirectUrl(uri) || isSuccessUrl(uri) || isCancelUrl(uri);
        }

        @Override
        public void onPageFinished(@Nullable String url) {
            if (handleFinish) {
                requestAccessToken(Uri.parse(url));
            }

            handleFinish = false;
        }

        protected boolean isAllowedRedirectUrl(Uri uri) {
            boolean hostCheck = uri.getHost().equals("linkedin.com") || uri.getHost().equals("www.linkedin.com");
            boolean pathCheck = uri.getPath().equals("/uas/oauth2/authorization") || uri.getPath().equals("/uas/oauth2/authorization/");
            return hostCheck || pathCheck;
        }

        protected boolean isSuccessUrl(Uri uri) {
            boolean hostCheck = uri.getHost().equals("api.getenvoy.co") || uri.getHost().equals("dev-envoy-api.herokuapp.com");
            return hostCheck && uri.getQueryParameter("code") != null;
        }

        protected boolean isCancelUrl(Uri uri) {
            boolean hostCheck = uri.getHost().equals("api.getenvoy.co") || uri.getHost().equals("dev-envoy-api.herokuapp.com");
            return hostCheck && uri.getQueryParameter("error") != null;
        }

        /**
         * Takes the redirected URL with a code query param attached, extracts the code, and uses it
         * to request an access token from the LinkedIn API, then stores it. When completion is called
         * the Activity finishes.
         * @param uri the URL overridden from the WebView client, this will be a url with ?code= attached.
         */
        protected void requestAccessToken(Uri uri) {
            String requestToken = uri.getQueryParameter("code");
            if (requestToken == null) {
                finish();
                return;
            }

            requestManager.getEnvoyAccessToken(popup, new RequestManager.RequestCompletion() {
                @Override
                public void onSuccess(Object successResponse) {
                    if (!(successResponse instanceof EnvoyApi.GetAccessTokenResponse)) {
                        finish();
                        return;
                    }

                    EnvoyApi.GetAccessTokenResponse response = (EnvoyApi.GetAccessTokenResponse) successResponse;
                    user.setLinkedInAccessToken(response.access_token);
                    finish();
                }

                @Override
                public void onFailure(Object failureResponse) {
                    finish();
                }
            });
        }

    }

    public static void clearCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else if (AmbSingleton.getContext() != null) {
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(AmbSingleton.getContext());
            cookieSyncManager.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncManager.stopSync();
            cookieSyncManager.sync();
        }
    }

}
