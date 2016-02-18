package com.ambassador.ambassadorsdk.internal.api;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.ambassador.ambassadorsdk.BuildConfig;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.api.identify.IdentifyApi;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Handles everything to do with Pusher, our socket to the backend.
 * Keeps track of a single channel and does the connecting, subscribing, disposing of it all.
 * Handles incoming events and dispatches them on the otto event ambassaBus after processing.
 */
@Singleton
public class PusherManager {

    protected static String universalKey;

    @Inject protected RequestManager requestManager;
    @Inject protected Auth auth;
    @Inject protected User user;

    protected Channel channel;

    protected static List<PusherListener> pusherListeners;

    /**
     * Default constructor handling injection and dependencies.
     */
    public PusherManager() {
        AmbSingleton.inject(this);
        universalKey = auth.getUniversalToken();
        pusherListeners = new ArrayList<>();

        addPusherListener(new PusherListenerAdapter() {
            @Override
            public void onEvent(String data) {
                super.onEvent(data);
                Utilities.debugLog("PusherSDK", "data = " + data);
                JsonParser parser = new JsonParser();
                JsonObject jsonData = parser.parse(data).getAsJsonObject();
                if (jsonData.has("url")) {
                    String url = jsonData.get("url").getAsString();
                    externalRequest(url);
                } else {
                    setPusherInfo(jsonData);
                }
            }
        });
    }

    /**
     * Creates a new Pusher channel and connects to Pusher.
     * No subscription is made here.
     */
    public void startNewChannel() {
        if (channel != null) channel.disconnect();
        channel = new Channel();
        channel.init();
    }

    /**
     * Sets up the channel with a new subscription to the Ambassador backend.
     */
    public void subscribeChannelToAmbassador() {
        if (channel != null && channel.isConnected()) {
            if (channel.getChannelName() != null) {
                try {
                    channel.unsubscribe(channel.getChannelName());
                } catch (Exception e) {
                    // this doesn't matter
                }
            }
        }

        requestSubscription();
    }

    /**
     * Requests the channel details from Ambassador and subscribes the Pusher client.
     */
    protected void requestSubscription() {
        requestManager.createPusherChannel(new RequestManager.RequestCompletion() {
            @Override
            public void onSuccess(Object successResponse) {
                IdentifyApi.CreatePusherChannelResponse channelData = (IdentifyApi.CreatePusherChannelResponse) successResponse;
                channel.subscribe(channelData.channel_name, channelData.expires_at, channelData.client_session_uid);
            }

            @Override
            public void onFailure(Object failureResponse) {
                for (PusherListener pusherListener : pusherListeners) {
                    pusherListener.subscriptionFailed();
                }
            }
        });
    }

    /**
     * Tells PusherManager that a new request is happening so it can update the requestId.
     */
    public void newRequest() {
        if (channel != null) {
            channel.requestId = System.currentTimeMillis();
        }
    }

    /**
     * Get the existing channel pusher channel.
     */
    @Nullable
    public Channel getChannel() {
        return channel;
    }

    public void addPusherListener(@NonNull PusherListener pusherListener) {
        pusherListeners.add(pusherListener);
    }

    public void removePusherListener(@NonNull PusherListener pusherListener) {
        pusherListeners.remove(pusherListener);
    }

    /**
     * Perfoms external pusher request on a passed in url, using RequestManager.
     * @param url the url to make a request to.
     */
    protected void externalRequest(String url) {
        requestManager.externalPusherRequest(url, new RequestManager.RequestCompletion() {

            @Override
            public void onSuccess(Object successResponse) {
                Utilities.debugLog("PusherSDK External", "Saved pusher object as String = " + successResponse.toString());
                String data = successResponse.toString();
                JsonParser parser = new JsonParser();
                JsonObject jsonData = parser.parse(data).getAsJsonObject();

                if (jsonData.get("request_id").getAsLong() == channel.getRequestId()) {
                    setPusherInfo(jsonData);
                }
            }

            @Override
            public void onFailure(Object failureResponse) {
                Utilities.debugLog("PusherSDK External", "FAILED to save pusher object with error: " + failureResponse);
            }

        });
    }

    /**
     * Processes and stores pusher data into User.
     * @param data the json info to store.
     */
    protected void setPusherInfo(JsonObject data) {
        JsonObject pusherSave = new JsonObject();
        JsonObject pusherObject = data.get("body").getAsJsonObject();

        pusherSave.add("email", pusherObject.get("email"));
        pusherSave.add("firstName", pusherObject.get("first_name"));
        pusherSave.add("lastName", pusherObject.get("last_name"));
        pusherSave.add("phoneNumber", pusherObject.get("phone"));
        pusherSave.add("urls", pusherObject.get("urls").getAsJsonArray());

        user.setPusherInfo(pusherSave);

        user.setFirstName(pusherObject.get("first_name").getAsString());
        user.setLastName(pusherObject.get("last_name").getAsString());

        Intent intent = new Intent("pusherData");
        LocalBroadcastManager.getInstance(AmbSingleton.getContext()).sendBroadcast(intent);
    }

    /**
     * Handles a single connection to Pusher.  Keeps track of a channel name and session id.
     * Connects and subscribes with this information and receives events, which are pushed back
     * to parent.
     */
    public static class Channel {

        protected Pusher pusher;

        protected String sessionId;
        protected String channelName;
        protected Date expiry;
        protected long requestId;
        protected ConnectionState connectionState;

        protected Channel() {}

