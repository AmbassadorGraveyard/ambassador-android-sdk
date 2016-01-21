package com.ambassador.ambassadorsdk.injection;

import android.content.Context;

import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.PusherSDK;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.utils.Device;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class AmbassadorApplicationModule {

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public RequestManager provideRequestManager() {
        return new RequestManager();
    }

    @Provides
    @Singleton
    @ForActivity
    public Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    public BulkShareHelper provideBulkShareHelper() {
        return new BulkShareHelper();
    }

    @Provides
    @Singleton
    public AmbassadorConfig provideAmbassadorConfig() {
        return new AmbassadorConfig();
    }

    @Provides
    @Singleton
    public PusherSDK providePusherSDK() {
        return new PusherSDK();
    }

    @Provides
    @Singleton
    public Device provideDevice() {
        return new Device();
    }

}
