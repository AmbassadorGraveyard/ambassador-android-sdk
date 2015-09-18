package com.example.ambassador.ambassadorsdk;

import dagger.Module;
import dagger.Provides;

/**
 * Created by coreyfields on 9/14/15.
 */
@Module
public class TweetRequestModule {
    @Provides
    //@Singleton
    //Can't be a singleton because you can't run execute() on the same AsyncTask object more than once
    //this way we'll get a new one each time it's injected
    TweetRequest provideTweetRequest() {
        return new TweetRequest();
    }
}
