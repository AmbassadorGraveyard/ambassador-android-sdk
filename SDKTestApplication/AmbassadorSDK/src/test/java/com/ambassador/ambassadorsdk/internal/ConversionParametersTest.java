package com.ambassador.ambassadorsdk.internal;


import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({})
public class ConversionParametersTest {

    @Test
    public void isValidTest() {
        ConversionParameters parameters = new ConversionParameters();
        Assert.assertFalse(parameters.isValid());

        parameters.mbsy_campaign = 12;
        Assert.assertFalse(parameters.isValid());

        parameters.mbsy_email = "test@getambassador.com";
        Assert.assertFalse(parameters.isValid());

        parameters.mbsy_revenue = 50;
        Assert.assertTrue(parameters.isValid());

        parameters.mbsy_campaign = -1;
        Assert.assertFalse(parameters.isValid());

        parameters.mbsy_email = "";
        Assert.assertFalse(parameters.isValid());
    }

}
