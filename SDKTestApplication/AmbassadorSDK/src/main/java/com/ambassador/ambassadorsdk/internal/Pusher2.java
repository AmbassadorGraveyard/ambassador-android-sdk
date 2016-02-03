package com.ambassador.ambassadorsdk.internal;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.identify.IdentifyApi;
import com.ambassador.ambassadorsdk.internal.events.AmbassaBus;
import com.ambassador.ambassadorsdk.internal.events.IdentifyEvent;
import com.ambassador.ambassadorsdk.internal.events.PusherConnectedEvent;
import com.ambassador.ambassadorsdk.internal.events.PusherSubscribedEvent;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;
import com.squareup.otto.Subscribe;

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
public class Pusher2 {

    protected static String universalKey;

    protected Channel channel;

    @Inject protected AmbassaBus ambassaBus;
    @Inject protected AmbassadorConfig ambassadorConfig;
    @Inject protected RequestManager requestManager;

    /**
     * Default constructor handling injection and event ambassaBus registration.
     */
    public Pusher2() {
        AmbassadorSingleton.getInstanceComponent().inject(this);
        ambassaBus.register(this);
        Pusher2.universalKey = ambassadorConfig.getUniversalKey();
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
     * Hit when Pusher makes a connection to the Pusher backend.
     */
    @Subscribe
    public void pusherConnected(PusherConnectedEvent pusherConnectedEvent) {

    }

    /**
     * Handles a single connection to Pusher.  Keeps track of a channel name and session id.
     * Connects and subscribes with this information and receives events, which are pushed back
     * to parent.
     */
    public static final class Channel {

        @Inject protected AmbassaBus ambassaBus;

        protected Pusher pusher;

        protected String sessionId;
        protected String channelName;
        protected Date expiry;
        protected long requestId;
        protected ConnectionState connectionState;

        private Channel() {}

        public void init() {
            AmbassadorSingleton.getInstanceComponent().inject(this);
            this.pusher = setupPusher();
            ambassaBus.register(this);
        }

        /**
         * Sets up a Pusher object from the Pusher Android SDK.
         * @return a Pusher object configured with the channel name from Ambassador.
         */
        private Pusher setupPusher() {
            HttpAuthorizer authorizer = new HttpAuthorizer(AmbassadorConfig.pusherCallbackURL());

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

            String key = AmbassadorConfig.isReleaseBuild ? AmbassadorConfig.PUSHER_KEY_PROD : AmbassadorConfig.PUSHER_KEY_DEV;

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
                        ambassaBus.post(new PusherConnectedEvent());
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
         * Listens for events and dispatches on the {@link Pusher2#ambassaBus} accordingly.
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
                Log.v("TAG", "TAG");
            }

            @Override
            public void onSubscriptionSucceeded(String channelName) {
                ambassaBus.post(new PusherSubscribedEvent());
            }

            @Override
            public void onEvent(String channelName, String eventName, String data) {
                ambassaBus.post(new IdentifyEvent());
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
