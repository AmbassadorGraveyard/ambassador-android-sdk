package com.example.ambassador.ambassadorsdk;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by coreyfields on 10/7/15.
 */
public class ApplicationContext {
    private Context appContext;
    private static AmbassadorApplicationComponent component = null;
    public static AmbassadorApplicationModule amb;

    @Singleton
    @Component(modules=AmbassadorApplicationModule.class)
    public interface ApplicationComponent extends AmbassadorApplicationComponent {
        //dummy component which will not override anything from parent interface
        //the testing component will provide its own overrides to inject into the tests
    }

    public void init(Context context) {
        if (appContext == null) {
            appContext = context;
        }

        //get injected modules we need
        if (component == null) {
            amb = new AmbassadorApplicationModule();
            ApplicationComponent component = DaggerApplicationContext_ApplicationComponent.builder().ambassadorApplicationModule(amb).build();
            setComponent(component);
        }
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

    private Context getContext() {
        return appContext;
    }

    public static Context get() {
        return getInstance().getContext();
    }

    private static ApplicationContext instance;

    public static ApplicationContext getInstance() {
        return instance == null ? (instance = new ApplicationContext()) : instance;
    }
}
