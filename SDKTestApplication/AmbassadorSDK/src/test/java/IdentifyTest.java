package com.example.ambassador.ambassadorsdk;
import android.test.InstrumentationTestCase;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by JakeDunahee on 9/8/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class IdentifyTest extends TestCase {
    private String data;
    @Mock private Identify identify;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getIdentityTest() {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                data = "GetIdentify";
                return null;
            }
        }).when(identify).getIdentity();

        identify.getIdentity();

        assertEquals("GetIdentify", data);
    }
}
