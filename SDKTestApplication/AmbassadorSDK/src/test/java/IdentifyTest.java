//package com.example.ambassador.ambassadorsdk;
//
//import android.content.Context;
//
//import com.pusher.client.Pusher;
//
//import junit.framework.TestCase;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.invocation.InvocationOnMock;
//import org.mockito.runners.MockitoJUnitRunner;
//import org.mockito.stubbing.Answer;
//
//import static org.mockito.Mockito.doAnswer;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.spy;
//
//
///**
// * Created by JakeDunahee on 9/8/15.
// */
//@RunWith(MockitoJUnitRunner.class)
//public class IdentifyTest extends TestCase {
//    private String data;
//    private Identify identify, idSpy;
//
//    @Before
//    public void setup() {
//        Context context = mock(Context.class);
//        idSpy = spy(new Identify(context, "kdjfs"));
//    }
//
//    @Test
//    public void getIdentityTest() {
//        IdentifyAugurSDK.AugurCompletion completionMock = mock(IdentifyAugurSDK.AugurCompletion.class);
//        IdentifyAugurSDK augurSDK = mock(IdentifyAugurSDK.class);
//
//        doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                data = "completionData";
//                return null;
//            }
//        }).when(augurSDK).getAugur(completionMock);
//
//        augurSDK.getAugur(completionMock);
//
//        assertEquals("GetIdentify", "completionData", data);
//    }
//
//    @Test
//    public void setUpPusherTest() {
//        IdentifyPusher.PusherCompletion pusherCompletion = mock(IdentifyPusher.PusherCompletion.class);
//
//    }
//}
