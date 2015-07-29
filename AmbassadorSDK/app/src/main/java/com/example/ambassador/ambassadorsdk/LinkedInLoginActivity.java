package com.example.ambassador.ambassadorsdk;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linkedin);

        webView = (WebView)findViewById(R.id.wvLinkedInLogin);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyBrowser());
        webView.loadUrl("https://www.linkedin.com/uas/oauth2/authorization?" +
                "response_type=code&client_id=777z4czm3edaef&redirect_uri=http://localhost:2999&state=987654321&scope=r_basicprofile%20w_share");
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            System.out.println(url);
            String[] urlArray = url.split("\\?");
            String callbackURL = urlArray[0];
            String codeErrorString = urlArray[1];
            if (callbackURL.startsWith(AmbassadorSingleton.LINKED_IN_CALLBACK_URL) && codeErrorString.startsWith("code")) {
                String code;
                if (url.contains("&")) {
                    code = url.substring(url.indexOf("code=") + "code=".length(), url.indexOf("&"));
                } else {
                    code = url.substring(url.indexOf("code=") + "code=".length(), url.length() - 1);
                }

                RequestTask requestTask = new RequestTask();
                requestTask.currentCode = code;
                requestTask.context = getApplicationContext();
                requestTask.execute();

                Toast.makeText(getApplicationContext(), "Successfully logged in", Toast.LENGTH_SHORT).show();
                finish();
            }

            view.loadUrl(url);
            return true;
        }
    }

    private void saveTokenToSharedPreferences(String token) throws UnsupportedEncodingException, MalformedURLException {
        new RequestTask().execute();
    }

    class RequestTask extends AsyncTask<Void, Void, Void> {
        public String currentCode;
        public Context context;
        @Override
        protected Void doInBackground(Void... params) {
            String url  = "https://www.linkedin.com/uas/oauth2/accessToken";
            String charset = "UTF-8";
            System.out.println("CREATING URL PARAMS!!!!!");
            String urlParams = null;

            try {
                urlParams = "grant_type=authorization_code&code=" + URLEncoder.encode(currentCode, "UTF-8") +
                        "&redirect_uri=" + URLEncoder.encode(AmbassadorSingleton.LINKED_IN_CALLBACK_URL, "UTF-8") +
                        "&client_id=" + URLEncoder.encode(AmbassadorSingleton.LINKED_IN_CLIENT_ID, "UTF-8") +
                        "&client_secret=" + URLEncoder.encode(AmbassadorSingleton.LINKED_IN_CLIENT_SECRET, "UTF-8");
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

                int status = connection.getResponseCode();

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();

                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }

                try {
                    JSONObject json = new JSONObject(response.toString());
                    String accessToken = json.getString("access_token");
                    System.out.println(accessToken);

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
    }
}


