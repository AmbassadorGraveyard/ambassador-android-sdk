package com.ambassador.ambassadorsdk;
import com.example.ambassador.ambassadorsdk.AmbassadorSDK;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Created by JakeDunahee on 9/11/15.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(Utilities.class)
public class UtilitiesTest {
    @Before
    public void setUp() {
        PowerMockito.mockStatic(Utilities.class);
    }

    @Test
    public void successfulResponseCodeTest() {

    }
}
