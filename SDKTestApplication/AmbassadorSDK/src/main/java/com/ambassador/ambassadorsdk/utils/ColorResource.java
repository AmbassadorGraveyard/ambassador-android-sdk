package com.ambassador.ambassadorsdk.utils;

import android.support.annotation.ColorRes;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;

public class ColorResource {

    int color;

    private ColorResource() {}

    public ColorResource(@ColorRes int resId) {
        this.color = AmbassadorSingleton.getInstanceContext().getResources().getColor(resId);
    }

    public int getColor() {
        return this.color;
    }

}
