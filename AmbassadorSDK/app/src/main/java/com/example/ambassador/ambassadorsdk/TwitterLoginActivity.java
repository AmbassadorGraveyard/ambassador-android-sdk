package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by JakeDunahee on 7/29/15.
 */


// NOT USED YET!
public class TwitterLoginActivity extends ActionBarActivity {
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

        wvTwitter = (WebView)findViewById(R.id.wvSocial);
        loader = (ProgressBar) findViewById(R.id.pbLoader);

        wvTwitter.getSettings().setJavaScriptEnabled(true);
        wvTwitter.setWebViewClient(new CustomBrowser());

        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(AmbassadorSingleton.TWITTER_KEY, AmbassadorSingleton.TWITTER_SECRET);

        RequestTask task = new RequestTask();
        task.execute();
    }

    private class CustomBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
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