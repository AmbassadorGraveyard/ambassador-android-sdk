package com.example.ambassador.ambassadorsdk;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by JakeDunahee on 7/29/15.
 */

public class TwitterLoginActivity extends AppCompatActivity {
    private WebView wvTwitter;
    private ProgressBar loader;
    private RequestToken requestToken;
    private AccessToken accessToken;
    private Twitter twitter;
    private String oauth_secret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_login);

        setUpToolbar();

        // UI Components
        wvTwitter = (WebView)findViewById(R.id.wvSocial);
        loader = (ProgressBar) findViewById(R.id.pbLoader);

        wvTwitter.setWebViewClient(new CustomBrowser());

        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(AmbassadorSingleton.TWITTER_KEY, AmbassadorSingleton.TWITTER_SECRET);

        RequestTask task = new RequestTask();
        task.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish(); // Dismisses activity if back button pressed
        return super.onOptionsItemSelected(item);
    }

    void setUpToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.action_bar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setBackgroundColor(Color.parseColor("#62a9ef"));
        toolbar.setTitleTextColor(Color.WHITE);

        if (getSupportActionBar() != null) { getSupportActionBar().setTitle("Log into Twitter"); }
    }

    private class CustomBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Checks for callback url to get the oAuth verifier string for Twitter login
            if (url.startsWith(AmbassadorSingleton.LINKED_IN_CALLBACK_URL)) {
                loader.setVisibility(View.VISIBLE);
                wvTwitter.setVisibility(View.INVISIBLE);
                oauth_secret = url.substring(url.indexOf("oauth_verifier=") + "oauth_verifier=".length(), url.length());
                AccessTokenRequest atr = new AccessTokenRequest();
                atr.execute();
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (url.startsWith("https://api.twitter.com/oauth")) {
                loader.setVisibility(View.INVISIBLE);
            }
        }
    }

    // Async Task that get OAuth token
    class RequestTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                requestToken = twitter.getOAuthRequestToken("http://localhost:2999");
            } catch (twitter4j.TwitterException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            wvTwitter.loadUrl(String.valueOf(Uri.parse(requestToken.getAuthenticationURL())));
        }
    }

    // Async task that get Access token from OAuth credentials
    class AccessTokenRequest extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                accessToken = twitter.getOAuthAccessToken(requestToken, oauth_secret);
                AmbassadorSingleton.getInstance().setTwitterAccessToken(accessToken.getToken());
                AmbassadorSingleton.getInstance().setTwitterAccessTokenSecret(accessToken.getTokenSecret());
            } catch (twitter4j.TwitterException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Successfully logged in!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}