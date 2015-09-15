package com.example.ambassador.ambassadorsdk;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.junit.Test;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by JakeDunahee on 9/9/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({BulkShareHelper.class, BulkShareHelper.BulkShareSMSRequest.class})
public class BulkShareHelperTest {
    ProgressDialog mockDialog = mock(ProgressDialog.class);
    String mockMessage = "This is a mock message. http://mockURL.co";
    BulkShareHelper mockHelper = new BulkShareHelper(mockDialog, mockMessage);

    @Test
    public void bulkSMSShareTest() throws Exception {
        // ARRANGE
        BulkShareHelper.BulkShareSMSRequest mockRequest = Mockito.spy(BulkShareHelper.BulkShareSMSRequest.class);
        AsyncTask<Void, Void, Void> mockExecuteTask = mock(AsyncTask.class);
        ArrayList<ContactObject> mockContacts = mock(ArrayList.class);

        // ACT
        PowerMockito.whenNew(BulkShareHelper.BulkShareSMSRequest.class).withAnyArguments().thenReturn(mockRequest);
        when(mockRequest.execute()).thenReturn(mockExecuteTask);
        mockHelper.bulkShare(mockContacts, true);

        // ASSERT
        assertEquals(mockContacts, mockRequest.contacts);
        verify(mockRequest).execute();
    }
}
