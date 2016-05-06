package com.ambassador.ambassadorsdk.internal.identify;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.pusher.PusherListenerAdapter;
import com.ambassador.ambassadorsdk.internal.data.User;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AmbIdentify {

    @Inject protected User user;
    @Inject protected RequestManager requestManager;
    @Inject protected PusherManager pusherManager;

    protected String emailAddress;
    protected AmbIdentifyTask[] identifyTasks;

    public AmbIdentify(String emailAddress) {
        AmbSingleton.inject(this);
        this.emailAddress = emailAddress;
        this.identifyTasks = new AmbIdentifyTask[2];
        this.identifyTasks[0] = new AmbGcmTokenTask();
        this.identifyTasks[1] = new AmbAugurTask();

    }

    public void execute() {
        user.setEmail(emailAddress);
        final List<AmbIdentifyTask> identifyTasksList = new ArrayList<>();
        for (AmbIdentifyTask task : identifyTasks) {
            identifyTasksList.add(task);
        }

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

    protected void onPreExecutionComplete() {
        pusherManager.addPusherListener(new PusherListenerAdapter() {

            @Override
            public void subscribed() {
                super.subscribed();
                requestManager.identifyRequest(new RequestManager.RequestCompletion() {
                    @Override
                    public void onSuccess(Object successResponse) {

                    }

                    @Override
                    public void onFailure(Object failureResponse) {

                    }
                });
            }

            @Override
            public void onEvent(String data) {
                super.onEvent(data);
            }

        });

        pusherManager.startNewChannel();
        pusherManager.subscribeChannelToAmbassador();
    }

}
