package com.example.ambassador.ambassadorsdk;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
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
    private Twitter twitter;
    private String oauth_secret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        _setUpToolbar();

        // UI Components
        wvTwitter = (WebView)findViewById(R.id.wvSocial);
        loader = (ProgressBar) findViewById(R.id.loadingPanel);

        wvTwitter.setWebViewClient(new CustomBrowser());

        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(AmbassadorSingleton.TWITTER_KEY, AmbassadorSingleton.TWITTER_SECRET);

        RequestManager.getInstance().twitterLoginRequest(new RequestManager.RequestCompletion() {
            @Override
            public void onSuccess(Object successResponse) {
                requestToken = (RequestToken)successResponse;
                String url = requestToken.getAuthenticationURL();
                wvTwitter.loadUrl(String.valueOf(Uri.parse(requestToken.getAuthenticationURL())));
            }

            @Override
            public void onFailure(Object failureResponse) {

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
            if (url.startsWith(AmbassadorSingleton.CALLBACK_URL)) {
                loader.setVisibility(View.VISIBLE);
                wvTwitter.setVisibility(View.INVISIBLE);
                oauth_secret = url.substring(url.indexOf("oauth_verifier=") + "oauth_verifier=".length(), url.length());
//                AccessTokenRequest atr = new AccessTokenRequest();
//                atr.execute();
                RequestManager.getInstance().twitterAccessTokenRequest(oauth_secret, requestToken, new RequestManager.RequestCompletion() {
                    @Override
                    public void onSuccess(Object successResponse) {
                        Toast.makeText(getApplicationContext(), "Successfully logged in!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Object failureResponse) {
                        Toast.makeText(getApplicationContext(), "Unable to login!", Toast.LENGTH_SHORT).show();
//                        finish();
                    }
                });
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
//    private class RequestTask extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                requestToken = twitter.getOAuthRequestToken("http://localhost:2999");
//            } catch (twitter4j.TwitterException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            if (requestToken != null) {
//                wvTwitter.loadUrl(String.valueOf(Uri.parse(requestToken.getAuthenticationURL())));
//            }
//        }
//    }

    // Async task that get Access token from OAuth credentials
//    private class AccessTokenRequest extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, oauth_secret);
//                AmbassadorSingleton.getInstance().setTwitterAccessToken(accessToken.getToken());
//                AmbassadorSingleton.getInstance().setTwitterAccessTokenSecret(accessToken.getTokenSecret());
//            } catch (twitter4j.TwitterException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            Toast.makeText(getApplicationContext(), "Successfully logged in!", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    }
}