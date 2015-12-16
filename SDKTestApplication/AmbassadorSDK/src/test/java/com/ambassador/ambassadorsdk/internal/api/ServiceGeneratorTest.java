package com.ambassador.ambassadorsdk.internal.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        ServiceGenerator.class
})
public class ServiceGeneratorTest {

    static String endpoint = "http://test.com";

    @Before
    public void setUp() throws Exception {
        PowerMockito.spy(ServiceGenerator.class);
    }

    @Test
    public void createServiceTest() throws Exception {
        // ARRANGE
        boolean thrown1 = false;
        boolean thrown2 = false;

        // ACT
        try {
            ServiceGenerator.createService(ApiClient2.class);
        } catch (ServiceGenerator.NoEndpointFoundException e) {
            thrown1 = true;
        }
        try {
            ServiceGenerator.createService(ApiClient3.class);
        } catch (ServiceGenerator.NoEndpointFoundException e) {
            thrown2 = true;
        }

        // ASSERT
        Assert.assertTrue(thrown1);
        Assert.assertTrue(thrown2);
    }

    @Test
    public void extractEndpointTest() {
        // ARRANGE
        String check1 = null;
        String check2 = null;
        String check3;
        boolean thrown = false;

        // ACT
        check1 = ServiceGenerator.extractEndpoint(ApiClient1.class);
        try {
            check2 = ServiceGenerator.extractEndpoint(ApiClient2.class);
        } catch (ServiceGenerator.NoEndpointFoundException e) {
            thrown = true;
        }
        check3 = ServiceGenerator.extractEndpoint(ApiClient3.class);

        // ASSERT
        Assert.assertEquals(endpoint, check1);
        Assert.assertNull(check2);
        Assert.assertTrue(thrown);
        Assert.assertEquals("", check3);
    }

    private interface ApiClient1 {

        String ENDPOINT = ServiceGeneratorTest.endpoint;

    }

    private interface ApiClient2 {

    }

    private interface ApiClient3 {

        String ENDPOINT = "";

    }

}
