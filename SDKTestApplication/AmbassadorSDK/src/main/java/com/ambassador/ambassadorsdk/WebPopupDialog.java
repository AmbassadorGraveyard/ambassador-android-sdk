package com.ambassador.ambassadorsdk;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by dylan on 12/4/15.
 */
public class WebPopupDialog extends Dialog {

    private WebView webView;
    private ProgressBar progressBar;

    public WebPopupDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_webview_popup);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = 0.85f;

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new Client());

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private class Client extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            if (uri != null) {
                if (uri.getPath().equals("/uas/login")) {
                    dismiss();
                }
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            Uri uri = Uri.parse(url);
            if (uri != null) {
                if (uri.getPath().equals("/start/reg/api/createAccount")) {
                    WebPopupDialog.this.dismiss();
                    Toast.makeText(getContext(), "Account Registered", Toast.LENGTH_SHORT).show();
                }
            }
        }


    }

    public void load(String url) {
        webView.loadUrl(url);
    }

}
