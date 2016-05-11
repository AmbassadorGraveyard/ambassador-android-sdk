package com.ambassador.ambassadorsdk.internal.conversion;

public interface ConversionStatusListener {

    void success();
    void pending();
    void error();

}
