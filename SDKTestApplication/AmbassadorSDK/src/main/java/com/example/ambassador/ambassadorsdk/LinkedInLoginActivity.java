package com.example.ambassador.ambassadorsdk;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by JakeDunahee on 7/27/15.
 */
public class LinkedInLoginActivity extends ActionBarActivity {
    WebView webView;
    ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // UI Components
        loader = (ProgressBar)findViewById(R.id.loadingPanel);
        webView = (WebView)findViewById(R.id.wvSocial);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyBrowser());
        webView.loadUrl("https://www.linkedin.com/uas/oauth2/authorization?" +
                "response_type=code&client_id=777z4czm3edaef&redirect_uri=http://localhost:2999&state=987654321&scope=r_basicprofile%20w_share");
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Breaks up url to grab seperate components
            String[] urlArray = url.split("\\?");
            String callbackURL = urlArray[0];
            String codeErrorString = urlArray[1];

            // Checks for url to get code for getting Access Token
            if (callbackURL.startsWith(AmbassadorSingleton.LINKED_IN_CALLBACK_URL) && codeErrorString.startsWith("code")) {
                String code;
                if (url.contains("&")) {
                    code = url.substring(url.indexOf("code=") + "code=".length(), url.indexOf("&"));
                } else {
                    code = url.substring(url.indexOf("code=") + "code=".length(), url.length() - 1);
                }

                // Request task makes call to get OAuth LinkedIn token
                RequestTask requestTask = new RequestTask();
                requestTask.currentCode = code;
                requestTask.context = getApplicationContext();
                requestTask.execute();

                finish();
            }

            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            loader.setVisibility(View.GONE);
        }
    }


    class RequestTask extends AsyncTask<Void, Void, Void> {
        public String currentCode;
        public Context context;
        public String charset = "UTF-8";
        public int statusCode;

        @Override
        protected Void doInBackground(Void... params) {
            String url  = "https://www.linkedin.com/uas/oauth2/accessToken";
            String urlParams = null;

            // Create params to send for Access Token
            try {
                urlParams = "grant_type=authorization_code&code=" + URLEncoder.encode(currentCode, charset) +
                        "&redirect_uri=" + URLEncoder.encode(AmbassadorSingleton.LINKED_IN_CALLBACK_URL, charset) +
                        "&client_id=" + URLEncoder.encode(AmbassadorSingleton.LINKED_IN_CLIENT_ID, charset) +
                        "&client_secret=" + URLEncoder.encode(AmbassadorSingleton.LINKED_IN_CLIENT_SECRET, charset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Host", "www.linkedin.com");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(urlParams);
                wr.flush();
                wr.close();

                statusCode = connection.getResponseCode();

                // Get and read response from LinkedIN
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();

                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }

                try {
                    // Get values from JSON object
                    JSONObject json = new JSONObject(response.toString());
                    String accessToken = json.getString("access_token");
                    System.out.println(accessToken);

                    // Save Access Token to SharedPreferences so user doesn't need to sign in again
                    SharedPreferences prefs = context.getSharedPreferences(
                            "com.example.ambassador.ambassadorsdk", Context.MODE_PRIVATE);
                    prefs.edit().putString("linkedInToken", accessToken).apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            // Make sure status code is good and dismiss the activity
            if (statusCode < 300 && statusCode > 199) {
                Toast.makeText(getApplicationContext(), "Logged in successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Unable to log in, please try again!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


