package com.ambassador.ambassadorsdk.internal.identify;

import android.content.Context;
import android.content.SharedPreferences;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.pusher.PusherListenerAdapter;
import com.ambassador.ambassadorsdk.internal.conversion.AmbConversion;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.inject.Inject;

public class AmbIdentify {
    @Inject protected User user;
    @Inject protected RequestManager requestManager;
    @Inject protected PusherManager pusherManager;

    protected static AmbIdentify runningInstance;
    protected String userId;
    protected AmbassadorIdentification ambassadorIdentification;
    protected CompletionListener completionListener;
    protected boolean subscribed;
    protected String memberIdentifyType;
    public static String identifyType = "";

    @Inject
    protected AmbIdentify(String userId, AmbassadorIdentification ambassadorIdentification) {
        AmbSingleton.getInstance().getAmbComponent().inject(this);

        this.userId = userId;
        this.ambassadorIdentification = ambassadorIdentification;
        this.memberIdentifyType = identifyType;
        identifyType = "";
    }

    public void execute() {
        runningInstance = this;

        setupPusher();
        user.setUserId(userId);
        user.setEmail(ambassadorIdentification.getEmail());
        user.setAmbassadorIdentification(ambassadorIdentification);

        pusherManager.addPusherListener(new PusherListenerAdapter() {

            @Override
            public void onIdentifyComplete() {
                super.onIdentifyComplete();
                runningInstance = null;
                if (completionListener != null) {
                    completionListener.complete();
                }
                pusherManager.disconnect();
            }

            @Override
            public void onIdentifyFailed() {
                super.onIdentifyFailed();
                runningInstance = null;
                if (completionListener != null) {
                    completionListener.networkError();
                }
                pusherManager.disconnect();
            }
        });

        if (subscribed) {
            performIdentifyRequest();
        } else {
            pusherManager.addPusherListener(new PusherListenerAdapter() {
                @Override
                public void subscribed() {
                    super.subscribed();
                    performIdentifyRequest();
                }

                @Override
                public void subscriptionFailed() {
                    super.subscriptionFailed();
                    runningInstance = null;
                    if (completionListener != null) {
                        completionListener.noSDK();
                    }
                }
            });
        }
    }

    protected void setupPusher() {
        pusherManager.addPusherListener(new PusherListenerAdapter() {

            @Override
            public void connectionFailed() {
                super.connectionFailed();
                if (completionListener != null) {
                    completionListener.networkError();
                }
            }

            @Override
            public void subscribed() {
                super.subscribed();
                subscribed = true;
            }

            @Override
            public void subscriptionFailed() {
                super.subscriptionFailed();
                runningInstance = null;
                if (completionListener != null) {
                    completionListener.noSDK();
                }
            }

        });

        pusherManager.startNewChannel();
        pusherManager.subscribeChannelToAmbassador();
    }

    protected void performIdentifyRequest() {
        requestManager.identifyRequest(memberIdentifyType, pusherManager, new RequestManager.RequestCompletion() {
            @Override
            public void onSuccess(Object successResponse) {
                SharedPreferences sharedPreferences = AmbSingleton.getInstance().getContext().getSharedPreferences("conversions", Context.MODE_PRIVATE);
                String content = sharedPreferences.getString("conversions", "[]");
                sharedPreferences.edit().putString("conversions", "[]").apply();
                final JsonArray conversions = new JsonParser().parse(content).getAsJsonArray();
                for (final JsonElement jsonElement : conversions) {
                    AmbConversion ambConversion = new Gson().fromJson(jsonElement, AmbConversion.class);
                    ambConversion.execute();
                }
            }

            @Override
            public void onFailure(Object failureResponse) {
            }
        });
    }

    public void setCompletionListener(CompletionListener completionListener) {
        this.completionListener = completionListener;
    }

    public static AmbIdentify getRunningInstance() {
        return runningInstance;
    }

    public void cancel() {
        runningInstance = null;
    }

    public interface CompletionListener {
        void complete();
        void noSDK();
        void networkError();
    }

    public static AmbIdentify get(String userId, AmbassadorIdentification ambassadorIdentification) {
        return new AmbIdentify(userId, ambassadorIdentification);
    }

}
