package com.ambassador.ambassadorsdk.internal.utils.res;

import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;

public final class ColorResource {

    private int color;

    @SuppressWarnings("unused")
    private ColorResource() {}

    public ColorResource(@ColorRes int resId) {
        this.color = ContextCompat.getColor(AmbSingleton.getInstanceContext(), resId);
    }

    public int getColor() {
        return this.color;
    }

}
