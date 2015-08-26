package com.example.ambassador.ambassadorsdk;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.IntDef;
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
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.net.URL;
import java.util.logging.Handler;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by JakeDunahee on 7/23/15.
 */
public class IdentifyAugur implements IIdentify {
    private Context context;
    private Timer timer;
    private String emailAddress;
    private WebView wvTest;
    private JSONObject augurObject;

    public IdentifyAugur(Context context, String emailAddress) {
        this.context = context;
        this.emailAddress = emailAddress;
    }

    @Override
    public void getIdentity() {
        // Set up webview
        wvTest = new WebView(context);
        wvTest.getSettings().setDomStorageEnabled(true); // Helps with console log error
        wvTest.getSettings().setJavaScriptEnabled(true);
        wvTest.addJavascriptInterface(this, "android");
        wvTest.setWebViewClient(new MyBrowser());

        // Start timer and set to run every 3 seconds until it successfully gets augur identity object
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                reloadWebPage();
            }
        }, 0, 3000);
    }

    @JavascriptInterface
    public void onData(String returnString) {
        if (returnString.startsWith("{\"consumer\"")) {
            Log.d("Augur", "Augur object received!!! Object = " + returnString);
            AmbassadorSingleton.getInstance().setIdentifyObject(returnString);
            timer.cancel();
            String deviceID = getAugurID(returnString);
            createPusher(deviceID);
        } else {
            Log.d("Augur", "Augur not yet loaded");
        }
    }

    final android.os.Handler myHandler = new android.os.Handler();

    private void reloadWebPage() {
        myHandler.post(myRunnable);
    }

    final Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            wvTest.loadUrl("https://staging.mbsy.co/universal/landing/?url=ambassador:ios/&universal_id=***REMOVED***");
        }
    };


    // Subclass of webViewClent
    public class MyBrowser extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (url.startsWith("https://staging.mbsy.co")) {
                Log.d("AugurCall", "Attempting to get Augur Object");
                wvTest.loadUrl("javascript:android.onData(JSON.stringify(augur.json))");
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            wvTest.stopLoading();
        }
    }

    private void sendIdBroadcast() {
        // Posts notification to listener once identity is successfully received
        Intent intent = new Intent("pusherData");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    // Pulls deviceID from Augur Object
    public String getAugurID(String augurString) {
        String deviceID = "null";
        try {
            augurObject = new JSONObject(augurString);
            JSONObject deviceObject = augurObject.getJSONObject("device");
            deviceID = deviceObject.getString("ID");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return deviceID;
    }

    public void createPusher(String augurDeviceID) {
        String channelName = "private-snippet-channel@user=" + augurDeviceID;

        // HttpAuthorizer is used to append headers and extra parameters to the initial Pusher authorization request
        HttpAuthorizer authorizer = new HttpAuthorizer("https://dev-ambassador-api.herokuapp.com/auth/subscribe/");

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", AmbassadorSingleton.getInstance().getAPIKey());
        authorizer.setHeaders(headers);

        HashMap<String, String> queryParams = new HashMap<>();
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
                Log.d("Pusher", "State changed from " + connectionStateChange.getPreviousState() + " to " + connectionStateChange.getCurrentState());
            }

            @Override
            public void onError(String s, String s1, Exception e) {
                Log.d("Pusher", "There was a problem connecting to Pusher" + "Exception = " + e);
            }
        }, ConnectionState.ALL);

        pusher.connect();

        pusher.subscribePrivate(channelName, new PrivateChannelEventListener() {
            @Override
            public void onAuthenticationFailure(String message, Exception e) {
                Log.d("Pusher", "Failed to subscribe to Pusher because " + message + ". The " +
                        "exception was " + e);
            }

            @Override
            public void onSubscriptionSucceeded(String channelName) {
                Log.d("Pusher", "Successfully subscribed to " + channelName);
                IdentifyRequest request = new IdentifyRequest();
                request.execute();
            }

            @Override
            public void onEvent(String channelName, String eventName, String data) {
                Log.d("Pusher", "data = " + data);
                getAndSavePusherInfo(data);
            }
        }, "identify_action");
    }

    class IdentifyRequest extends AsyncTask<Void, Void, Void> {
        int statusCode;

        @Override
        protected Void doInBackground(Void... params) {
            String url = "http://dev-ambassador-api.herokuapp.com/universal/action/identify/?u=***REMOVED***/";

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
                connection.setRequestProperty("Authorization", AmbassadorSingleton.getInstance().getAPIKey());

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(identifyObject.toString());
                wr.flush();
                wr.close();

                statusCode = connection.getResponseCode();

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }

                Log.d("Pusher", response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    // Saves Pusher object to SharedPreferences
    public void getAndSavePusherInfo(String jsonObject) {
        JSONObject pusherSave = new JSONObject();

        try {
            JSONObject pusherObject = new JSONObject(jsonObject);
            String firstName = pusherObject.getString("first_name");
            String lastName = pusherObject.getString("last_name");
            String phoneNumber = pusherObject.getString("phone");
            String email = pusherObject.getString("email");

            pusherSave.put("email", email);
            pusherSave.put("firstName", firstName);
            pusherSave.put("lastName", lastName);
            pusherSave.put("phoneNumber", phoneNumber);
            pusherSave.put("urls", pusherObject.getJSONArray("urls"));

            AmbassadorSingleton.getInstance().savePusherInfo(pusherSave.toString());
            sendIdBroadcast(); // Tells MainActivity to update edittext with url
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
