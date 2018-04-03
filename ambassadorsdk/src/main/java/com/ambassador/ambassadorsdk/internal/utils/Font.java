package com.ambassador.ambassadorsdk.internal.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;

import javax.inject.Inject;

/**
 *
 */
public final class Font {
    public enum Defaults {
        FONT_DEFAULT, FONT_LIGHT
    }

    @Inject
    protected AmbSingleton AmbSingleton;

    private Typeface typeface;

    @SuppressWarnings("unused")
    private Font() {}

    public Font(@NonNull String path) {
        switch (path) {
            case "sans-serif":
                loadDefault(Defaults.FONT_DEFAULT);
                break;

            case "sans-serif-light":
                loadDefault(Defaults.FONT_LIGHT);
                break;

            default:
                AssetManager assets =AmbSingleton.getInstance().getContext().getAssets();
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
            case FONT_DEFAULT:
                this.typeface = Typeface.create("sans-serif", Typeface.NORMAL);
                break;

            case FONT_LIGHT:
                this.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL);
                break;

            default:
                this.typeface = Typeface.DEFAULT;
                break;
        }
    }

    @NonNull
    public Typeface getTypeface() {
        return typeface != null ? typeface : Typeface.DEFAULT;
    }

}
