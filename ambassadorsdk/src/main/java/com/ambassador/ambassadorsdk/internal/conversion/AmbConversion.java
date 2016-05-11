package com.ambassador.ambassadorsdk.internal.conversion;

import com.ambassador.ambassadorsdk.ConversionParameters;

public class AmbConversion {

    protected ConversionParameters conversionParameters;
    protected boolean limitOnce;
    protected ConversionStatusListener conversionStatusListener;

    protected AmbConversion(ConversionParameters conversionParameters, boolean limitOnce, ConversionStatusListener conversionStatusListener) {
        this.conversionParameters = conversionParameters;
        this.limitOnce = limitOnce;
        this.conversionStatusListener = conversionStatusListener;
    }

    public void execute() {
        if (conversionParameters.getCampaign() == -1 || conversionParameters.getRevenue() < 0) {
            conversionStatusListener.error();
            return;
        }
    }

    public static AmbConversion get(ConversionParameters conversionParameters, boolean limitOnce, ConversionStatusListener conversionStatusListener) {
        return new AmbConversion(conversionParameters, limitOnce, conversionStatusListener);
    }

}
