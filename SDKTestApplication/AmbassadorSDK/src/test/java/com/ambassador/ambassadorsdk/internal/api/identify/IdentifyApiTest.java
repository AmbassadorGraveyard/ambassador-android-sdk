package com.ambassador.ambassadorsdk.internal.api.identify;

import com.ambassador.ambassadorsdk.internal.api.ServiceGenerator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        ServiceGenerator.class
})
public class IdentifyApiTest {

    IdentifyApi identifyApi;
    IdentifyClient identifyClient;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(
                ServiceGenerator.class
        );
        
        IdentifyApi ia = new IdentifyApi(false);
        identifyApi = Mockito.spy(ia);

        identifyClient = Mockito.mock(IdentifyClient.class);
        identifyApi.setIdentifyClient(identifyClient);
    }

    @Test
    public void initTest() {
        // ARRANGE
        Mockito.when(ServiceGenerator.createService(IdentifyClient.class)).thenReturn(identifyClient);

        // ACT
        identifyApi.init();

        // ASSERT
        Mockito.verify(identifyApi, Mockito.times(2)).setIdentifyClient(identifyClient);
    }

}
