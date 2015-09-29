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
    public static AmbassadorApplicationModule amb;

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

    public static AmbassadorApplicationModule getAmbModule() {
        return amb;
    }

    public static void setAmbModule(AmbassadorApplicationModule ambModule) {
        amb = ambModule;
    }

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        //get injected modules we need
        if (component == null) {
            amb = new AmbassadorApplicationModule();
            ApplicationComponent component = DaggerMyApplication_ApplicationComponent.builder().ambassadorApplicationModule(amb).build();
            setComponent(component);
        }
    }

    public static Context getAppContext() {
        return context;
    }
}
