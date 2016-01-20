package com.ambassador.ambassadorsdk.internal;

import android.content.Context;

import com.ambassador.ambassadorsdk.internal.utils.Device;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class AmbassadorApplicationModule {

    private Context context;
    private Boolean mockMode = false;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setMockMode(Boolean mockMode) {
        this.mockMode = mockMode;
    }

    @Provides
    @Singleton
    RequestManager provideRequestManager() {
        if (mockMode) return mock(RequestManager.class);
        return new RequestManager();
    }

    @Provides
    @Singleton
    @ForActivity
    Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    BulkShareHelper provideBulkShareHelper() {
        if (mockMode) return mock(BulkShareHelper.class);
        return new BulkShareHelper();
    }

    @Provides
    @Singleton
    AmbassadorConfig provideAmbassadorConfig() {
        if (mockMode) return mock(AmbassadorConfig.class);
        return new AmbassadorConfig();
    }

    @Provides
    @Singleton
    PusherSDK providePusherSDK() {
        if (mockMode) return mock(PusherSDK.class);
        return new PusherSDK();
    }

    @Provides
    @Singleton
    Device provideDevice() {
        return new Device();
    }

}
