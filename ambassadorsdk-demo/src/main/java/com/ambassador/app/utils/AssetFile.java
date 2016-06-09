package com.ambassador.app.utils;

import android.support.annotation.Nullable;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class AssetFile {

    protected String file;

    public AssetFile(String file) {
        this.file = file;
    }

    @Nullable
    public String getAsString() {
        if (AmbSingleton.getContext() == null) return null;
        try {
            InputStream inputStream = AmbSingleton.getContext().getAssets().open(this.file);
            Scanner s = new Scanner(inputStream).useDelimiter("\\A");
            return s.hasNext() ? s.next() : null;
        } catch (IOException e) {
            return null;
        }
    }

}
