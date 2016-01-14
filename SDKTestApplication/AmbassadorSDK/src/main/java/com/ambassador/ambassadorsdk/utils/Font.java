package com.ambassador.ambassadorsdk.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;

public class Font {

    private Typeface typeface;

    private Font() {}

    public Font(String path) {
        AssetManager assets = AmbassadorSingleton.getInstanceContext().getAssets();
        try {
            this.typeface = Typeface.createFromAsset(assets, path);
        } catch (Exception e) {
            this.typeface = Typeface.DEFAULT;
        }
    }

    public Typeface getTypeface() {
        return this.typeface;
    }

}
