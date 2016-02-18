package com.ambassador.ambassadorsdk.internal.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.dialogs.WebPopupDialog;
import com.ambassador.ambassadorsdk.internal.utils.Device;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterfork.Bind;
import butterfork.ButterFork;
import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterMethod;
import twitter4j.auth.RequestToken;

/**
 * Activity that handles OAuth authentication with Twitter.
 * Presents a WebView prompting the user to login, and handles getting
 * the access token and secret stored in AmbassadorConfig.
 */
public class TwitterLoginActivity extends AppCompatActivity {

    @Bind(B.id.action_bar)  protected Toolbar       toolbar;
    @Bind(B.id.wvLogin)     protected WebView       wvLogin;
    @Bind(B.id.pbLoading)   protected ProgressBar   loader;

    @Inject protected Device device;
    @Inject protected RAFOptions raf;

    protected RequestToken requestToken;
    protected String authUrl;
    protected WebPopupDialog popupDialog;

    // region Methods

    // region Activity overrides
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // Injection
        AmbSingleton.inject(this);
        ButterFork.bind(this);

        // Requirement checks
        finishIfSingletonInvalid();
        finishIfDeviceNotConnected();
        if (isFinishing()) return;

        // Other setup
        setUpToolbar();
        configureWebView();
        generateAuthUrl();
        loadLoginPage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupDialog != null && popupDialog.isShowing()) {
            popupDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
    // endregion

    // region Requirement checks
    private void finishIfSingletonInvalid() {
        if (!AmbSingleton.isValid()) {
            finish();
        }
    }

    private void finishIfDeviceNotConnected() {
        if (!device.isConnected()) {
            Toast.makeText(this, new StringResource(R.string.connection_failure).getValue(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    // endregion

    // region Layout configuration
    private void setUpToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Login to Twitter");
        }

        Drawable arrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        arrow.setColorFilter(raf.getTwitterToolbarArrowColor(), PorterDuff.Mode.SRC_ATOP);

        if (toolbar == null) return;

        toolbar.setNavigationIcon(arrow);
        toolbar.setBackgroundColor(raf.getTwitterToolbarColor());
        toolbar.setTitleTextColor(raf.getTwitterToolbarTextColor());

        Utilities.setStatusBar(getWindow(), raf.getTwitterToolbarColor());
    }
    // endregion

    // region Web configuration
    private void configureWebView() {
        WebSettings settings = wvLogin.getSettings();
        settings.setAllowFileAccess(false);
        settings.setJavaScriptEnabled(false);
        settings.setSaveFormData(false);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        wvLogin.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        wvLogin.setVerticalScrollBarEnabled(false);
        wvLogin.setHorizontalScrollBarEnabled(true);
        wvLogin.setWebViewClient(new TwitterClient());
    }

    private void generateAuthUrl() {
        authUrl = getIntent().getStringExtra("url");
    }

    private void loadLoginPage() {
        AsyncTwitter twitter = new AsyncTwitterFactory().getInstance();
        String twitterConsumerKey = new StringResource(R.string.twitter_consumer_key).getValue();
        String twitterConsumerSecret = new StringResource(R.string.twitter_consumer_secret).getValue();
        twitter.setOAuthConsumer(twitterConsumerKey, twitterConsumerSecret);

        twitter.addListener(new TwitterAdapter() {

            @Override
            public void gotOAuthRequestToken(RequestToken token) {
                super.gotOAuthRequestToken(token);
                requestToken = token;
                authUrl = token.getAuthenticationURL();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wvLogin.loadUrl(authUrl);
                    }
                });
            }

            @Override
            public void onException(TwitterException te, TwitterMethod method) {
                super.onException(te, method);
            }

        });

        twitter.getOAuthRequestTokenAsync();

    }
    // endregion

    // endregion

    // region Classes
    /**
     * WebViewClient extension that handles redirects, login finishing, and opening un-handled
     * links inside of a WebPopupDialog.
     */
    private final class TwitterClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!isHandled(url) && !isPopupOpen()) {
                Uri uri = Uri.parse(url);
                List<String> toOpenInBrowser = new ArrayList<>();
                toOpenInBrowser.add("/settings/applications");
                toOpenInBrowser.add("/signup");
                toOpenInBrowser.add("/account/resend_password");
                if (uri != null && toOpenInBrowser.contains(uri.getPath())) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(uri);
                    startActivity(i);
                    view.stopLoading();
                    return false;
                }

                if (uri != null && uri.getPath().equals("/login/error")) {
                    view.stopLoading();
                    Toast.makeText(TwitterLoginActivity.this, "Incorrect Username/Password!", Toast.LENGTH_SHORT).show();
                    return false;
                }

                view.stopLoading();
                popup(url);
            }

            return false;
        }

        @Override
        public void onPageFinished(final WebView view, String url) {
            super.onPageFinished(view, url);
            if (url.equals(authUrl)) {
                loader.setVisibility(View.GONE);
            } else if (url.contains("google.com")) {
                Uri uri = Uri.parse(url);
                String token = uri.getQueryParameter("oauth_token");
                String verifier = uri.getQueryParameter("oauth_verifier");
                if (token != null && verifier != null) {
                    Intent data = new Intent();
                    data.putExtra("token", token);
                    data.putExtra("verifier", verifier);
                    data.putExtra("request", requestToken);
                    setResult(5, data);
                }

                finish();
            }
        }

        private boolean isHandled(String url) {
            Uri uri = Uri.parse(url);
            return uri != null && uri.getHost().equals("google.com");
        }

        private void popup(String url) {
            popupDialog = new WebPopupDialog(TwitterLoginActivity.this);
            popupDialog.setOwnerActivity(TwitterLoginActivity.this);
            popupDialog.load(url);
            popupDialog.setCanceledOnTouchOutside(true);
            popupDialog.setCancelable(true);
            popupDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    popupDialog = null;
                }
            });
            popupDialog.show();
        }

        private boolean isPopupOpen() {
            return popupDialog != null && popupDialog.isShowing();
        }

    }
    // endregion

}
