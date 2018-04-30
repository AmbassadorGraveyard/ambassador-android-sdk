package com.ambassador.ambassadorsdk.internal.utils.res;

import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;

import javax.inject.Inject;

public final class ColorResource {
    @Inject
    protected AmbSingleton AmbSingleton;

    private int color;

    @SuppressWarnings("unused")
    private ColorResource() {}

    public ColorResource(@ColorRes int resId) {
        this.color = ContextCompat.getColor(AmbSingleton.getInstance().getContext(), resId);
    }

    public int getColor() {
        return this.color;
    }

}
