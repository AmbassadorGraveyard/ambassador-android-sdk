package com.ambassador.ambassadorsdk.internal;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.internal.injection.AmbModule;

import dagger.ObjectGraph;

/**
 * Stores a Context and injection data to be utilized statically throughout the codebase.
 */
public class AmbSingleton {

    protected static Context context;
    protected static AmbModule module;
    protected static ObjectGraph graph;

    /**
     * Initializes the singleton using a context.  After this call the Singleton should be valid.
     * Context is stored as an application context.  Module and graph are initialized and set.
     * @param context the context to store and utilize throughout the codebase.
     */
    public static void init(@NonNull Context context) {
        AmbSingleton.context = context.getApplicationContext();

        if (AmbSingleton.context == null) {
            AmbSingleton.context = context;
        }

        if (AmbSingleton.module == null) {
            AmbSingleton.module = new AmbModule();
        }

        if (AmbSingleton.graph == null) {
            AmbSingleton.graph = ObjectGraph.create(AmbSingleton.module);
            AmbSingleton.module.init();
        }
    }

    /**
     * Does init with a custom module for creating the ObjectGraph.
     * @param context the context to store and utilize throughout the codebase.
     * @param module the module to use to provide injection dependencies.
     */
    public static void init(@NonNull Context context, @NonNull Object module) {
        AmbSingleton.graph = ObjectGraph.create(module);
        init(context);
    }

    /**
     * Returns context. NonNull because the only case in which this should return null is if the developer
     * does not call runWithKeys, or the parent application is garbage collected.  These are both things
     * not handled by us, so assume not null.
     * @return Context context
     */
    @NonNull
    public static Context getContext() {
        return context;
    }

    /**
     * Returns graph. NonNull because the only case in which this should return null is if the developer
     * does not call runWithKeys, or the parent application is garbage collected.  These are both things
     * not handled by us, so assume not null.
     * @return ObjectGraph graph
     */
    @NonNull
    public static ObjectGraph getGraph() {
        return graph;
    }

    /**
     * Injects dependencies onto a passed in object using the ObjectGraph created during
     * initialization.
     * @param object the object to inject dependencies into.
     */
    public static void inject(Object object) {
        if (graph == null) {
            AmbSingleton.init(AmbSingleton.getContext());
            if (graph == null) {
                graph = ObjectGraph.create(AmbSingleton.module);
            }
        }

        graph.inject(object);
    }

    /**
     * Singleton is valid if all fields are not null.  If any given field is null and the singleton
     * is used, unexpected behaviour will occur.
     * @return a boolean telling whether or not all fields are not null.
     */
    public static boolean isValid() {
        return AmbSingleton.context != null && AmbSingleton.module != null && AmbSingleton.graph != null;
    }

}
