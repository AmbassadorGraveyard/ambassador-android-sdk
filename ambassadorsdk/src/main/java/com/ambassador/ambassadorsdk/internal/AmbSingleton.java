package com.ambassador.ambassadorsdk.internal;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.internal.injection.AmbComponent;
import com.ambassador.ambassadorsdk.internal.injection.AmbModule;
import com.ambassador.ambassadorsdk.internal.injection.DaggerAmbComponent;

public final class AmbSingleton {
    protected Context context;
    protected AmbComponent component;
    private static AmbSingleton instance;

    public static AmbSingleton getInstance() {
        if (instance == null) {
            instance = new AmbSingleton();
        }

        return instance;
    }

    public void buildDaggerComponent() {
        this.component = DaggerAmbComponent.builder()
            .ambModule(new AmbModule())
            .build();
    }

    /**
     * Context is stored as an application context.
     * @param context the context to store and utilize throughout the codebase.
     */
    public void setContext(@NonNull Context context) {
        this.context = context.getApplicationContext();

        if (this.context == null) {
            this.context = context;
        }
    }

    /**
     * Returns context. NonNull because the only case in which this should return null is if the developer
     * does not call runWithKeys, or the parent application is garbage collected.  These are both things
     * not handled by us, so assume not null.
     * @return Context context
     */
    @NonNull
    public Context getContext() {
        return context;
    }

    /**
     * Returns AmbComponent. NonNull because the only case in which this should return null is if the developer
     * does not call runWithKeys, or the parent application is garbage collected.  These are both things
     * not handled by us, so assume not null.
     * @return ObjectGraph graph
     */
    @NonNull
    public AmbComponent getAmbComponent() {
        return component;
    }

    /**
     * Singleton is valid if all fields are not null.  If any given field is null and the singleton
     * is used, unexpected behaviour will occur.
     * @return a boolean telling whether or not all fields are not null.
     */
    public boolean isValid() {
        return this.context != null && this.component != null;
    }
}
