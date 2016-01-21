package com.ambassador.ambassadorsdk.internal.factories;

import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;

import com.ambassador.ambassadorsdk.internal.utils.res.ColorResource;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;

public final class ResourceFactory {

    public static ColorResource getColor(@ColorRes int resId) {
        return new ColorResource(resId);
    }

    public static StringResource getString(@StringRes int resId) {
        return new StringResource(resId);
    }

}
