package com.ambassador.demo;

import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.IdentifyAugurSDK;
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
import com.ambassador.ambassadorsdk.internal.notifications.InstanceIdListener;
import com.ambassador.ambassadorsdk.internal.utils.Device;
import com.ambassador.demo.ambassadorsdk.AmbassadorActivityTest;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
        MainActivityTest.class,
        LaunchActivityTest.class,
        CustomizationActivityTest.class,
        AmbassadorActivityTest.class,
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
        IdentifyAugurSDK.class,
        PusherManager.class,
        PusherManager.Channel.class,
        InstallReceiver.class,
        InstanceIdListener.class,
}, staticInjections = {
        AmbassadorSDK.class
}, library = true)
public final class TestModule {

    @NonNull
    @Provides
    @Singleton
    public RequestManager provideRequestManager() {
        return Mockito.mock(RequestManager.class);
    }

    @NonNull
    @Provides
    @Singleton
    public BulkShareHelper provideBulkShareHelper() {
        return Mockito.mock(BulkShareHelper.class);
    }

    @NonNull
    @Provides
    @Singleton
    public PusherManager providePusherManager() {
        return Mockito.spy(new PusherManager());
    }

    @NonNull
    @Provides
    @Singleton
    public ConversionUtility provideConversionUtility() {
        return Mockito.mock(ConversionUtility.class);
    }

    @NonNull
    @Provides
    @Singleton
    public Device provideDevice() {
        return Mockito.mock(Device.class);
    }

    @NonNull
    @Provides
    @Singleton
    public Campaign provideCampaign() {
        return Mockito.spy(new Campaign());
    }

    @NonNull
    @Provides
    @Singleton
    public User provideUser() {
        return Mockito.spy(new User());
    }

    @NonNull
    @Provides
    @Singleton
    public Auth provideAuth() {
        return Mockito.spy(new Auth());
    }

    @NonNull
    @Provides
    @Singleton
    public RAFOptions provideRAFOptions() {
        return RAFOptions.get();
    }

}