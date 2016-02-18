package com.ambassador.ambassadorsdk.internal.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;

/**
 *
 */
public final class Font {

    public enum Defaults {
        FONT_LIGHT
    }

    private Typeface typeface;

    @SuppressWarnings("unused")
    private Font() {}

    public Font(@NonNull String path) {
        switch (path) {
            case "sans-serif-light":
                loadDefault(Defaults.FONT_LIGHT);
                break;

            default:
                AssetManager assets = AmbSingleton.getContext().getAssets();
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

            default:
                this.typeface = Typeface.DEFAULT;
        }
    }

    @NonNull
    public Typeface getTypeface() {
        return typeface != null ? typeface : Typeface.DEFAULT;
    }

}
