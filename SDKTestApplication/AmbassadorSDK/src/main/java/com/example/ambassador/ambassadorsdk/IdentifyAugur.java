package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;
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

/**
 * Created by JakeDunahee on 7/23/15.
 */
class IdentifyAugur implements IIdentify {
    private Context context;
    private Timer augurGetTimer, webPageReloadTimer;
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
        wvTest.loadUrl("https://staging.mbsy.co/universal/landing/?url=ambassador:ios/&universal_id=***REMOVED***");

        _startTimers();
    }

    @JavascriptInterface
    public void onData(String returnString) {
        if (returnString.startsWith("{\"consumer\"")) {
            Log.d("Augur", "Augur object received!!! Object = " + returnString);
            AmbassadorSingleton.getInstance().setIdentifyObject(returnString);
            augurGetTimer.cancel();
            webPageReloadTimer.cancel();
            String deviceID = _getAugurID(returnString);
            _createPusher(deviceID);
        } else {
            Log.d("Augur", "Augur not yet loaded");
        }
    }

    //region Timer Functions
    final android.os.Handler myHandler = new android.os.Handler();

    private void getAugur() {
        myHandler.post(myRunnable);
    }

    private void reloadWebPage() {
        myHandler.post(reloadRunnable);
    }

    final Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("AugurCall", "Attempting to get Augur Object");
            wvTest.loadUrl("javascript:android.onData(JSON.stringify(augur.json))");
        }
    };

    final Runnable reloadRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("Augur", "Reloading webView");
            wvTest.loadUrl("https://staging.mbsy.co/universal/landing/?url=ambassador:ios/&universal_id=***REMOVED***");
        }
    };
    //endregion


    // Subclass of webViewClent
    private class MyBrowser extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            wvTest.loadUrl("javascript:android.onData(JSON.stringify(augur.json))");
        }
    }

    private void _sendIdBroadcast() {
        // Functionality: Posts notification to listener once identity is successfully received
        Intent intent = new Intent("pusherData");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private String _getAugurID(String augurString) {
        // Functionality: Pulls deviceID from Augur Object
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

    private void _createPusher(String augurDeviceID) {
        // Functionality: Subcribes to Pusher channel and sets listener for pusher action
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
                _getAndSavePusherInfo(data);
            }
        }, "identify_action");
    }

    private void _getAndSavePusherInfo(String jsonObject) {
        // Functionality: Saves Pusher object to SharedPreferences
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
            _sendIdBroadcast(); // Tells AmbassadorActivity to update edittext with url
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void _startTimers() {
        // Functionality: Starts timer and set to run every 3 seconds until it successfully gets augur identity object
        augurGetTimer = new Timer();
        augurGetTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getAugur();
            }
        }, 0, 3000);

        // Reloads webpage every 12 seconds in case user wasnt on network while loading the webpage
        webPageReloadTimer = new Timer();
        webPageReloadTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                reloadWebPage();
            }
        }, 12000, 12000);
    }

    private class IdentifyRequest extends AsyncTask<Void, Void, Void> {
        int statusCode;

        @Override
        protected Void doInBackground(Void... params) {
            String url = "http://dev-ambassador-api.herokuapp.com/universal/action/identify/?u=***REMOVED***/";

            JSONObject identifyObject = new JSONObject();

            try {
                identifyObject.put("email", emailAddress);
                identifyObject.put("source", "android_sdk_pilot");
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
                Log.d("Augur", "Identify Object = " + identifyObject.toString());
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

}
