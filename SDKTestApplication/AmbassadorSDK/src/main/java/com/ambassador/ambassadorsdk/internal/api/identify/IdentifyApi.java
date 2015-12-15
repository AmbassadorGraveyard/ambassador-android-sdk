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
     */
    public static void identifyRequest() {

    }

    /**
     *
     * @param email
     * @param firstName
     * @param lastName
     * @param completion
     */
    public static void updateNameRequest(String email, String firstName, String lastName, RequestManager.RequestCompletion completion) {

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

    /**
     * 
     * @param url
     * @param completion
     */
    public static void externalPusherRequest(String url, RequestManager.RequestCompletion completion) {

    }

}
