package com.ambassador.ambassadorsdk;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

/**
 * Created by JakeDunahee on 7/27/15.
 */
public class LinkedInLoginActivity extends AppCompatActivity {

    ProgressBar loader;

    @Inject
    RequestManager requestManager;

    private boolean popupIsOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utilities.setStatusBar(getWindow(), getResources().getColor(R.color.linkedin_blue));

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

        loader = (ProgressBar) findViewById(R.id.loadingPanel);

        String authUrl = LinkedInApi.getAuthorizationUrl("code", "777z4czm3edaef", "http://localhost:2999", "987654321", "r_basicprofile", "w_share");

        WebView webView = (WebView)findViewById(R.id.wvSocial);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setWebViewClient(new LinkedInClient());
        webView.loadUrl(authUrl);
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

    private class LinkedInClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!isHandled(url) && !popupIsOpen) {
                Uri uri = Uri.parse(url);
                if (uri != null && uri.getPath().equals("/uas/request-password-reset")) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }

                view.stopLoading();
                WebPopupDialog dialog = new WebPopupDialog(LinkedInLoginActivity.this);
                dialog.setOwnerActivity(LinkedInLoginActivity.this);
                dialog.load(url);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        popupIsOpen = false;
                    }
                });
                dialog.show();
                popupIsOpen = true;
            }
            return false;
        }

        @Override
        public void onPageFinished(final WebView view, String url) {
            super.onPageFinished(view, url);
            if (url.equals(LinkedInApi.lastGeneratedAuthorizationUrl)) {
                loader.setVisibility(View.GONE);
            } else if (LinkedInApi.isSuccessUrl(url)) {
                String requestToken = LinkedInApi.extractToken(url);
                if (requestToken == null ) {
                    finish();
                    return;
                }
                requestManager.linkedInLoginRequest(requestToken, new RequestManager.RequestCompletion() {
                    @Override
                    public void onSuccess(Object successResponse) {
                        Toast.makeText(getApplicationContext(), "Logged in successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Object failureResponse) {
                        Toast.makeText(getApplicationContext(), "Unable to log in, please try again!", Toast.LENGTH_SHORT).show();
                        view.loadUrl(LinkedInApi.lastGeneratedAuthorizationUrl);
                    }
                });
            }
        }

        private boolean isHandled(String url) {
            Uri uri = Uri.parse(url);
            if (uri != null) {
                return uri.getHost().equals("localhost");
            }
            return false;
        }

    }

    static class LinkedInApi {

        private static final String AUTHORIZE_URL = "https://www.linkedin.com/uas/oauth2/authorization";
        private static final String RESPONSE_TYPE_KEY = "response_type";
        private static final String CLIENT_ID_KEY = "client_id";
        private static final String REDIRECT_URI_KEY = "redirect_uri";
        private static final String STATE_KEY = "state";
        private static final String SCOPE_KEY = "scope";

        public static String lastGeneratedAuthorizationUrl;

        public static String getAuthorizationUrl(String responseType, String clientId, String redirectUrl, String state, String... scopes) {
            Uri authUri = Uri.parse(AUTHORIZE_URL)
                    .buildUpon()
                    .appendQueryParameter(RESPONSE_TYPE_KEY, responseType)
                    .appendQueryParameter(CLIENT_ID_KEY, clientId)
                    .appendQueryParameter(REDIRECT_URI_KEY, "REDIRECT")
                    .appendQueryParameter(STATE_KEY, state)
                    .appendQueryParameter(SCOPE_KEY, getScopeValue(scopes))
                    .build();

            lastGeneratedAuthorizationUrl = authUri.toString().replace("REDIRECT", redirectUrl);
            return lastGeneratedAuthorizationUrl;
        }

        private static String getScopeValue(String... scopes) {
            String ret = "";
            for (String scope : scopes ) {
                ret += scope;
                ret += " ";
            }
            if (ret.length() != 0) {
                ret = ret.substring(0, ret.length() - 1);
            }

            return ret;
        }

        public static boolean isSuccessUrl(String url) {
            try {
                URL urlObj = new URL(url);
                return urlObj.getHost().equals("localhost");
            } catch (MalformedURLException e) {
                return false;
            }
        }

        public static String extractToken(String url) {
            Uri uri = Uri.parse(url);
            if (uri != null) {
                return uri.getQueryParameter("code");
            }

            return null;
        }

    }

}


