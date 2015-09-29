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
    private static AmbassadorApplicationComponent component = null;

    @Singleton
    @Component(modules=AmbassadorApplicationModule.class)
    public interface ApplicationComponent extends AmbassadorApplicationComponent {
        //dummy component which will not override anything from parent interface
        //the testing component will provide its own overrides to inject into the tests
    }

    public static AmbassadorApplicationComponent getComponent() {
        return component;
    }

    public static void setComponent(AmbassadorApplicationComponent comp) {
        component = comp;
    }

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        //get injected modules we need
        if (component == null) {
            ApplicationComponent component = DaggerMyApplication_ApplicationComponent.builder().ambassadorApplicationModule(new AmbassadorApplicationModule()).build();
            MyApplication.setComponent(component);
        }
    }

    public static Context getAppContext() {
        return context;
    }
}
