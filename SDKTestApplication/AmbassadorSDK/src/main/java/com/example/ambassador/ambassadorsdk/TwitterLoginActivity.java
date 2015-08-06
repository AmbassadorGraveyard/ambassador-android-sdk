package com.example.ambassador.ambassadorsdk;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.internal.oauth.OAuth1aHeaders;
import com.twitter.sdk.android.core.internal.oauth.OAuth2Token;
import com.twitter.sdk.android.tweetui.TweetUi;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import io.fabric.sdk.android.Fabric;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Created by JakeDunahee on 7/29/15.
 */


// NOT USED YET!
public class TwitterLoginActivity extends ActionBarActivity {
    private WebView wvTwitter;
    private TwitterLoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter_login);

//        wvTwitter = (WebView)findViewById(R.id.wvSocial);
//        wvTwitter.getSettings().setJavaScriptEnabled(true);
//        wvTwitter.setWebViewClient(new CustomBrowser());
//        wvTwitter.loadUrl("https://api.twitter.com/oauth/request_token");


        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = result.data;
                AmbassadorSingleton.getInstance().setTwitterToken(session.getAuthToken().token, getApplicationContext());
                AmbassadorSingleton.getInstance().setTwitterTokenSecret(session.getAuthToken().secret, getApplicationContext());
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("test", "failure");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    private class CustomBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
