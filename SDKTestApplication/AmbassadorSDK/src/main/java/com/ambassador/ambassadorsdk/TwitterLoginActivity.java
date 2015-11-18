package com.ambassador.ambassadorsdk;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import javax.inject.Inject;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

/**
 * Created by JakeDunahee on 7/29/15.
 */
public class TwitterLoginActivity extends AppCompatActivity {

    private WebView wvTwitter;
    private ProgressBar loader;
    private RequestToken requestToken;

    @Inject
    RequestManager requestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        if (!AmbassadorSingleton.isValid()) {
            finish();
            return;
        }

        AmbassadorSingleton.getComponent().inject(this);

        if (!Utilities.isConnected(this)) {
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        _setUpToolbar();

        // UI Components
        wvTwitter = (WebView)findViewById(R.id.wvSocial);
        loader = (ProgressBar) findViewById(R.id.loadingPanel);

        wvTwitter.setWebViewClient(new CustomBrowser());

        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(AmbassadorConfig.TWITTER_KEY, AmbassadorConfig.TWITTER_SECRET);

        requestManager.twitterLoginRequest(new RequestManager.RequestCompletion() {
            @Override
            public void onSuccess(Object successResponse) {
                requestToken = (RequestToken) successResponse;
                wvTwitter.loadUrl(String.valueOf(Uri.parse(requestToken.getAuthenticationURL())));
            }

            @Override
            public void onFailure(Object failureResponse) {
                Toast.makeText(TwitterLoginActivity.this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish(); // Dismisses activity if back button pressed
        return super.onOptionsItemSelected(item);
    }

    private void _setUpToolbar() {
       if (getSupportActionBar() != null) { getSupportActionBar().setTitle("Log in to Twitter"); }

       Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
       if (toolbar == null) return;

       final Drawable arrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
       arrow.setColorFilter(getResources().getColor(R.color.twitterToolBarArrow), PorterDuff.Mode.SRC_ATOP);
       toolbar.setNavigationIcon(arrow);

       toolbar.setBackgroundColor(getResources().getColor(R.color.twitterToolBar));
       toolbar.setTitleTextColor(getResources().getColor(R.color.twitterToolBarText));
    }

    private class CustomBrowser extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Checks for callback url to get the oAuth verifier string for Twitter login
            if (url.startsWith(AmbassadorConfig.CALLBACK_URL)) {
                loader.setVisibility(View.VISIBLE);
                wvTwitter.setVisibility(View.INVISIBLE);
                String oauth_secret = url.substring(url.indexOf("oauth_verifier=") + "oauth_verifier=".length(), url.length());
                requestManager.twitterAccessTokenRequest(oauth_secret, requestToken, new RequestManager.RequestCompletion() {
                    @Override
                    public void onSuccess(Object successResponse) {
                        loader.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Successfully logged in!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Object failureResponse) {
                        loader.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Unable to login!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            loader.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Toast.makeText(TwitterLoginActivity.this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            finish();
        }

    }
}