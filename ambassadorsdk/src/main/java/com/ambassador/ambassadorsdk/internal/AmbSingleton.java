package com.ambassador.ambassadorsdk.internal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

        if (AmbSingleton.module == null) {
            AmbSingleton.module = new AmbModule();
            AmbSingleton.graph = ObjectGraph.create(AmbSingleton.module);
            AmbSingleton.module.init();
        }
    }

    @Nullable
    public static Context getContext() {
        return context;
    }

    @Nullable
    public static ObjectGraph getGraph() {
        return graph;
    }

    /**
     * Injects dependencies onto a passed in object using the ObjectGraph created during
     * initialization.
     * @param object the object to inject dependencies into.
     */
    public static void inject(Object object) {
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
