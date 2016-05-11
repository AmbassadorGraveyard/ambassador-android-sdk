package com.ambassador.ambassadorsdk.internal.conversion;

import com.ambassador.ambassadorsdk.ConversionParameters;

public class AmbConversion {

    protected ConversionParameters conversionParameters;
    protected boolean limitOnce;

    protected AmbConversion(ConversionParameters conversionParameters, boolean limitOnce) {
        this.conversionParameters = conversionParameters;
        this.limitOnce = limitOnce;
    }

    public void execute() {

    }

    public static AmbConversion get(ConversionParameters conversionParameters, boolean limitOnce) {
        return new AmbConversion(conversionParameters, limitOnce);
    }

}
