package com.ambassador.ambassadorsdk.internal;

import android.content.Context;

import com.ambassador.ambassadorsdk.injection.AmbassadorApplicationComponent;
import com.ambassador.ambassadorsdk.injection.AmbassadorApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

public class AmbassadorSingleton {

    private static AmbassadorSingleton instance;

    private Context context;
    private AmbassadorApplicationComponent component;
    public AmbassadorApplicationModule amb;

    @Singleton
    @Component(modules=AmbassadorApplicationModule.class)
    public interface ApplicationComponent extends AmbassadorApplicationComponent {
        // Dummy component which will not override anything from parent interface.
        // The testing component will provide its own overrides to inject into the tests.
    }

    public static void init(Context context) {
        if (getInstanceContext() == null) {
            setInstanceContext(context);
        }

        if (getInstanceComponent() == null) {
            setInstanceAmbModule(buildAmbassadorApplicationModule());
            ApplicationComponent component = DaggerAmbassadorSingleton_ApplicationComponent.builder().ambassadorApplicationModule(getInstanceAmbModule()).build();
            setInstanceComponent(component);
        }
    }

    static AmbassadorApplicationModule buildAmbassadorApplicationModule() {
        return new AmbassadorApplicationModule();
    }

    private void setContext(Context context) {
        this.context = context;
    }

    public static void setInstanceContext(Context context) {
        getInstance().setContext(context);
    }

    public Context getContext() {
        return context;
    }

    public static Context getInstanceContext() {
        return getInstance().getContext();
    }

    public AmbassadorApplicationComponent getComponent() {
        return component;
    }

    public static AmbassadorApplicationComponent getInstanceComponent() {
        return getInstance().getComponent();
    }

    private void setComponent(AmbassadorApplicationComponent component) {
        this.component = component;
    }

    public static void setInstanceComponent(AmbassadorApplicationComponent component) {
        AmbassadorSingleton.getInstance().setComponent(component);
    }

    public AmbassadorApplicationModule getAmbModule() {
        return amb;
    }

    public static AmbassadorApplicationModule getInstanceAmbModule() {
        return getInstance().getAmbModule();
    }

    private void setAmbModule(AmbassadorApplicationModule ambModule) {
        this.amb = ambModule;
    }

    public static void setInstanceAmbModule(AmbassadorApplicationModule ambModule) {
        getInstance().setAmbModule(ambModule);
    }

    public static boolean isValid() {
        return getInstanceComponent() != null && getInstanceAmbModule() != null;
    }

    public static AmbassadorSingleton getInstance() {
        return instance == null ? (instance = new AmbassadorSingleton()) : instance;
    }



}
