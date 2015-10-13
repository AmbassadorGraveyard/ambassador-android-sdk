package com.ambassador.ambassadorsdk;

import android.app.Activity;
import android.content.Context;

import com.facebook.share.widget.ShareDialog;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

/**
 * Created by coreyfields on 9/14/15.
 */
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
    ShareDialog provideFbShareDialog() {
        return new ShareDialog((Activity)context);
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
}
