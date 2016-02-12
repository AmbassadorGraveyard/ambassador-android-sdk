package com.ambassador.ambassadorsdk.internal;

import android.content.Context;

import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.ambassador.ambassadorsdk.internal.injection.AmbassadorApplicationComponent;
import com.pusher.client.Pusher;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        AmbassadorSingleton.class,
        PusherManager.class,
        PusherManager.Channel.class,
        Auth.class
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
        PowerMockito.mockStatic(
                AmbassadorSingleton.class
        );

        AmbassadorApplicationComponent component = Mockito.mock(AmbassadorApplicationComponent.class);
        PowerMockito.when(AmbassadorSingleton.getInstanceComponent()).thenReturn(component);
        Mockito.doNothing().when(component).inject(Mockito.any(PusherManager.class));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                PusherManager pusher2 = (PusherManager) invocation.getArguments()[0];
                pusher2.auth = auth;
                pusher2.requestManager = requestManager;
                return null;
            }
        }).when(component).inject(Mockito.any(PusherManager.class));

        context = Mockito.mock(Context.class);
        PowerMockito.when(AmbassadorSingleton.getInstanceContext()).thenReturn(context);

        this.auth = Mockito.spy(Auth.class);
        Mockito.doNothing().when(auth).save();

        this.requestManager = Mockito.mock(RequestManager.class);
        this.channel = Mockito.spy(new PusherManager.Channel.Builder().build());
        PowerMockito.whenNew(PusherManager.Channel.class).withAnyArguments().thenReturn(channel);

        this.pusher = Mockito.mock(Pusher.class);
        channel.pusher = this.pusher;

        pusherManager = Mockito.spy(PusherManager.class);
    }

    @Test
    public void connectToNewChannel_connectionIsNull_doesNotCallDisconnect() {
        // ARRANGE
        pusherManager.channel = null;
        Mockito.doNothing().when(channel).init();
        Mockito.doNothing().when(channel).connect();

        // ACT
        pusherManager.startNewChannel();

        // ASSERT
        Mockito.verify(pusher, Mockito.never()).disconnect();
    }

    @Test
    public void connectToNewChannel_connectionNotNull_doesCallDisconnect() {
        // ARRANGE
        pusherManager.channel = this.channel;
        Mockito.doNothing().when(channel).init();
        Mockito.doNothing().when(channel).connect();

        // ACT
        pusherManager.startNewChannel();

        // ASSERT
        Mockito.verify(pusher, Mockito.times(1)).disconnect();
    }


}
