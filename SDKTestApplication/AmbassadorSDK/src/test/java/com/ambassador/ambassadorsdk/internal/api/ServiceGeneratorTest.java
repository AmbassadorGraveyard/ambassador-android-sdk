package com.ambassador.ambassadorsdk.internal.api;

import android.util.Log;

import com.ambassador.ambassadorsdk.TestUtils;
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.Client;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbassadorSingleton.class,
        ServiceGenerator.class,
        Log.class
})
public class ServiceGeneratorTest {

    private static String endpoint = "http://test.com";

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(
                Log.class
        );

        TestUtils.mockStrings();

        PowerMockito.spy(ServiceGenerator.class);
    }

    @Test
    public void createServiceTest() throws Exception {
        // ARRANGE
        boolean thrown1 = false;
        boolean thrown2 = false;
        boolean thrown3 = false;

        RestAdapter.Builder builder = Mockito.mock(RestAdapter.Builder.class);
        RestAdapter restAdapter = Mockito.mock(RestAdapter.class);

        Mockito.when(ServiceGenerator.getBuilder()).thenReturn(builder);
        Mockito.when(builder.setEndpoint(Mockito.anyString())).thenReturn(builder);
        Mockito.when(builder.setClient(Mockito.any(Client.class))).thenReturn(builder);
        Mockito.when(builder.setLogLevel(Mockito.any(RestAdapter.LogLevel.class))).thenReturn(builder);
        Mockito.when(builder.setLog(Mockito.any(AndroidLog.class))).thenReturn(builder);
        Mockito.when(builder.build()).thenReturn(restAdapter);

        // ACT
        try {
            ServiceGenerator.createService(ApiClient1.class);
        } catch (ServiceGenerator.NoEndpointFoundException e) {
            thrown1 = true;
        }
        try {
            ServiceGenerator.createService(ApiClient2.class);
        } catch (ServiceGenerator.NoEndpointFoundException e) {
            thrown2 = true;
        }
        try {
            ServiceGenerator.createService(ApiClient3.class);
        } catch (ServiceGenerator.NoEndpointFoundException e) {
            thrown3 = true;
        }

        // ASSERT
        Assert.assertFalse(thrown1);
        Assert.assertTrue(thrown2);
        Assert.assertTrue(thrown3);

        Mockito.verify(restAdapter).create(Mockito.eq(ApiClient1.class));
        Mockito.verify(restAdapter, Mockito.times(0)).create(Mockito.eq(ApiClient2.class));
        Mockito.verify(restAdapter, Mockito.times(0)).create(Mockito.eq(ApiClient3.class));
    }

    @Test
    public void extractEndpointTest() {
        // ARRANGE
        String check1 = null;
        String check2 = null;
        String check3 = null;
        boolean thrown1 = false;
        boolean thrown2 = false;
        boolean thrown3 = false;

        // ACT
        try {
            check1 = ServiceGenerator.extractEndpoint(ApiClient1.class);
        } catch (ServiceGenerator.NoEndpointFoundException e) {
            thrown1 = true;
        }
        try {
            check2 = ServiceGenerator.extractEndpoint(ApiClient2.class);
        } catch (ServiceGenerator.NoEndpointFoundException e) {
            thrown2 = true;
        }
        try {
            check3 = ServiceGenerator.extractEndpoint(ApiClient3.class);
        } catch (ServiceGenerator.NoEndpointFoundException e) {
            thrown3 = true;
        }

        // ASSERT
        Assert.assertFalse(thrown1);
        Assert.assertTrue(thrown2);
        Assert.assertFalse(thrown3);

        Assert.assertEquals(endpoint, check1);
        Assert.assertEquals(null, check2);
        Assert.assertEquals("", check3);
    }

    @Test
    public void getBuilderTest() {
        // ARRANGE
        RestAdapter.Builder builder1;
        RestAdapter.Builder builder2;

        // ACT
        builder1 = ServiceGenerator.getBuilder();
        builder2 = ServiceGenerator.getBuilder();

        // ASSERT
        Assert.assertNotNull(builder1);
        Assert.assertNotNull(builder2);
        Assert.assertNotEquals(builder1, builder2);
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
