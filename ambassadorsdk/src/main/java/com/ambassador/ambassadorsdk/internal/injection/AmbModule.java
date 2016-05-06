package com.ambassador.ambassadorsdk.internal.injection;

import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.activities.ambassador.AmbassadorActivity;
import com.ambassador.ambassadorsdk.internal.activities.contacts.ContactSelectorActivity;
import com.ambassador.ambassadorsdk.internal.activities.oauth.SocialOAuthActivity;
import com.ambassador.ambassadorsdk.internal.adapters.ContactListAdapter;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.dialogs.AskEmailDialog;
import com.ambassador.ambassadorsdk.internal.dialogs.AskNameDialog;
import com.ambassador.ambassadorsdk.internal.dialogs.SocialShareDialog;
import com.ambassador.ambassadorsdk.internal.identify.AmbAugurTask;
import com.ambassador.ambassadorsdk.internal.identify.AmbGcmTokenTask;
import com.ambassador.ambassadorsdk.internal.identify.AmbIdentify;
import com.ambassador.ambassadorsdk.internal.identify.AmbIdentifyTask;
import com.ambassador.ambassadorsdk.internal.notifications.InstanceIdListener;
import com.ambassador.ambassadorsdk.internal.utils.Device;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
        AmbassadorActivity.class,
        SocialShareDialog.class,
        SocialOAuthActivity.class,
        ContactSelectorActivity.class,
        ContactListAdapter.class,
        BulkShareHelper.class,
        ConversionUtility.class,
        RequestManager.class,
        AmbassadorSDK.class,
        AskNameDialog.class,
        AskEmailDialog.class,
        PusherManager.class,
        PusherManager.Channel.class,
        InstallReceiver.class,
        InstanceIdListener.class,
        AmbIdentify.class,
        AmbIdentifyTask.class,
        AmbGcmTokenTask.class,
        AmbAugurTask.class,
}, staticInjections = {
        AmbassadorSDK.class
}, library = true)
public final class AmbModule {

    protected RequestManager requestManager;
    protected PusherManager pusherManager;
    protected BulkShareHelper bulkShareHelper;

    public void init() {
        requestManager = new RequestManager();
        pusherManager = new PusherManager();
        bulkShareHelper = new BulkShareHelper();

        AmbSingleton.inject(requestManager);
        AmbSingleton.inject(pusherManager);
        AmbSingleton.inject(bulkShareHelper);
    }

    @NonNull
    @Provides
    public RequestManager provideRequestManager() {
        return requestManager;
    }

    @NonNull
    @Provides
    public BulkShareHelper provideBulkShareHelper() {
        return bulkShareHelper;
    }

    @NonNull
    @Provides
    public PusherManager providePusherManager() {
        return pusherManager;
    }

    @NonNull
    @Provides
    @Singleton
    public ConversionUtility provideConversionUtility() {
        return new ConversionUtility();
    }

    @NonNull
    @Provides
    @Singleton
    public Device provideDevice() {
        return new Device();
    }

    @NonNull
    @Provides
    @Singleton
    public Campaign provideCampaign() {
        return new Campaign();
    }

    @NonNull
    @Provides
    @Singleton
    public User provideUser() {
        return new User();
    }

    @NonNull
    @Provides
    @Singleton
    public Auth provideAuth() {
        return new Auth();
    }

    @NonNull
    @Provides
    @Singleton
    public RAFOptions provideRAFOptions() {
        return RAFOptions.get();
    }

}
