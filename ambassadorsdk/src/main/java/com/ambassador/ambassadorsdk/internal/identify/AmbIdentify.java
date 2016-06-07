package com.ambassador.ambassadorsdk.internal.identify;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.pusher.PusherListenerAdapter;
import com.ambassador.ambassadorsdk.internal.conversion.AmbConversion;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.identify.tasks.AmbAugurTask;
import com.ambassador.ambassadorsdk.internal.identify.tasks.AmbGcmTokenTask;
import com.ambassador.ambassadorsdk.internal.identify.tasks.AmbIdentifyTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class AmbIdentify {

    protected static AmbIdentify runningInstance;

    @Inject protected User user;
    @Inject protected RequestManager requestManager;
    protected PusherManager pusherManager;
    protected String userId;
    protected AmbassadorIdentification ambassadorIdentification;
    protected AmbIdentifyTask[] identifyTasks;
    protected CompletionListener completionListener;
    protected boolean subscribed;
    protected String memberIdentifyType;

    public static String identifyType;

    protected AmbIdentify(String userId, AmbassadorIdentification ambassadorIdentification) {
        AmbSingleton.inject(this);
        this.userId = userId;
        this.ambassadorIdentification = ambassadorIdentification;
        this.identifyTasks = new AmbIdentifyTask[2];
        this.identifyTasks[0] = new AmbGcmTokenTask();
        this.identifyTasks[1] = new AmbAugurTask();
        this.memberIdentifyType = identifyType;
        identifyType = "";

        this.pusherManager = new PusherManager();
    }

    public void execute() {
        runningInstance = this;

        setupPusher();

        user.clear();
        user.setUserId(userId);
        user.setAmbassadorIdentification(ambassadorIdentification);
        final List<AmbIdentifyTask> identifyTasksList = new ArrayList<>();
        Collections.addAll(identifyTasksList, identifyTasks);

        for (final AmbIdentifyTask task : identifyTasks) {
            try {
                task.execute(new AmbIdentifyTask.OnCompleteListener() {
                    @Override
                    public void complete() {
                        identifyTasksList.remove(task);
                        if (identifyTasksList.isEmpty()) {
                            onPreExecutionComplete();
                        }
                    }
                });
            } catch (Exception e) {
                if (identifyTasksList.contains(task)) {
                    identifyTasksList.remove(task);
                    if (identifyTasksList.isEmpty()) {
                        onPreExecutionComplete();
                    }
                }
            }
        }
    }

    protected void setupPusher() {
        pusherManager.addPusherListener(new PusherListenerAdapter() {

            @Override
            public void connectionFailed() {
                super.connectionFailed();
                completionListener.networkError();
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

    protected void onPreExecutionComplete() {
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

    protected void performIdentifyRequest() {
        requestManager.identifyRequest(pusherManager, new RequestManager.RequestCompletion() {
            @Override
            public void onSuccess(Object successResponse) {
                AmbConversion.attemptExecutePending();
            }

            @Override
            public void onFailure(Object failureResponse) {
                // Not handled here.
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
