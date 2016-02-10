package com.ambassador.ambassadorsdk;

import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.PusherSDK;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.utils.Device;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
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
    public AmbassadorConfig provideAmbassadorConfig() {
        return Mockito.mock(AmbassadorConfig.class);
    }

    @NonNull
    @Provides
    @Singleton
    public PusherSDK providePusherSDK() {
        return Mockito.mock(PusherSDK.class);
    }

    @NonNull
    @Provides
    @Singleton
    public Device provideDevice() {
        return Mockito.mock(Device.class);
    }

    @NonNull
    @Provides
    public RAFOptions provideRAFOptions() {
        return RAFOptions.get();
    }

}
