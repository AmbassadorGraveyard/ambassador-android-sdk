package com.ambassador.ambassadorsdk.internal.conversion;

public interface ConversionStatusListener {

    /**
     * Called when a conversion has been sent to and registered on the Ambassador backend successfully.
     */
    void success();

    /**
     * Called when a conversion is correct but can't be registered at the time. It is placed into storage
     * and will be sent when it has all the information it needs.
     */
    void pending();

    /**
     * Called when a conversion cannot and won't be registered due to invalid and/or incomplete
     * ConversionParameters.
     */
    void error();

}
