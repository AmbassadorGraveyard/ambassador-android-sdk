package com.example.ambassador.ambassadorsdk;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

/**
 * Created by coreyfields on 9/14/15.
 */
@Module
public class MockTweetRequestModule {
    @Provides
    @Singleton
    TweetRequest provideTweetRequest() {
        return mock(TweetRequest.class);
    }
}
