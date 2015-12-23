package com.ambassador.ambassadorsdk.internal;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;

public class AmbassadorSingleton {

    private static AmbassadorSingleton instance;

    private Context appContext;
    private AmbassadorApplicationComponent component = null;
    public AmbassadorApplicationModule amb;

    @Singleton
    @Component(modules=AmbassadorApplicationModule.class)
    public interface ApplicationComponent extends AmbassadorApplicationComponent {
        //dummy component which will not override anything from parent interface
        //the testing component will provide its own overrides to inject into the tests
    }

    public void init(Context context) {
        if (appContext == null) {
            setAppContext(context);
        }

        if (component == null) {
            amb = new AmbassadorApplicationModule();
            ApplicationComponent component = DaggerAmbassadorSingleton_ApplicationComponent.builder().ambassadorApplicationModule(amb).build();
            setComponent(component);
        }
    }

    void setAppContext(Context context) {
        this.appContext = context;
    }

    public void setComponent(AmbassadorApplicationComponent comp) {
        this.component = comp;
    }

    public static AmbassadorApplicationComponent getComponent() {
        return getInstance().component;
    }

    public static AmbassadorApplicationModule getAmbModule() {
        return getInstance().amb;
    }

    public static void setAmbModule(AmbassadorApplicationModule ambModule) {
        getInstance().amb = ambModule;
    }

    private Context getContext() {
        return appContext;
    }

    public static Context get() {
        return getInstance().getContext();
    }

    public static boolean isValid() {
        return getInstance().component != null && getInstance().amb != null;
    }

    public static AmbassadorSingleton getInstance() {
        return instance == null ? (instance = new AmbassadorSingleton()) : instance;
    }

}
