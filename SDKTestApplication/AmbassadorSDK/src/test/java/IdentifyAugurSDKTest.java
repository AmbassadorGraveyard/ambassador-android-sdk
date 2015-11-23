package com.ambassador.ambassadorsdk;

import android.content.Context;

import com.pusher.client.Pusher;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;

import javax.inject.Inject;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;


/**
 * Created by JakeDunahee on 9/8/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class IdentifyAugurSDKTest extends TestCase {
    private String data;
    private IdentifyAugurSDK identify, idSpy;

    @Inject
    AmbassadorConfig ambassadorConfig;

    @Before
    public void setup() {
        //Context context = mock(Context.class);
        idSpy = spy(new IdentifyAugurSDK());
    }

    @Test
    public void getIdentityTest() {
        IdentifyAugurSDK augurSDK = mock(IdentifyAugurSDK.class);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                data = "completionData";
                return null;
            }
        }).when(augurSDK).getAugur(ambassadorConfig);

        augurSDK.getAugur(ambassadorConfig);

        assertEquals("GetIdentify", "completionData", data);
    }
}
