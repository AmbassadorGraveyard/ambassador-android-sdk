package com.ambassador.ambassadorsdk;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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

/**
 * Created by JakeDunahee on 7/27/15.
 */
public class LinkedInLoginActivity extends AppCompatActivity {

    WebView webView;
    ProgressBar loader;

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

        // UI Components
        loader = (ProgressBar)findViewById(R.id.loadingPanel);
        webView = (WebView)findViewById(R.id.wvSocial);

        _setUpToolbar();
        loader.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new CustomBrowser());
        webView.loadUrl("https://www.linkedin.com/uas/oauth2/authorization?" +
                "response_type=code&client_id=777z4czm3edaef" +
                "&redirect_uri=http://localhost:2999" +
                "&state=987654321" +
                "&scope=r_basicprofile%20w_share");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void _setUpToolbar() {
        if (getSupportActionBar() != null) { getSupportActionBar().setTitle("Login to LinkedIn"); }

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        if (toolbar == null) return;

        final Drawable arrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        arrow.setColorFilter(getResources().getColor(R.color.linkedinToolBarArrow), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(arrow);

        toolbar.setBackgroundColor(getResources().getColor(R.color.linkedinToolBar));
        toolbar.setTitleTextColor(getResources().getColor(R.color.linkedinToolBarText));
    }

    private class CustomBrowser extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Breaks up url to grab separate components
            String[] urlArray = url.split("\\?");
            String callbackURL = urlArray[0];
            String codeErrorString = urlArray[1];

            // Checks for url to get code for getting Access Token
            if (callbackURL.startsWith(AmbassadorConfig.CALLBACK_URL) && codeErrorString.startsWith("code")) {
                String code;
                if (url.contains("&")) {
                    code = url.substring(url.indexOf("code=") + "code=".length(), url.indexOf("&"));
                } else {
                    code = url.substring(url.indexOf("code=") + "code=".length(), url.length() - 1);
                }

                requestManager.linkedInLoginRequest(code, new RequestManager.RequestCompletion() {
                    @Override
                    public void onSuccess(Object successResponse) {
                        Toast.makeText(getApplicationContext(), "Logged in successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Object failureResponse) {
                        Toast.makeText(getApplicationContext(), "Unable to log in, please try again!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            view.loadUrl(url);
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
            Toast.makeText(LinkedInLoginActivity.this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            finish();
        }

    }
}


