package com.ambassador.ambassadorsdk.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;

public final class Font {

    private Typeface typeface;

    public enum Defaults {
        FONT_LIGHT
    }

    private Font() {}

    public Font(@NonNull String path) {
        switch (path) {
            case "sans-serif-light":
                loadDefault(Defaults.FONT_LIGHT);
                break;

            default:
                AssetManager assets = AmbassadorSingleton.getInstanceContext().getAssets();
                try {
                    this.typeface = Typeface.createFromAsset(assets, path);
                } catch (Exception e) {
                    this.typeface = Typeface.DEFAULT;
                }
                break;
        }
    }

    private void loadDefault(@NonNull Defaults type) {
        switch (type) {
            case FONT_LIGHT:
                this.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL);
                break;
        }
    }

    public Typeface getTypeface() {
        return this.typeface;
    }

}
