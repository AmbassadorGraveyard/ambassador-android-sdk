package com.example.ambassador.ambassadorsdk;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JakeDunahee on 7/23/15.
 */
public class Identify {
    private Context context;
    private Timer timer;
    private String objectString;
    private WebView wvTest;

    public Identify(Context context) {
        this.context = context;
    }

    public void getIdentity() {
        // Set up webview
        wvTest = new WebView(context);
        wvTest.setWebChromeClient(new MyChromeClient());
        wvTest.setWebViewClient(new MyBrowser());
        wvTest.getSettings().setJavaScriptEnabled(true);
        wvTest.loadUrl("https://staging.mbsy.co/universal/landing/?url=ambassador:ios/&universal_id=***REMOVED***/");

        // Start timer and set to run every 3 seconds until it successfully gets augur identity object
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHandler.obtainMessage().sendToTarget();
            }
        }, 0, 3000);
    }

    // Handles timer activity on the main thread to avoid errors caused by webview calls from different thread
    public android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            if (objectString == null){
                wvTest.loadUrl("https://staging.mbsy.co/universal/landing/?url=ambassador:ios/&universal_id=***REMOVED***/");
            }
        }
    };

    // Subclass of webViewClent
    public class MyBrowser extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (url.startsWith("https://staging.mbsy.co")) {
                Log.d("AugurCall", "CHECKING FOR AUGUR ID");
                wvTest.loadUrl("javascript:console.log(JSON.stringify(augur.json))");
            }
        }
    }

    // Subclass of WebChromeClient where we can get console messages from webview's javascript
    final class MyChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            if (consoleMessage.message().startsWith("{\"consumer\"")) {
                AmbassadorSingleton.getInstance().setIdentifyObject(consoleMessage.message());
                sendIdBroadcast();
                timer.cancel();
                Log.d("AugurID", AmbassadorSingleton.getInstance().getIdentifyObject());
                System.out.println("AUGUR IDENTIFICATION SUCCESS!");
            }

            return super.onConsoleMessage(consoleMessage);
        }
    }

    private void sendIdBroadcast() {
        // Posts notification to listener once identity is successfully received
        Intent intent = new Intent("augurID");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
