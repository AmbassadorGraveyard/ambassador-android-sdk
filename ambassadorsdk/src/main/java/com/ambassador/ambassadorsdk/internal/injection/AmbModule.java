package com.ambassador.ambassadorsdk.internal.injection;

import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.activities.oauth.SocialOAuthActivity;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.utils.Device;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class AmbModule {
    @NonNull
    @Provides
    @Singleton
    public RequestManager provideRequestManager() {
        return new RequestManager();
    }

    @NonNull
    @Provides
    @Singleton
    public BulkShareHelper provideBulkShareHelper() {
        return new BulkShareHelper();
    }

    @NonNull
    @Provides
    @Singleton
    public PusherManager providePusherManager() {
        return new PusherManager();
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
        return new RAFOptions().get();
    }

    @NonNull
    @Provides
    @Singleton
    public SocialOAuthActivity provideSocialOAuthActivity() {
        return new SocialOAuthActivity();
    }

    @NonNull
    @Provides
    @Singleton
    public Utilities provideUtilities() {
        return new Utilities();
    }
}
