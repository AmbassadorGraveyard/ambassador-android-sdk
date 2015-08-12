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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import org.json.JSONException;
import org.json.JSONObject;

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
        wvTest.getSettings().setDomStorageEnabled(true); // Helps with console log error
        wvTest.getSettings().setJavaScriptEnabled(true);
        wvTest.setWebViewClient(new MyBrowser());

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
                wvTest.loadUrl("https://staging.mbsy.co/universal/landing/?url=ambassador:ios/&universal_id=abfd1c89-4379-44e2-8361-ee7b87332e32/");
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
                getAugurID(consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            } else {
                return Boolean.parseBoolean(null);
            }
        }
    }

    private void sendIdBroadcast() {
        // Posts notification to listener once identity is successfully received
        Intent intent = new Intent("augurID");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public String getAugurID(String augurString) {
        try {
            JSONObject augurObject = new JSONObject(augurString);
            JSONObject deviceObject = augurObject.getJSONObject("device");
            String deviceID = deviceObject.getString("ID");
            System.out.println("created augur object");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "alksdjf";
    }

    public  void createPusher(String augurDeviceID) {
        Pusher pusher = new Pusher(AmbassadorSingleton.PUSHER_KEY);
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange connectionStateChange) {
                Log.d("Pusher", "State changed to " + connectionStateChange.getCurrentState() + " from " + connectionStateChange.getPreviousState());
            }

            @Override
            public void onError(String s, String s1, Exception e) {
                Log.d("Pusher", "There was a problem connecting to Pusher");
            }
        }, ConnectionState.ALL);

        // Subscribing to channel
        String channelName = "snippet-channel@user=<" + augurDeviceID + ">";
        PrivateChannel privateChannel = pusher.subscribePrivate(channelName);
        privateChannel.bind("identify_action", new ChannelEventListener() {
            @Override
            public void onSubscriptionSucceeded(String s) {

            }

            @Override
            public void onEvent(String s, String s1, String s2) {
                Log.d("Pusher", "String 1 = " + s);
                Log.d("Pusher", "String 2 = " + s1);
                Log.d("Pusher", "String 3 = " + s2);
            }
        });
    }
}
