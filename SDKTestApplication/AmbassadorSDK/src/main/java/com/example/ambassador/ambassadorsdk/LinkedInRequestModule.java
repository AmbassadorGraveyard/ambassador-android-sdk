package com.example.ambassador.ambassadorsdk;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by coreyfields on 9/21/15.
 */
@Module
public class LinkedInRequestModule {
    @Provides
    @Singleton
    LinkedInRequest provideLinkedInRequest() {
        return new LinkedInRequest();
    }
}
