package com.ambassador.ambassadorsdk.internal;

import android.content.Context;

import com.ambassador.ambassadorsdk.internal.injection.AmbassadorApplicationModule;

import dagger.ObjectGraph;

public class AmbSingleton {

    private static AmbSingleton instance;
    private Context context;
    public static AmbassadorApplicationModule module;
    public static ObjectGraph graph;

    public static void init(Context context) {
        if (getInstanceContext() == null) {
            setInstanceContext(context);
        }

        if (getModule() == null) {
            module = new AmbassadorApplicationModule();
            graph = ObjectGraph.create(module);
            module.init();
        }
    }

    private void setContext(Context context) {
        this.context = context.getApplicationContext();
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

    public static AmbassadorApplicationModule getModule() {
        return module;
    }

    public static boolean isValid() {
        return getModule() != null;
    }

    public static AmbSingleton getInstance() {
        if (instance != null) {
            return instance;
        }

        instance = new AmbSingleton();
        return instance;
    }

    public static ObjectGraph getGraph() {
        return graph;
    }

}
