package com.ambassador.ambassadorsdk.internal;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.internal.injection.AmbComponent;
import com.ambassador.ambassadorsdk.internal.injection.AmbModule;
import com.ambassador.ambassadorsdk.internal.injection.DaggerAmbComponent;

public final class AmbSingleton {
    protected Context context;
    protected AmbComponent component;
    private static AmbSingleton instance;// = new AmbSingleton();

    public static AmbSingleton getInstance() {
        if (instance == null) {
            instance = new AmbSingleton();
        }

        return instance;
    }

    /**
     * Initializes the singleton using a context.  After this call the Singleton should be valid.
     * Context is stored as an application context.  Module and component are initialized and set.
     * @param context the context to store and utilize throughout the codebase.
     */
    public void init(@NonNull Context context) {
        this.context = context.getApplicationContext();

        if (this.context == null) {
            this.context = context;
        }

        this.component = DaggerAmbComponent.builder()
            .ambModule(new AmbModule())
            .build();
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
     * Injects dependencies onto a passed in object using the ObjectGraph created during
     * initialization.
     * @param object the object in which to inject dependencies
     */
//    public void inject(Object object) {
////        if (graph == null) {
////            AmbSingleton.init(AmbSingleton.getInstance().getContext());
////            if (graph == null) {
////                graph = ObjectGraph.create(AmbSingleton.module);
////            }
////        }
////
////        graph.inject(object);
//
//        getAmbComponent().inject(object);
//    }

    /**
     * Singleton is valid if all fields are not null.  If any given field is null and the singleton
     * is used, unexpected behaviour will occur.
     * @return a boolean telling whether or not all fields are not null.
     */
    public boolean isValid() {
        return true;//this.context != null && this.module != null && this.component != null;
    }
}
