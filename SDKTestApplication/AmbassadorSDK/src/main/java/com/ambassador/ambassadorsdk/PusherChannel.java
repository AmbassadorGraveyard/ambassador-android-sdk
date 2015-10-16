package com.ambassador.ambassadorsdk;

import java.util.Date;

/**
 * Created by coreyfields on 10/15/15.
 */
public class PusherChannel {
    private static String sessionId;
    private static String channelName;
    private static Date expiresAt;
    private static long requestId;

    static void setSessionId(String id) {
        sessionId = id;
    }

    static void setChannelName(String name) {
        channelName = name;
    }
    
    static void setExpiresAt(Date expires) {
        expiresAt = expires;
    }

    static void setRequestId(long rId) {
        requestId = rId;
    }

    static String getSessionId() {
        return sessionId;
    }
    
    static String getchannelName() {
        return channelName;
    }
    
    static Date getExpiresAt() {
        return expiresAt;
    }

    static long getRequestId() {
        return requestId;
    }

    static Boolean isExpired() {
        return expiresAt.getTime() < System.currentTimeMillis();
    }
}
