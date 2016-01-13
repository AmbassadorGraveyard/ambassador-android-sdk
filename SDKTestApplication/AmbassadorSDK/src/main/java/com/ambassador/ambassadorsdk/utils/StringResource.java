package com.ambassador.ambassadorsdk.utils;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;

public final class StringResource {

    private String value;

    private StringResource() {}

    public StringResource(int refId) {
        this.value = AmbassadorSingleton.getInstanceContext().getString(refId);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}
