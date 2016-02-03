package com.ambassador.ambassadorsdk.internal;


import android.content.Context;

import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.events.AmbassaBus;
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
        Pusher2.class,
        Pusher2.Channel.class
})
public class Pusher2Test {

    protected Pusher2 pusher2;

    protected Context context;

    protected AmbassaBus ambassaBus;
    protected AmbassadorConfig ambassadorConfig;
    protected RequestManager requestManager;

    protected Pusher2.Channel channel;
    protected Pusher pusher;

    @Before
    public void setUp() throws Exception {
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
                pusher2.ambassaBus = ambassaBus;
                pusher2.ambassadorConfig = ambassadorConfig;
                pusher2.requestManager = requestManager;
                return null;
            }
        }).when(component).inject(Mockito.any(Pusher2.class));

        context = Mockito.mock(Context.class);
        PowerMockito.when(AmbassadorSingleton.getInstanceContext()).thenReturn(context);

        this.ambassaBus = Mockito.mock(AmbassaBus.class);
        this.ambassadorConfig = Mockito.mock(AmbassadorConfig.class);
        this.requestManager = Mockito.mock(RequestManager.class);
        this.channel = Mockito.spy(new Pusher2.Channel.Builder().build());
        PowerMockito.whenNew(Pusher2.Channel.class).withAnyArguments().thenReturn(channel);

        this.pusher = Mockito.mock(Pusher.class);
        channel.pusher = this.pusher;

        pusher2 = Mockito.spy(Pusher2.class);
    }

    @Test
    public void connectToNewChannel_connectionIsNull_doesNotCallDisconnect() {
        // ARRANGE
        pusher2.channel = null;
        Mockito.doNothing().when(channel).init();
        Mockito.doNothing().when(channel).connect();

        // ACT
        pusher2.startNewChannel();

        // ASSERT
        Mockito.verify(pusher, Mockito.never()).disconnect();
    }

    @Test
    public void connectToNewChannel_connectionNotNull_doesCallDisconnect() {
        // ARRANGE
        pusher2.channel = this.channel;
        Mockito.doNothing().when(channel).init();
        Mockito.doNothing().when(channel).connect();

        // ACT
        pusher2.startNewChannel();

        // ASSERT
        Mockito.verify(pusher, Mockito.times(1)).disconnect();
    }

}
