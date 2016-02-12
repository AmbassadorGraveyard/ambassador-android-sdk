package com.ambassador.ambassadorsdk.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ambassador.ambassadorsdk.BuildConfig;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.identify.IdentifyApi;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

/**
 * Handles everything to do with Pusher, our socket to the backend.
 * Keeps track of a single channel and does the connecting, subscribing, disposing of it all.
 * Handles incoming events and dispatches them on the otto event ambassaBus after processing.
 */
public class PusherManager {

    protected static String universalKey;

    @Inject protected RequestManager requestManager;
    @Inject protected Auth auth;

    protected Channel channel;

    /**
     * Default constructor handling injection and dependencies.
     */
    public PusherManager() {
        AmbassadorSingleton.getInstanceComponent().inject(this);
        universalKey = auth.getUniversalToken();
    }

    /**
     * Creates a new Pusher channel and connects to Pusher.
     * No subscription is made here.
     */
    public void startNewChannel() {
        if (channel != null) channel.disconnect();
        channel = new Channel();
        channel.init();
        channel.connect();
    }

    /**
     * Sets up the channel with a new subscription to the Ambassador backend.
     */
    public void subscribeChannelToAmbassador() {
        if (channel != null && !channel.isConnected()) {
            startNewChannel();
            return;
        }

        requestSubscription();
    }

    /**
     * Requests the channel details from Ambassador and subscribes the Pusher client.
     */
    private void requestSubscription() {
        requestManager.createPusherChannel(new RequestManager.RequestCompletion() {
            @Override
            public void onSuccess(Object successResponse) {
                IdentifyApi.CreatePusherChannelResponse channelData = (IdentifyApi.CreatePusherChannelResponse) successResponse;
                channel.subscribe(channelData.channel_name, channelData.expires_at, channelData.client_session_uid);
            }

            @Override
            public void onFailure(Object failureResponse) {

            }
        });
    }

    /**
     * Get the existing channel pusher channel.
     */
    @Nullable
    public Channel getChannel() {
        return channel;
    }

    /**
     * Handles a single connection to Pusher.  Keeps track of a channel name and session id.
     * Connects and subscribes with this information and receives events, which are pushed back
     * to parent.
     */
    public static final class Channel {

        protected Pusher pusher;

        protected String sessionId;
        protected String channelName;
        protected Date expiry;
        protected long requestId;
        protected ConnectionState connectionState;

        private Channel() {}

        /**
         * Injects dependencies and sets up a Pusher client object.
         */
        public void init() {
            AmbassadorSingleton.getInstanceComponent().inject(this);
            this.pusher = setupPusher();
        }

        /**
         * Sets up a Pusher object from the Pusher Android SDK.
         * @return a Pusher object configured with the channel name from Ambassador.
         */
        private Pusher setupPusher() {
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

        /**
         * Establishes a connection to Pusher and listens to connection state changes,
         * changing {@link Channel#connectionState} accordingly.
         */
        public void connect() {
            pusher.connect(connectionEventListener, ConnectionState.ALL);
        }

        private ConnectionEventListener connectionEventListener = new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                connectionState = change.getCurrentState();
                switch (change.getCurrentState()) {
                    case CONNECTED:
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onError(String message, String code, Exception e) {

            }
        };

        /**
         * Subscribes Pusher connection to the channel's channel name.
         * Listens for events and dispatches them accordingly.
         */
        public void subscribe(@NonNull String channelName, @NonNull String expiry, @NonNull String sessionId) {
            this.channelName = channelName;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                this.expiry = sdf.parse(expiry);
            } catch (ParseException e) {
                Log.e("AmbassadorSDK", e.toString());
            }

            this.sessionId = sessionId;

            pusher.subscribePrivate(channelName, privateChannelEventListener);
        }

        private PrivateChannelEventListener privateChannelEventListener = new PrivateChannelEventListener() {
            @Override
            public void onAuthenticationFailure(String message, Exception e) {

            }

            @Override
            public void onSubscriptionSucceeded(String channelName) {

            }

            @Override
            public void onEvent(String channelName, String eventName, String data) {

            }
        };

        /**
         * Un-subscribes from a Pusher channel.
         * @param channelName String for name of channel to disconnect from.
         */
        public void unsubscribe(String channelName) {
            pusher.unsubscribe(channelName);
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

}
