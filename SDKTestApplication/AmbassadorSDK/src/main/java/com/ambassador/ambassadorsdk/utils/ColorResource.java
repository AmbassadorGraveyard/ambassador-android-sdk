package com.ambassador.ambassadorsdk.utils;

import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;

public class ColorResource {

    private int color;

    @SuppressWarnings("unused")
    private ColorResource() {}

    public ColorResource(@ColorRes int resId) {
        this.color = ContextCompat.getColor(AmbassadorSingleton.getInstanceContext(), resId);
    }

    public int getColor() {
        return this.color;
    }

}
