package com.ambassador.ambassadorsdk.internal.api.bulkshare;

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
public class BulkShareApiTest {

    BulkShareApi bulkShareApi;
    BulkShareClient bulkShareClient;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(
                ServiceGenerator.class
        );

        BulkShareApi bsa = new BulkShareApi(false);
        bulkShareApi = Mockito.spy(bsa);

        bulkShareClient = Mockito.mock(BulkShareClient.class);
        bulkShareApi.setBulkShareClient(bulkShareClient);
    }

    @Test
    public void initTest() {
        // ARRANGE
        Mockito.when(ServiceGenerator.createService(BulkShareClient.class)).thenReturn(bulkShareClient);

        // ACT
        bulkShareApi.init();

        // ASSERT
        Mockito.verify(bulkShareApi, Mockito.times(2)).setBulkShareClient(bulkShareClient);
    }

}
