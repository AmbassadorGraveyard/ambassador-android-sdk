package com.ambassador.ambassadorsdk.internal.utils.res;

import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;

/**
 *
 */
public final class StringResource {
    private String value;

    @SuppressWarnings("unused")
    private StringResource() {}

    public StringResource(@StringRes int resId) {
        try {
            this.value = AmbSingleton.getInstance().getContext().getString(resId);
        } catch (Resources.NotFoundException e) {
            this.value = null;
        }
    }

    @Nullable
    public String getValue() {
        return value;
    }

    @Nullable
    @Override
    public String toString() {
        return value;
    }

}
