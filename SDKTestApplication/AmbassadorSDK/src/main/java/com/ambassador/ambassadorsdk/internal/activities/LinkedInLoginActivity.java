package com.ambassador.ambassadorsdk.internal.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.RequestManager;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.WebPopupDialog;
import com.ambassador.ambassadorsdk.utils.Device;
import com.ambassador.ambassadorsdk.utils.StringResource;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import butterfork.Bind;
import butterfork.ButterFork;

/**
 * Activity that handles OAuth authentication with LinkedIn.
 * Presents a WebView prompting the user to login, and handles getting
 * the access token stored in AmbassadorConfig.
 */
public final class LinkedInLoginActivity extends AppCompatActivity {

    // region Fields

    // region Views
    @Bind(B.id.action_bar)  protected Toolbar       toolbar;
    @Bind(B.id.wvLogin)     protected WebView       wvLogin;
    @Bind(B.id.pbLoading)   protected ProgressBar   loader;
    // endregion

    // region Dependencies
    @Inject protected RequestManager    requestManager;
    @Inject protected Device            device;
    // endregion

    // region Local members
    protected RAFOptions raf = RAFOptions.get();
    protected String authUrl;
    // endregion

    // endregion

    // region Methods

    // region Activity overrides
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // Injection
        AmbassadorSingleton.getInstanceComponent().inject(this);
        ButterFork.bind(this);

        // Requirement checks
        finishIfContextInvalid();
        finishIfDeviceNotConnected();
        if (isFinishing()) return;

        // Other setup
        setUpToolbar();
        configureWebView();
        generateAuthUrl();
        loadLoginPage();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
    // endregion

    // region Requirement checks
    private void finishIfContextInvalid() {
        if (!AmbassadorSingleton.isValid()) {
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
            actionBar.setTitle("Login to LinkedIn");
        }

        Drawable arrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        arrow.setColorFilter(raf.getLinkedInToolbarArrowColor(), PorterDuff.Mode.SRC_ATOP);

        if (toolbar == null) return;

        toolbar.setNavigationIcon(arrow);
        toolbar.setBackgroundColor(raf.getLinkedinToolbarColor());
        toolbar.setTitleTextColor(raf.getLinkedInToolbarTextColor());

        Utilities.setStatusBar(getWindow(), raf.getLinkedinToolbarColor());
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
        wvLogin.setWebViewClient(new LinkedInClient());
    }

    private void generateAuthUrl() {
        authUrl = LinkedInApi.getAuthorizationUrl(
                new StringResource(R.string.linked_in_login_response_type).getValue(),
                new StringResource(R.string.linked_in_client_id).getValue(),
                new StringResource(R.string.linked_in_callback_url).getValue(),
                "987654321",
                new StringResource(R.string.linked_in_r_profile_permission).getValue(),
                new StringResource(R.string.linked_in_w_share_permission).getValue()
        );
    }

    private void loadLoginPage() {
        wvLogin.loadUrl(authUrl);
    }
    // endregion

    // endregion

    // region Classes

    // region LinkedIn Web
    /**
     * WebViewClient extension that handles redirects, login finishing, and opening un-handled
     * links inside of a WebPopupDialog.
     */
    private final class LinkedInClient extends WebViewClient {

        private WebPopupDialog popupDialog;

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!isHandled(url) && !isPopupOpen()) {
                Uri uri = Uri.parse(url);
                if (uri != null && uri.getPath().equals("/uas/request-password-reset")) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(uri);
                    startActivity(i);
                    view.stopLoading();
                    return false;
                } else if (uri != null && uri.getPath().equals("/start/join")) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(uri);
                    startActivity(i);
                    view.stopLoading();
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
            if (url.equals(LinkedInApi.lastGeneratedAuthorizationUrl)) {
                loader.setVisibility(View.GONE);
            } else if (LinkedInApi.isSuccessUrl(url)) {
                String requestToken = LinkedInApi.extractToken(url);
                if (requestToken == null) {
                    finish();
                    return;
                }
                requestManager.linkedInLoginRequest(requestToken, new RequestManager.RequestCompletion() {
                    @Override
                    public void onSuccess(Object successResponse) {
                        Toast.makeText(getApplicationContext(), new StringResource(R.string.login_success).getValue(), Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(Object failureResponse) {
                        Toast.makeText(getApplicationContext(), new StringResource(R.string.login_failure).getValue(), Toast.LENGTH_SHORT).show();
                        view.loadUrl(LinkedInApi.lastGeneratedAuthorizationUrl);
                    }
                });
            }
        }

        private boolean isHandled(String url) {
            Uri uri = Uri.parse(url);
            return uri != null && uri.getHost().equals("localhost");
        }

        private void popup(String url) {
            popupDialog = new WebPopupDialog(LinkedInLoginActivity.this);
            popupDialog.setOwnerActivity(LinkedInLoginActivity.this);
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

    /**
     * Handles information for working with LinkedIn OAuth like generating login URLs and
     * extracting tokens.
     */
    private static final class LinkedInApi {

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
    // endregion

    // endregion

}


