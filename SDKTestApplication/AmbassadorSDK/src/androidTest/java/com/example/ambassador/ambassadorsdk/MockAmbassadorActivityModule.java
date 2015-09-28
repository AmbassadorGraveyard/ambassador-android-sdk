package com.example.ambassador.ambassadorsdk;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

/**
 * Created by coreyfields on 9/14/15.
 */
@Module
public class MockAmbassadorActivityModule {
/*    private final Context context;

    public MockAmbassadorActivityModule(Context context) {
        this.context = context;
    }*/

    @Provides
    @Singleton
    TweetRequest provideTweetRequest() {
        return mock(TweetRequest.class);
    }

    @Provides
    @Singleton
    LinkedInRequest provideLinkedInRequest() {
        return mock(LinkedInRequest.class);
    }

    @Provides
    @Singleton
    IdentifyRequest provideIdentifyRequest() {
        return mock(IdentifyRequest.class);
    }

/*    @Provides
    @Singleton
    @ForActivity
    Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    ShareDialog provideFbShareDialog() {
        return new ShareDialog((Activity)context);
    }*/
}
