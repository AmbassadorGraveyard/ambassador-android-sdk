package com.ambassador.ambassadorsdk.internal.injection;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.activities.ambassador.AmbassadorActivity;
import com.ambassador.ambassadorsdk.internal.activities.contacts.AskNameActivity;
import com.ambassador.ambassadorsdk.internal.activities.contacts.ContactSelectorActivity;
import com.ambassador.ambassadorsdk.internal.activities.oauth.SocialOAuthActivity;
import com.ambassador.ambassadorsdk.internal.adapters.ContactListAdapter;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.conversion.AmbConversion;
import com.ambassador.ambassadorsdk.internal.dialogs.AskEmailDialog;
import com.ambassador.ambassadorsdk.internal.dialogs.AskNameDialog;
import com.ambassador.ambassadorsdk.internal.dialogs.SocialShareDialog;
import com.ambassador.ambassadorsdk.internal.identify.AmbIdentify;
import com.ambassador.ambassadorsdk.internal.identify.tasks.AmbAugurTask;
import com.ambassador.ambassadorsdk.internal.identify.tasks.AmbIdentifyTask;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AmbModule.class})
public interface AmbComponent {
    void inject(AmbassadorActivity ambassadorActivity);
    void inject(SocialShareDialog socialShareDialog);
    void inject(SocialOAuthActivity socialOAuthActivity);
    void inject(ContactSelectorActivity contactSelectorActivity);
    void inject(ContactListAdapter contactListAdapter);
    void inject(BulkShareHelper bulkShareHelper);
    void inject(RequestManager requestManager);
    void inject(AmbassadorSDK ambassadorSDK);
    void inject(AmbSingleton ambSingleton);
    void inject(AskNameDialog askNameDialog);
    void inject(AskEmailDialog askEmailDialog);
    void inject(PusherManager PusherManager);
    void inject(PusherManager.Channel pusherManagerChannel);
    void inject(InstallReceiver installReceiver);
    void inject(AmbIdentify ambIdentify);
    void inject(AmbIdentifyTask ambIdentifyTask);
    void inject(AmbAugurTask ambAugurTask);
    void inject(AskNameActivity askNameActivity);
    void inject(AmbConversion ambConversion);
}
