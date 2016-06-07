package com.ambassador.ambassadorsdk.internal.conversion;

import android.os.Bundle;

import com.ambassador.ambassadorsdk.ConversionParameters;

public class ConversionParametersFactory {

    public static ConversionParameters getFromProperties(Bundle properties) {
        return new ConversionParameters.Builder()
                .setCampaign(properties.getInt("campaign", -1))
                .setRevenue(properties.getFloat("revenue", -1f))
                .setIsApproved(properties.getInt("commissionApproved", 0))
                .setEventData1(properties.getString("eventData1", ""))
                .setEventData2(properties.getString("eventData2", ""))
                .setEventData3(properties.getString("eventData3", ""))
                .setTransactionUid(properties.getString("orderId", ""))
                .setEmailNewAmbassador(properties.getInt("emailNewAmbassador", 0))
                .build();
    }

}
