package com.ambassador.ambassadorsdk.internal;

import android.content.res.Resources;

import com.ambassador.ambassadorsdk.RAFOptions;

public final class RAFOptionsFactory {

    public static RAFOptions decodeResources(Resources resources) {
        RAFOptions.Builder rafBuilder = new RAFOptions.Builder();


        return rafBuilder.build();
    }


}
