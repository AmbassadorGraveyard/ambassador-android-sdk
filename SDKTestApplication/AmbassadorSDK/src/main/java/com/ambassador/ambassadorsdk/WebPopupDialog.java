package com.ambassador.ambassadorsdk;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

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
        WebSettings settings = webView.getSettings();

        settings.setAllowFileAccess(false);
        settings.setJavaScriptEnabled(false);
        settings.setSaveFormData(false);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(true);

        webView.setWebViewClient(new Client());

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        /** This works for dismiss on margin click */
        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return false;
            }
        });
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

    }

    public void load(String url) {
        webView.loadUrl(url);
    }

}
