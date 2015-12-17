package com.ambassador.ambassadorsdk.internal.api.conversions;

import com.ambassador.ambassadorsdk.internal.api.ServiceGenerator;
import com.ambassador.ambassadorsdk.internal.api.identify.IdentifyClient;

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
public class ConversionsApiTest {

    ConversionsApi conversionsApi;
    ConversionsClient conversionsClient;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(
                ServiceGenerator.class
        );

        ConversionsApi ca = new ConversionsApi(false);
        conversionsApi = Mockito.spy(ca);

        conversionsClient = Mockito.mock(ConversionsClient.class);
        conversionsApi.setConversionsClient(conversionsClient);
    }

    @Test
    public void initTest() {
        // ARRANGE
        Mockito.when(ServiceGenerator.createService(ConversionsClient.class)).thenReturn(conversionsClient);

        // ACT
        conversionsApi.init();

        // ASSERT
        Mockito.verify(conversionsApi, Mockito.times(2)).setConversionsClient(conversionsClient);
    }
    
}
