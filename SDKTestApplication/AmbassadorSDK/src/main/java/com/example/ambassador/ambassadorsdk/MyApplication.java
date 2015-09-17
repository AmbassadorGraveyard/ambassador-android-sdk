package com.example.ambassador.ambassadorsdk;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by JakeDunahee on 7/31/15.
 */
public class MyApplication extends Application {
    private static Context context;

    @Singleton
    @Component(modules=TweetRequestModule.class)
    public interface ApplicationComponent extends AmbassadorSDKComponent {
    }

    private static AmbassadorSDKComponent component = null;

    public void setComponent(AmbassadorSDKComponent comp) {
        component = comp;
    }

    public static AmbassadorSDKComponent component() {
        return component;
    }

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();

        if (component == null) {
            component = DaggerMyApplication_ApplicationComponent.builder().tweetRequestModule(new TweetRequestModule()).build();
        }
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
