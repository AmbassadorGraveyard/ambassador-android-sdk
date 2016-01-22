package com.ambassador.ambassadorsdk.internal.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;

import butterfork.Bind;
import butterfork.ButterFork;

/**
 * Dialog for handling external links on LinkedInLogin, opens in dialog using a WebView.
 */
public final class WebPopupDialog extends Dialog {

    // region Views
    @Bind(B.id.wvPopup)     protected WebView       wvPopup;
    @Bind(B.id.pbLoading)   protected ProgressBar   pbLoading;
    // endregion

    public WebPopupDialog(Context context) {
        super(context);

        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_webview_popup);
        ButterFork.bind(this);

        configureDialog();
        configureWebView();
        enableDismissOnTouchOutside();
    }

    public void load(@NonNull String url) {
        wvPopup.loadUrl(url);
    }

    private void configureDialog() {
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = 0.85f;
    }

    private void configureWebView() {
        WebSettings settings = wvPopup.getSettings();

        settings.setAllowFileAccess(false);
        settings.setJavaScriptEnabled(false);
        settings.setSaveFormData(false);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        wvPopup.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        wvPopup.setVerticalScrollBarEnabled(false);
        wvPopup.setHorizontalScrollBarEnabled(true);

        wvPopup.setWebViewClient(new WebClient());
    }

    private void enableDismissOnTouchOutside() {
        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return false;
            }
        });
    }

    private class WebClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri uri = Uri.parse(url);
            if (uri != null && "/uas/login".equals(uri.getPath())) {
                dismiss();
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            pbLoading.setVisibility(View.GONE);
        }

    }

}
