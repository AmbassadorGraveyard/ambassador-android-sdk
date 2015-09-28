package com.example.ambassador.ambassadorsdk;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by coreyfields on 9/14/15.
 */
@Module
public class AmbassadorActivityModule {
    //private final Context context;

/*
    public AmbassadorActivityModule(Context context) {
        this.context = context;
    }
*/

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

    @Provides
    @Singleton
    IdentifyRequest provideIdentifyRequest() {
        return new IdentifyRequest();
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
