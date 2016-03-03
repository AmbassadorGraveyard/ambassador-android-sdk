package com.ambassador.ambassadorsdk.internal.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;
import com.ambassador.ambassadorsdk.internal.views.LoadingView;

import javax.inject.Inject;

import butterfork.Bind;
import butterfork.ButterFork;
import twitter4j.auth.AccessToken;

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

    /** The global Auth object that stores social access tokens. */
    @Inject protected Auth auth;

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
        WebSettings settings = wvLogin.getSettings();
        settings.setAllowFileAccess(false);
        settings.setJavaScriptEnabled(false);
        settings.setSaveFormData(false);
        settings.setSavePassword(false);

        wvLogin.setVerticalScrollBarEnabled(false);
        wvLogin.setHorizontalScrollBarEnabled(true);
        wvLogin.setWebViewClient(new OAuthWebClient());
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
        public int getThemeColor() {
            return ContextCompat.getColor(SocialOAuthActivity.this, R.color.facebook_blue);
        }

        @Override
        public void getLoginUrl(@NonNull final LoginUrlListener loginUrlListener) {
            //loginUrlListener.onLoginUrlReceived("https://www.facebook.com/dialog/oauth?client_id=1527118794178815&scope=publish_actions&redirect_uri=https://api.getenvoy.co/auth/facebook/auth");
            requestManager.getFacebookLoginUrl(new RequestManager.RequestCompletion() {
                @Override
                public void onSuccess(final Object successResponse) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loginUrlListener.onLoginUrlReceived((String) successResponse);
                        }
                    });
                }

                @Override
                public void onFailure(final Object failureResponse) {
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
            Uri uri = Uri.parse(url);

            if (isLoginRedirect(uri)) {
                return false;
            } else if (isSuccessUrl(uri)) {
                wvLogin.stopLoading();
            } else if (isSuccessRedirectUrl(uri)) {
                wvLogin.stopLoading();
                requestAccessToken(uri);
            } else if (isFailureUrl(uri)) {
                wvLogin.stopLoading();
                Toast.makeText(SocialOAuthActivity.this, "Incorrect Username/Password!", Toast.LENGTH_SHORT).show();
            }

            return false;
        }

        @Override
        public boolean canHandleUrl(@Nullable String url) {
            Uri uri = Uri.parse(url);
            return isLoginRedirect(uri) || isSuccessUrl(uri) || isSuccessRedirectUrl(uri) || isFailureUrl(uri);
        }

        protected boolean isLoginRedirect(Uri uri) {
            return (uri.getHost().equals("m.facebook.com")) && (uri.getPath().equals("/v2.0/dialog/oauth") || uri.getPath().equals("/login.php"));
        }

        protected boolean isSuccessUrl(Uri uri) {
            return uri.getHost().equals("m.facebook.com") && uri.getPath().equals("/v2.0/dialog/oauth") && uri.getQueryParameter("redirect_uri") != null;
        }

        protected boolean isSuccessRedirectUrl(Uri uri) {
            return uri.getHost().equals("api.getenvoy.co") && uri.getPath().equals("/auth/facebook/auth") && uri.getQueryParameter("code") != null;
        }

        protected boolean isFailureUrl(Uri uri) {
            return uri.getHost().equals("m.facebook.com") && uri.getPath().equals("/login/") && uri.getQueryParameter("api_key") != null && uri.getQueryParameter("auth_token") != null;
        }

        protected void requestAccessToken(Uri uri) {
            String code = uri.getQueryParameter("code");
            if (code == null) {
                Toast.makeText(SocialOAuthActivity.this, new StringResource(R.string.login_failure).toString(), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                requestManager.getFacebookAccessToken(code, new RequestManager.RequestCompletion() {
                    @Override
                    public void onSuccess(final Object successResponse) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String accessToken = (String) successResponse;
                                if (accessToken == null) {
                                    onFailure(null);
                                } else {
                                    auth.setFacebookToken(accessToken);
                                    Toast.makeText(SocialOAuthActivity.this, new StringResource(R.string.login_success).toString(), Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(final Object failureResponse) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), new StringResource(R.string.login_failure).getValue(), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
            }

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
        public int getThemeColor() {
            return ContextCompat.getColor(SocialOAuthActivity.this, R.color.twitter_blue);
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
            Uri uri = Uri.parse(url);

            if (isSuccessUrl(uri)) {
                wvLogin.stopLoading();
                requestAccessToken(uri);
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
            return isSuccessUrl(uri) || isCancelUrl(uri) || isFailureUrl(uri);
        }

        protected boolean isSuccessUrl(Uri uri) {
            return uri.getHost().equals("google.com") && uri.getQueryParameter("oauth_token") != null && uri.getQueryParameter("oauth_verifier") != null;
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
            String oauthVerifier = uri.getQueryParameter("oauth_verifier");

            requestManager.getTwitterAccessToken(oauthVerifier, new RequestManager.RequestCompletion() {
                @Override
                public void onSuccess(final Object successResponse) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (successResponse instanceof AccessToken) {
                                AccessToken accessToken = (AccessToken) successResponse;
                                auth.setTwitterToken(accessToken.getToken());
                                auth.setTwitterSecret(accessToken.getTokenSecret());
                                Toast.makeText(getApplicationContext(), new StringResource(R.string.login_success).getValue(), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                onFailure(null);
                            }
                        }
                    });
                }

                @Override
                public void onFailure(Object failureResponse) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), new StringResource(R.string.login_failure).getValue(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            });
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
        public int getThemeColor() {
            return ContextCompat.getColor(SocialOAuthActivity.this, R.color.linkedin_blue);
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
            Uri uri = Uri.parse(url);

            if (isSuccessUrl(uri)) {
                wvLogin.stopLoading();
                requestAccessToken(uri);
            } else if (isCancelUrl(uri)) {
                wvLogin.stopLoading();
                finish();
            }

            return false;
        }

        @Override
        public boolean canHandleUrl(@Nullable String url) {
            Uri uri = Uri.parse(url);
            return isSuccessUrl(uri) || isCancelUrl(uri);
        }

        protected boolean isSuccessUrl(Uri uri) {
            return uri.getHost().equals("getambassador.com") && uri.getQueryParameter("code") != null;
        }

        protected boolean isCancelUrl(Uri uri) {
            return uri.getHost().equals("getambassador.com") && uri.getQueryParameter("error") != null;
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

            requestManager.linkedInLoginRequest(requestToken, new RequestManager.RequestCompletion() {
                @Override
                public void onSuccess(Object successResponse) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), new StringResource(R.string.login_success).getValue(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }

                @Override
                public void onFailure(Object failureResponse) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), new StringResource(R.string.login_failure).getValue(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            });
        }

    }

}
