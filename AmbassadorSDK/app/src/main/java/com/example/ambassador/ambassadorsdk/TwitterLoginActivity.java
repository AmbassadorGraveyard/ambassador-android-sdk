package com.example.ambassador.ambassadorsdk;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by JakeDunahee on 7/29/15.
 */

// NOT USED YET!
class TwitterLoginActivity extends ActionBarActivity {
    private WebView wvTwitter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        wvTwitter = (WebView)findViewById(R.id.wvSocial);
        wvTwitter.getSettings().setJavaScriptEnabled(true);
        wvTwitter.setWebViewClient(new CustomBrowser());
        wvTwitter.loadUrl("https://api.twitter.com/oauth/authenticate?oauth_token=NPcudxy0yU5T3tBzho7iCotZ3cnetKwcTIRlX0iwRl0");
    }

    private class CustomBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
