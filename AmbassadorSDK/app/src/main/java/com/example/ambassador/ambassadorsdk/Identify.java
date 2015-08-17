package com.example.ambassador.ambassadorsdk;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by JakeDunahee on 7/23/15.
 */
public class Identify {
    private Context context;
    private Timer timer;
    private String objectString;
    private String emailAddress;
    private WebView wvTest;
    private JSONObject augurObject;

    public Identify(Context context, String emailAddress) {
        this.context = context;
    }

    public void getIdentity() {
        // Set up webview
        wvTest = new WebView(context);
//        wvTest.setWebChromeClient(new MyChromeClient());
        wvTest.getSettings().setDomStorageEnabled(true); // Helps with console log error
        wvTest.getSettings().setJavaScriptEnabled(true);
        wvTest.addJavascriptInterface(this, "android");
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

    @JavascriptInterface
    public void onData(String returnString) {
        if (returnString.startsWith("{\"consumer\"")) {
            AmbassadorSingleton.getInstance().setIdentifyObject(returnString);
            sendIdBroadcast();
            timer.cancel();
            objectString = returnString;
            Log.d("Augur", AmbassadorSingleton.getInstance().getIdentifyObject());
            String deviceID = getAugurID(returnString);
            createPusher(deviceID);
            wvTest.destroy();

            IdentifyRequest request = new IdentifyRequest();
            request.execute();
        } else {
            Log.d("Augur", "Augur not yet loaded");
        }
    }

    // Handles timer activity on the main thread to avoid errors caused by webview calls from different thread
    public android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(Message msg) {
            if (objectString == null){
                wvTest.loadUrl("https://staging.mbsy.co/universal/landing/?url=ambassador:ios/&universal_id=***REMOVED***");
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
                wvTest.loadUrl("javascript:android.onData(JSON.stringify(augur.json))");
            }
        }
    }

    private void sendIdBroadcast() {
        // Posts notification to listener once identity is successfully received
        Intent intent = new Intent("augurID");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public String getAugurID(String augurString) {
        String deviceID = "null";
        try {
            augurObject = new JSONObject(augurString);
            JSONObject deviceObject = augurObject.getJSONObject("device");
            deviceID = deviceObject.getString("ID");
            System.out.println("created augur object");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return deviceID;
    }

    public  void createPusher(String augurDeviceID) {
        String channelName = "private-snippet-channel@user=" + augurDeviceID;

        HttpAuthorizer authorizer = new HttpAuthorizer("https://dev-ambassador-api.herokuapp.com/auth/subscribe/");

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", AmbassadorSingleton.API_KEY);
        authorizer.setHeaders(headers);

        HashMap<String, String>queryParams = new HashMap<>();
        queryParams.put("auth_type", "private");
        queryParams.put("channel", channelName);

        authorizer.setQueryStringParameters(queryParams);

        PusherOptions options = new PusherOptions();
        options.setAuthorizer(authorizer);
        options.setEncrypted(true);

        Pusher pusher = new Pusher(AmbassadorSingleton.PUSHER_KEY, options);
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange connectionStateChange) {
                Log.d("Pusher", "State changed to " + connectionStateChange.getCurrentState() + " from " + connectionStateChange.getPreviousState());
            }

            @Override
            public void onError(String s, String s1, Exception e) {
                Log.d("Pusher", "There was a problem connecting to Pusher" + "Exception = " + e);
            }
        }, ConnectionState.ALL);

        pusher.connect();

        PrivateChannel channel = pusher.subscribePrivate(channelName, new PrivateChannelEventListener() {
            @Override
            public void onAuthenticationFailure(String message, Exception e) {
                Log.d("Pusher", "failed");
            }

            @Override
            public void onSubscriptionSucceeded(String channelName) {
                Log.d("Pusher", "succeeeded");
            }

            @Override
            public void onEvent(String channelName, String eventName, String data) {
                Log.d("Pusher", "event");
            }
        }, "identify_action");
    }

    class IdentifyRequest extends AsyncTask<Void, Void, Void> {
        int statusCode;

        @Override
        protected Void doInBackground(Void... params) {
            String url = "http://dev-ambassador-api.herokuapp.com/universal/action/identify/?u=***REMOVED***";

            JSONObject identifyObject = new JSONObject();

            try {
                identifyObject.put("email", emailAddress);
                identifyObject.put("mbsy_source", "");
                identifyObject.put("mbsy_cookie_code", "");

                JSONObject fingerPrintObject = new JSONObject();

                JSONObject augurConsumer = augurObject.getJSONObject("consumer");
                JSONObject consumerObject = new JSONObject();
                consumerObject.put("UID", augurConsumer.getString("UID"));

                JSONObject augurDevice = augurObject.getJSONObject("device");
                JSONObject deviceObject = new JSONObject();
                deviceObject.put("type", augurDevice.getString("type"));
                deviceObject.put("ID", augurDevice.getString("ID"));

                fingerPrintObject.put("consumer", consumerObject);
                fingerPrintObject.put("device", deviceObject);

                identifyObject.put("fp", fingerPrintObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(identifyObject.toString());
                wr.flush();
                wr.close();

                statusCode = connection.getResponseCode();

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();

                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }

                Log.d("Identify", response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
