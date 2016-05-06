package com.ambassador.ambassadorsdk.internal.identify;

import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.pusher.PusherListenerAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AmbIdentify {

    @Inject protected PusherManager pusherManager;
    @Inject protected RequestManager requestManager;

    protected String emailAddress;
    protected List<AmbIdentifyTask> identifyTasks;

    public AmbIdentify(String emailAddress) {
        this.emailAddress = emailAddress;
        this.identifyTasks = new ArrayList<>();
        this.identifyTasks.add(new AmbGcmTokenTask());
        this.identifyTasks.add(new AmbAugurTask());
    }

    public void execute() {
        for (final AmbIdentifyTask task :identifyTasks) {
            try {
                task.execute(new AmbIdentifyTask.OnCompleteListener() {
                    @Override
                    public void complete() {
                        identifyTasks.remove(task);
                        if (identifyTasks.isEmpty()) {
                            onPreExecutionComplete();
                        }
                    }
                });
            } catch (Exception e) {
                if (identifyTasks.contains(task)) {
                    identifyTasks.remove(task);
                    if (identifyTasks.isEmpty()) {
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
                requestManager.identifyRequest(null);
            }
        });

        pusherManager.subscribeChannelToAmbassador();
    }

}
