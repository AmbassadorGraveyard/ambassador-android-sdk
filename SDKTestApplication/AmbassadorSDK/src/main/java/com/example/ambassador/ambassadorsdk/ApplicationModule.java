package com.example.ambassador.ambassadorsdk;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by coreyfields on 9/14/15.
 */
@Module
public class ApplicationModule {
    @Provides
    @Singleton
    TweetRequest provideTweetRequest() {
        return new TweetRequest();
    }

    @Provides
    @Singleton
    LinkedInRequest provideLinkedInRequest() {
        return new LinkedInRequest();
    }
}
