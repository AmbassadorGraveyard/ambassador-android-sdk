package com.ambassador.ambassadorsdk.internal.activities;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class PresenterManagerTest {

    @Test
    public void testsGetInstanceIsNotNull() {
        PresenterManager instance = PresenterManager.getInstance();
        Assert.assertNotNull(instance);
    }

    @Test
    public void testsGetInstanceAlwaysSameInstance() {
        PresenterManager a = PresenterManager.getInstance();
        PresenterManager b = PresenterManager.getInstance();
        Assert.assertEquals(a, b);
    }

}
