package com.ambassador.ambassadorsdk.internal;

import com.pusher.client.connection.ConnectionState;

import java.util.Date;

public class PusherChannel {
    private static String sessionId;
    private static String channelName;
    private static Date expiresAt;
    private static long requestId;
    private static ConnectionState connectionState;

    public static void setSessionId(String id) {
        sessionId = id;
    }

    public static void setChannelName(String name) {
        channelName = name;
    }
    
    public static void setExpiresAt(Date expires) {
        expiresAt = expires;
    }

    public static void setRequestId(long rId) {
        requestId = rId;
    }

    public static void setConnectionState(ConnectionState connState) {
        connectionState = connState;
    }

    public static String getSessionId() {
        return sessionId;
    }
    
    public static String getChannelName() {
        return channelName;
    }
    
    public static Date getExpiresAt() {
        return expiresAt;
    }

    public static long getRequestId() {
        return requestId;
    }

    public static Boolean isExpired() {
        return expiresAt.getTime() < System.currentTimeMillis();
    }

    public static ConnectionState getConnectionState() {
        return connectionState;
    }
}
