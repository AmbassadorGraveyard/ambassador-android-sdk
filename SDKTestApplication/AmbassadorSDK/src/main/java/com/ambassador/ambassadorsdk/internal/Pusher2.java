package com.ambassador.ambassadorsdk.internal;

import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;
import com.squareup.otto.Bus;

import java.util.Date;
import java.util.HashMap;

import javax.inject.Inject;

/**
 * Handles everything to do with Pusher, our socket to the backend.
 * Keeps track of a single channel and does the connecting, subscribing, disposing of it all.
 * Handles incoming events and dispatches them on the otto event bus after processing.
 */
public class Pusher2 {

    protected static String universalKey;

    protected Channel channel;

    @Inject protected Bus bus;
    @Inject protected AmbassadorConfig ambassadorConfig;
    @Inject protected RequestManager requestManager;

    /**
     * Default constructor handling injection and event bus registration.
     */
    public Pusher2() {
        AmbassadorSingleton.getInstanceComponent().inject(this);
        bus.register(this);
        Pusher2.universalKey = ambassadorConfig.getUniversalKey();
    }

    /**
     * Creates a new pusher channel and connects.
     * Will re-use the old Pusher connection if open but not subscription to backend.
     * If there is an existing subscription it will dispose of it.
     */
    public void connectToNewChannel() {
        if (channel != null) channel.disconnect();
        channel = null;
        requestManager.createPusherChannel(new RequestManager.RequestCompletion() {
            @Override
            public void onSuccess(Object successResponse) {

            }

            @Override
            public void onFailure(Object failureResponse) {

            }
        });
    }

    private void setNewConnection() {

    }

    /**
     * Handles a single connection to Pusher.  Keeps track of a channel name and session id.
     * Connects and subscribes with this information and receives events, which are pushed back
     * to parent.
     */
    protected static final class Channel {

        protected Pusher pusher;

        protected String sessionId;
        protected String channelName;
        protected Date expiry;
        protected long requestId;
        protected ConnectionState connectionState;

        private Channel() {}

        public void init() {
            this.pusher = setupPusher();
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
            }

            @Override
            public void onError(String message, String code, Exception e) {

            }
        };

        /**
         * Subscribes Pusher connection to the channel's channel name.
         * Listens for events and dispatches on the {@link Pusher2#bus} accordingly.
         */
        public void subscribe() {
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
