package com.example.ambassador.ambassadorsdk;


import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by coreyfields on 9/14/15.
 */
@Module
public class TweetDialogModule {
    private Context context;

    public TweetDialogModule(final Context context) {
        this.context = context;
    }

    @Provides @Singleton
    TweetRequest provideTweetRequest() {
        return new TweetRequest();
    }

    @Provides @Singleton
    TweetDialog provideTweetDialog() {
        return new TweetDialog(provideContext(), new TweetRequest());
    }

    @Provides @Singleton
    Context provideContext() {
        return context;
    }
}
