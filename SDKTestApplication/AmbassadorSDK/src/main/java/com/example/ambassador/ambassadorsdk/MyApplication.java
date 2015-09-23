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

    @Singleton
    @Component(modules=ApplicationModule.class)
    public interface ApplicationComponent extends AmbassadorSDKComponent {
    }

    private static AmbassadorActivity.AmbassadorActivityComponent component = null;

    public void setComponent(AmbassadorActivity.AmbassadorActivityComponent comp) {
        component = comp;
    }

    public static AmbassadorActivity.AmbassadorActivityComponent component() {
        return component;
    }

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();

        //testing code sets the component to test version. if app is running normally, component will be null
        //which will create the application version component
        if (component == null) {
            component = DaggerAmbassadorActivity_AmbassadorActivityComponent.builder().ambassadorActivityModule(new AmbassadorActivityModule(null)).build();
            //component = DaggerMyApplication_ApplicationComponent.builder().applicationModule(new ApplicationModule()).build();
        }
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
