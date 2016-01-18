package com.ambassador.ambassadorsdk.internal;


import com.ambassador.ambassadorsdk.ConversionParameters;

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

        parameters.campaign = 12;
        Assert.assertFalse(parameters.isValid());

        parameters.email = "test@getambassador.com";
        Assert.assertFalse(parameters.isValid());

        parameters.revenue = 50;
        Assert.assertTrue(parameters.isValid());

        parameters.campaign = -1;
        Assert.assertFalse(parameters.isValid());

        parameters.email = "";
        Assert.assertFalse(parameters.isValid());
    }

}
