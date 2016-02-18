package com.ambassador.ambassadorsdk;

import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.utils.Device;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
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
