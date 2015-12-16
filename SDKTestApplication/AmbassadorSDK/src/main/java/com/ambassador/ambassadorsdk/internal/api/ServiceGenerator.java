package com.ambassador.ambassadorsdk.internal.api;

import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;

/**
 * Class containing static method createService which will generate service implementations
 * for working with different APIs.
 */
public class ServiceGenerator {

    /**
     * Generates and returns an object that implements one of the API service interfaces and handles
     * all method implementations for handling Http behind the scenes using Retrofit.
     * @param serviceClass the api interface to implement
     * @return an interface implementation that handles using the API
     * @throws NoEndpointFoundException in the case that the service class does not contain String ENDPOINT
     */
    public static <S> S createService(Class<S> serviceClass) throws NoEndpointFoundException {
        String endpoint = extractEndpoint(serviceClass);

        if (!endpoint.equals("")) {
            OkHttpClient client = new OkHttpClient();
            RestAdapter.Builder builder = new RestAdapter.Builder()
                    .setEndpoint(endpoint)
                    .setClient(new OkClient(client));

            if (!AmbassadorConfig.isReleaseBuild) {
                builder.setLogLevel(RestAdapter.LogLevel.FULL).setLog(new AndroidLog("amb-retrofit"));
            }

            RestAdapter adapter = builder.build();
            return adapter.create(serviceClass);
        }

        throw new NoEndpointFoundException("Your ENDPOINT is invalid.");
    }

    /**
     * Extracts the String endpoint from the interface using reflection.
     * @param serviceClass the class to get the ENDPOINT field from
     * @return the String value of the field
     * @throws NoEndpointFoundException when the field is not declared in serviceClass
     */
    static String extractEndpoint(Class<?> serviceClass) throws NoEndpointFoundException {
        try {
           return (String) serviceClass.getField("ENDPOINT").get(null);
        } catch (Exception e) {
            throw new NoEndpointFoundException();
        }
    }

    /**
     * Exception for when no ENDPOINT is declared in the service interface.
     */
    public static class NoEndpointFoundException extends RuntimeException {

        public NoEndpointFoundException() {
            super("You must declare String ENDPOINT in your client.");
        }

        public NoEndpointFoundException(String detailMessage) {
            super(detailMessage);
        }

    }

}
