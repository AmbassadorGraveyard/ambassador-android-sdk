package com.ambassador.ambassadorsdk.internal;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.util.HttpAuthorizer;
import com.squareup.otto.Bus;

import java.util.HashMap;

import javax.inject.Inject;

public class Pusher2 {

    private Pusher pusher;

    @Inject protected Bus bus;
    @Inject protected AmbassadorConfig ambassadorConfig;

    public Pusher2() {
        AmbassadorSingleton.getInstanceComponent().inject(this);
        pusher = setupPusher();
    }

    private Pusher setupPusher() {
        HttpAuthorizer authorizer = new HttpAuthorizer(AmbassadorConfig.pusherCallbackURL());

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", ambassadorConfig.getUniversalKey());
        authorizer.setHeaders(headers);

        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put("auth_type", "private");
        queryParams.put("channel", PusherChannel.getChannelName());
        authorizer.setQueryStringParameters(queryParams);

        PusherOptions options = new PusherOptions();
        options.setAuthorizer(authorizer);
        options.setEncrypted(true);

        String key = AmbassadorConfig.isReleaseBuild ? AmbassadorConfig.PUSHER_KEY_PROD : AmbassadorConfig.PUSHER_KEY_DEV;

        return new Pusher(key, options);
    }

}