        /**
         * Injects dependencies and sets up a Pusher client object.
         */
        public void init() {
            AmbSingleton.inject(this);
        }

        /**
         * Sets up a Pusher object from the Pusher Android SDK.
         * @return a Pusher object configured with the channel name from Ambassador.
         */
        protected Pusher setupPusher() {
            int pusherCallbackId = BuildConfig.IS_RELEASE_BUILD ? R.string.pusher_callback_url : R.string.pusher_callback_url_dev;
            String pusherCallbackUrl = new StringResource(pusherCallbackId).getValue();
            HttpAuthorizer authorizer = new HttpAuthorizer(pusherCallbackUrl);

            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", universalKey);
            authorizer.setHeaders(headers);

            HashMap<String, String> queryParams = new HashMap<>();
            queryParams.put("auth_type", "private");
            queryParams.put("channel", channelName);
            authorizer.setQueryStringParameters(queryParams);

            PusherOptions options = new PusherOptions();
            options.setAuthorizer(authorizer);
            options.setEncrypted(true);

            int keyId = BuildConfig.IS_RELEASE_BUILD ? R.string.pusher_key_prod : R.string.pusher_key_dev;
            String key = new StringResource(keyId).getValue();

            return new Pusher(key, options);
        }

        protected ConnectionEventListener connectionEventListener = new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                connectionState = change.getCurrentState();
                switch (change.getCurrentState()) {
                    case CONNECTED:
                        for (PusherListener pusherListener : pusherListeners) {
                            pusherListener.connected();
                        }
                        break;

                    case DISCONNECTED:
                        for (PusherListener pusherListener : pusherListeners) {
                            pusherListener.disconnected();
                        }
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onError(String message, String code, Exception e) {
                for (PusherListener pusherListener : pusherListeners) {
                    pusherListener.connectionFailed();
                }
            }
        };

        /**
         * Subscribes Pusher connection to the channel's channel name.
         * Listens for events and dispatches them accordingly.
         */
        public void subscribe(@NonNull final String channelName, @NonNull String expiry, @NonNull String sessionId) {
            this.channelName = channelName;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                this.expiry = sdf.parse(expiry);
            } catch (ParseException e) {
                Log.e("AmbassadorSDK", e.toString());
            }

            this.sessionId = sessionId;

            pusher = setupPusher();
            pusher.connect(connectionEventListener, ConnectionState.ALL);
            pusher.subscribePrivate(channelName, privateChannelEventListener);
        }

        private PrivateChannelEventListener privateChannelEventListener = new PrivateChannelEventListener() {

            @Override
            public void onAuthenticationFailure(String message, Exception e) {
                for (PusherListener pusherListener : pusherListeners) {
                    pusherListener.subscriptionFailed();
                }
            }

            @Override
            public void onSubscriptionSucceeded(String channelName) {
                for (PusherListener pusherListener : pusherListeners) {
                    pusherListener.subscribed();
                }
            }

            @Override
            public void onEvent(String channelName, String eventName, String data) {
                for (PusherListener pusherListener : pusherListeners) {
                    pusherListener.onEvent(data);
                }
            }

        };

        /**
         * Un-subscribes from a Pusher channel.
         * @param channelName String for name of channel to disconnect from.
         */
        public void unsubscribe(String channelName) {
            pusher.unsubscribe(channelName);
            for (PusherListener pusherListener : pusherListeners) {
                pusherListener.unsubscribed();
            }
        }

        /**
         * Disconnects connection to Pusher.
         */
        public void disconnect() {
            pusher.disconnect();
        }

        public Pusher getPusher() {
            return pusher;
        }

        public String getSessionId() {
            return sessionId;
        }

        public String getChannelName() {
            return channelName;
        }

        public Date getExpiry() {
            return expiry;
        }

        public long getRequestId() {
            return requestId;
        }

        public ConnectionState getConnectionState() {
            return connectionState;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public void setExpiry(Date expiry) {
            this.expiry = expiry;
        }

        public void setRequestId(long requestId) {
            this.requestId = requestId;
        }

        public boolean isConnected() {
            return connectionState != null && connectionState.equals(ConnectionState.CONNECTED);
        }

        protected static class Builder {

            private String sessionId;
            private String channelName;
            private Date expiry;
            private long requestId;
            private ConnectionState connectionState;

            public Builder() {}

            public Builder setSessionId(String sessionId) {
                this.sessionId = sessionId;
                return this;
            }

            public Builder setChannelName(String channelName) {
                this.channelName = channelName;
                return this;
            }

            public Builder setExpiry(Date expiry) {
                this.expiry = expiry;
                return this;
            }

            public Builder setRequestId(long requestId) {
                this.requestId = requestId;
                return this;
            }

            public Builder setConnectionState(ConnectionState connectionState) {
                this.connectionState = connectionState;
                return this;
            }

            public Channel build() {
                Channel channel = new Channel();
                channel.sessionId = this.sessionId;
                channel.channelName = this.channelName;
                channel.expiry = this.expiry;
                channel.requestId = this.requestId;
                channel.connectionState = this.connectionState;
                return channel;
            }

        }

    }

    /**
     * Callback methods for Pusher events.
     */
    public interface PusherListener {
        void connected();
        void disconnected();
        void subscribed();
        void unsubscribed();
        void connectionFailed();
        void subscriptionFailed();
        void onEvent(String data);
    }

    public static List<PusherListener> getPusherListeners() {
        return pusherListeners;
    }

}
