package com.ambassador.ambassadorsdk.utils;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;

public class StringResource {

    private String value;

    @SuppressWarnings("unused")
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
