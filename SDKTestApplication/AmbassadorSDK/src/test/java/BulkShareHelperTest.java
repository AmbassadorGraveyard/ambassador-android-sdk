package com.example.ambassador.ambassadorsdk;
import android.app.ProgressDialog;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.mock;

/**
 * Created by JakeDunahee on 9/9/15.
 */
@RunWith(PowerMockRunner.class)
public class BulkShareHelperTest {
    ProgressDialog mockDialog = mock(ProgressDialog.class);
    String mockMessage = "This is a mock message. http://mockURL.co";
    BulkShareHelper mockHelper = new BulkShareHelper()
}
