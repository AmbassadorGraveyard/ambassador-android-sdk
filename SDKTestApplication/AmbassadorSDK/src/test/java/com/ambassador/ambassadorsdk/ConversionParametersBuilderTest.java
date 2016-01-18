package com.ambassador.ambassadorsdk;

import android.util.Log;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
    Log.class
})
public class ConversionParametersBuilderTest {

    private int mbsy_campaign = 15;
    private String mbsy_email = "test@getambassador.com";
    private String mbsy_first_name = "first";
    private String mbsy_last_name = "last";
    private int mbsy_email_new_ambassador = 16;
    private String mbsy_uid = "uid";
    private String mbsy_custom1 = "custom1";
    private String mbsy_custom2 = "custom2";
    private String mbsy_custom3 = "custom3";
    private int mbsy_auto_create = 17;
    private int mbsy_revenue = 18;
    private int mbsy_deactivate_new_ambassador = 19;
    private String mbsy_transaction_uid = "s1";
    private String mbsy_add_to_group_id = "s2";
    private String mbsy_event_data1 = "s3";
    private String mbsy_event_data2 = "s4";
    private String mbsy_event_data3 = "s5";
    private int mbsy_is_approved = 5;
    
    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                Log.class
        );
    }

    @Test
    public void buildTest() {
        // ARRANGE
        ConversionParametersBuilder builder = new ConversionParametersBuilder()
                .setCampaign(mbsy_campaign)
                .setEmail(mbsy_email)
                .setFirstName(mbsy_first_name)
                .setLastName(mbsy_last_name)
                .setEmailNewAmbassador(mbsy_email_new_ambassador)
                .setUid(mbsy_uid)
                .setCustom1(mbsy_custom1)
                .setCustom2(mbsy_custom2)
                .setCustom3(mbsy_custom3)
                .setAutoCreate(mbsy_auto_create)
                .setRevenue(mbsy_revenue)
                .setDeactivateNewAmbassador(mbsy_deactivate_new_ambassador)
                .setTransactionUid(mbsy_transaction_uid)
                .setAddToGroupId(mbsy_add_to_group_id)
                .setEventData1(mbsy_event_data1)
                .setEventData2(mbsy_event_data2)
                .setEventData3(mbsy_event_data3)
                .setIsApproved(mbsy_is_approved);
        
        // ACT
        ConversionParameters parameters = builder.build();
        
        // VERIFY
        Assert.assertEquals(parameters.campaign, mbsy_campaign);
        Assert.assertEquals(parameters.email, mbsy_email);
        Assert.assertEquals(parameters.firstName, mbsy_first_name);
        Assert.assertEquals(parameters.lastName, mbsy_last_name);
        Assert.assertEquals(parameters.emailNewAmbassador, mbsy_email_new_ambassador);
        Assert.assertEquals(parameters.uid, mbsy_uid);
        Assert.assertEquals(parameters.custom1, mbsy_custom1);
        Assert.assertEquals(parameters.custom2, mbsy_custom2);
        Assert.assertEquals(parameters.custom3, mbsy_custom3);
        Assert.assertEquals(parameters.autoCreate, mbsy_auto_create);
        Assert.assertEquals(parameters.revenue, mbsy_revenue);
        Assert.assertEquals(parameters.deactivateNewAmbassador, mbsy_deactivate_new_ambassador);
        Assert.assertEquals(parameters.transactionUid, mbsy_transaction_uid);
        Assert.assertEquals(parameters.addToGroupId, mbsy_add_to_group_id);
        Assert.assertEquals(parameters.eventData1, mbsy_event_data1);
        Assert.assertEquals(parameters.eventData2, mbsy_event_data2);
        Assert.assertEquals(parameters.eventData3, mbsy_event_data3);
        Assert.assertEquals(parameters.isApproved, mbsy_is_approved);
    }

}
