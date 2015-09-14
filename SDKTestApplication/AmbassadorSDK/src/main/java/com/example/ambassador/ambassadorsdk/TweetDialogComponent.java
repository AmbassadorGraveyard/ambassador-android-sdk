package com.example.ambassador.ambassadorsdk;

/**
 * Created by coreyfields on 9/14/15.
 */

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {TweetDialogModule.class})
public interface TweetDialogComponent {
    TweetDialog provideTweetDialog();
}