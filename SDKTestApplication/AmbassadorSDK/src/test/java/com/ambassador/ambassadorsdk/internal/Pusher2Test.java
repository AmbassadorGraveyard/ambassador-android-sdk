package com.ambassador.ambassadorsdk.internal;


import android.content.Context;

import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.injection.AmbassadorApplicationComponent;
import com.pusher.client.Pusher;
import com.squareup.otto.Bus;

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
        Pusher2.class
})
public class Pusher2Test {

    protected Pusher2 pusher2;

    protected Context context;

    protected Bus bus;
    protected AmbassadorConfig ambassadorConfig;
    protected RequestManager requestManager;

    protected Pusher2.Channel channel;
    protected Pusher pusher;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbassadorSingleton.class
        );

        AmbassadorApplicationComponent component = Mockito.mock(AmbassadorApplicationComponent.class);
        PowerMockito.when(AmbassadorSingleton.getInstanceComponent()).thenReturn(component);
        Mockito.doNothing().when(component).inject(Mockito.any(Pusher2.class));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Pusher2 pusher2 = (Pusher2) invocation.getArguments()[0];
                pusher2.ambassaBus = bus;
                pusher2.ambassadorConfig = ambassadorConfig;
                pusher2.requestManager = requestManager;
                return null;
            }
        }).when(component).inject(Mockito.any(Pusher2.class));

        context = Mockito.mock(Context.class);
        Mockito.when(AmbassadorSingleton.getInstanceContext()).thenReturn(context);

        this.bus = Mockito.mock(Bus.class);
        this.ambassadorConfig = Mockito.mock(AmbassadorConfig.class);
        this.requestManager = Mockito.mock(RequestManager.class);
        this.channel = new Pusher2.Channel.Builder().build();
        this.pusher = Mockito.mock(Pusher.class);
        channel.pusher = this.pusher;

        pusher2 = Mockito.spy(Pusher2.class);
    }

    @Test
    public void connectToNewChannel_connectionIsNull_doesNotCallDisconnect() {
        // ARRANGE
        pusher2.channel = null;
        Mockito.doNothing().when(requestManager).createPusherChannel(Mockito.any(RequestManager.RequestCompletion.class));

        // ACT
        pusher2.startNewChannel();

        // ASSERT
        Mockito.verify(pusher, Mockito.never()).disconnect();
    }

    @Test
    public void connectToNewChannel_connectionNotNull_doesCallDisconnect() {
        // ARRANGE
        pusher2.channel = this.channel;
        Mockito.doNothing().when(requestManager).createPusherChannel(Mockito.any(RequestManager.RequestCompletion.class));

        // ACT
        pusher2.startNewChannel();

        // ASSERT
        Mockito.verify(pusher, Mockito.times(1)).disconnect();
    }

}
