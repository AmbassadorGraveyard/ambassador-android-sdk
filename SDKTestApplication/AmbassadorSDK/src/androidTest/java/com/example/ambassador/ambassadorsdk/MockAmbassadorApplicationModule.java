package com.example.ambassador.ambassadorsdk;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

/**
 * Created by coreyfields on 9/14/15.
 */
@Module
public class MockAmbassadorApplicationModule {
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
}
