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
import com.ambassador.ambassadorsdk.internal.adapters.SocialGridAdapter;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.api.bulkshare.BulkShareApi;
import com.ambassador.ambassadorsdk.internal.api.conversions.ConversionsApi;
import com.ambassador.ambassadorsdk.internal.api.identify.IdentifyApi;
import com.ambassador.ambassadorsdk.internal.conversion.AmbConversion;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.dialogs.AskEmailDialog;
import com.ambassador.ambassadorsdk.internal.dialogs.AskNameDialog;
import com.ambassador.ambassadorsdk.internal.dialogs.ContactInfoDialog;
import com.ambassador.ambassadorsdk.internal.dialogs.SocialShareDialog;
import com.ambassador.ambassadorsdk.internal.identify.AmbIdentify;
import com.ambassador.ambassadorsdk.internal.views.PermissionView;

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
    void inject(ContactListAdapter.ContactViewHolder contactViewHolder);
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
    void inject(AskNameActivity askNameActivity);
    void inject(AmbConversion ambConversion);
    void inject(SocialGridAdapter socialGridAdapter);
    void inject(IdentifyApi identifyApi);
    void inject(IdentifyApi.IdentifyRequestBody identifyRequestBody);
    void inject(PermissionView permissionView);
    void inject(ContactInfoDialog contactInfoDialog);
    void inject(BulkShareApi bulkShareApi);
    void inject(ConversionsApi conversionsApi);
    Auth provideAuth();
}
