package com.ambassador.ambassadorsdk;

import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.ConversionUtility;
import com.ambassador.ambassadorsdk.internal.IdentifyAugurSDK;
import com.ambassador.ambassadorsdk.internal.InstallReceiver;
import com.ambassador.ambassadorsdk.internal.activities.AmbassadorActivity;
import com.ambassador.ambassadorsdk.internal.activities.ContactSelectorActivity;
import com.ambassador.ambassadorsdk.internal.activities.LinkedInLoginActivity;
import com.ambassador.ambassadorsdk.internal.activities.SocialOAuthActivity;
import com.ambassador.ambassadorsdk.internal.activities.TwitterLoginActivity;
import com.ambassador.ambassadorsdk.internal.adapters.ContactListAdapter;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.dialogs.AskNameDialog;
import com.ambassador.ambassadorsdk.internal.dialogs.SocialShareDialog;
import com.ambassador.ambassadorsdk.internal.notifications.InstanceIdListener;
import com.ambassador.ambassadorsdk.internal.utils.Device;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
        AmbassadorActivity.class,
        SocialShareDialog.class,
        LinkedInLoginActivity.class,
        TwitterLoginActivity.class,
        SocialOAuthActivity.class,
        ContactSelectorActivity.class,
        ContactListAdapter.class,
        BulkShareHelper.class,
        ConversionUtility.class,
        RequestManager.class,
        AmbassadorSDK.class,
        AskNameDialog.class,
        IdentifyAugurSDK.class,
        PusherManager.class,
        PusherManager.Channel.class,
        InstallReceiver.class,
        InstanceIdListener.class,
        AmbassadorActivityTest.class
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
        return Mockito.mock(PusherManager.class);
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
