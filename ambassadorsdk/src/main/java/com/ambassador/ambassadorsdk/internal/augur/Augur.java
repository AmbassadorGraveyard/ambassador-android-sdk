package com.ambassador.ambassadorsdk.internal.augur;

import android.os.Handler;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class Augur {

    @SuppressWarnings("unchecked")
    public static void getJSON(JSONObject jsonObject, Handler.Callback callback) {
        try {
            final String libPath = "file:///android_asset/augur.aar";

            final File tmpDir = AmbassadorSingleton.getInstanceContext().getDir("dex", 0);

            final DexClassLoader classloader = new DexClassLoader(libPath, tmpDir.getAbsolutePath(), null, AmbassadorSingleton.getInstanceContext().getClassLoader());
            final Class<Object> classToLoad = (Class<Object>) classloader.loadClass("io.augur.wintermute.Augur");

            final Method method = classToLoad.getMethod("getJSON", JSONObject.class, Handler.Callback.class);
            method.invoke(null, jsonObject, callback);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
