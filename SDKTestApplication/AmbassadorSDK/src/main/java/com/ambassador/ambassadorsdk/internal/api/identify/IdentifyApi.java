package com.ambassador.ambassadorsdk.internal.api.identify;

import android.util.Log;

import com.ambassador.ambassadorsdk.internal.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.ServiceGenerator;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 *
 */
public class IdentifyApi {

    /** */
    private static IdentifyClient identifyClient;

    /**
     *
     */
    public static void init() {
        identifyClient = ServiceGenerator.createService(IdentifyClient.class);
    }

    /**
     *
     * @param completion
     */
    public static void createPusherChannel(final RequestManager.RequestCompletion completion) {
        identifyClient.createPusherChannel(null, new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                Log.v("TAG", "TAG");
                completion.onSuccess("success");
            }

            @Override
            public void failure(RetrofitError error) {
                Log.v("TAG", "TAG");
                completion.onFailure("failure");
            }
        });
    }

}
