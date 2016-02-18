package com.ambassador.ambassadorsdk.internal.api;

import android.content.Context;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.injection.AmbModule;
import com.pusher.client.Pusher;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbSingleton.class,
        PusherManager.class,
        PusherManager.Channel.class,
        Auth.class,
        AmbModule.class
})
public class PusherManagerTest {

    protected PusherManager pusherManager;

    protected Context context;

    protected Auth auth;

    protected RequestManager requestManager;

    protected PusherManager.Channel channel;
    protected Pusher pusher;

    @Before
    public void setUp() throws Exception {
//        PowerMockito.mockStatic(
//                AmbSingleton.class
//        );
//
//        PowerMockito.doAnswer(new Answer() {
//            @Override
//            public Object answer(InvocationOnMock invocation) throws Throwable {
//                PusherManager pusher2 = (PusherManager) invocation.getArguments()[0];
//                pusher2.auth = auth;
//                pusher2.requestManager = requestManager;
//                return null;
//            }
//        }).when(AmbSingleton.class, "inject", Mockito.any(PusherManager.class));
//
//
//        context = Mockito.mock(Context.class);
//        PowerMockito.when(AmbSingleton.getContext()).thenReturn(context);
//
//        this.auth = Mockito.spy(Auth.class);
//        Mockito.doNothing().when(auth).save();
//
//        this.requestManager = Mockito.mock(RequestManager.class);
//        this.channel = Mockito.spy(new PusherManager.Channel());
//
//        this.pusher = Mockito.mock(Pusher.class);
//        channel.pusher = this.pusher;
//
//        pusherManager = Mockito.spy(PusherManager.class);
//        pusherManager.channel = channel;
    }

    @Test
    public void test() {

    }

}
