package com.ambassador.ambassadorsdk.internal.identify;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.pusher.PusherListenerAdapter;
import com.ambassador.ambassadorsdk.internal.data.User;

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

    protected AmbIdentify(String userId, AmbassadorIdentification ambassadorIdentification) {
        AmbSingleton.inject(this);
        this.userId = userId;
        this.ambassadorIdentification = ambassadorIdentification;
        this.identifyTasks = new AmbIdentifyTask[2];
        this.identifyTasks[0] = new AmbGcmTokenTask();
        this.identifyTasks[1] = new AmbAugurTask();

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
            public void subscribed() {
                super.subscribed();
                subscribed = true;
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

        });

        if (subscribed) {
            requestManager.identifyRequest(pusherManager, null);
        } else {
            pusherManager.addPusherListener(new PusherListenerAdapter() {
                @Override
                public void subscribed() {
                    super.subscribed();
                    requestManager.identifyRequest(pusherManager, null);
                }
            });
        }
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
    }

    public static AmbIdentify get(String userId, AmbassadorIdentification ambassadorIdentification) {
        return new AmbIdentify(userId, ambassadorIdentification);
    }

}
