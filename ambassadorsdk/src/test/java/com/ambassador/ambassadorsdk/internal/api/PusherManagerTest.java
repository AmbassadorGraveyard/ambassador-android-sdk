package com.ambassador.ambassadorsdk.internal.api;

import android.content.Context;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.data.Auth;
import com.pusher.client.Pusher;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import junit.framework.Assert;

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
        pusherManager.channel = channel;
    }

    @Test
    public void connectToNewChannelDoesNotCallDisconnectWhenConnectionIsNull() {
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
    public void connectToNewChannelDoesCallDisconnectWhenConnectionNotNull() {
        // ARRANGE
        pusherManager.channel = this.channel;
        Mockito.doNothing().when(channel).init();
        Mockito.doNothing().when(channel).connect();

        // ACT
        pusherManager.startNewChannel();

        // ASSERT
        Mockito.verify(pusher, Mockito.times(1)).disconnect();
    }

    @Test
    public void subscribeChannelToAmbassadorDoesNothingWhenChannelNotConnected() {
        // ARRANGE
        Mockito.doNothing().when(pusherManager).startNewChannel();
        Mockito.doNothing().when(pusherManager).requestSubscription();
        channel.connectionState = ConnectionState.DISCONNECTED;

        // ACT
        pusherManager.subscribeChannelToAmbassador();

        // ASSERT
        Mockito.verify(pusherManager, Mockito.never()).requestSubscription();
        Mockito.verify(pusherManager, Mockito.never()).startNewChannel();
    }

    @Test
    public void subscribeChannelToAmbassadorDoesNothingWhenChannelNull() {
        // ARRANGE
        Mockito.doNothing().when(pusherManager).startNewChannel();
        Mockito.doNothing().when(pusherManager).requestSubscription();
        pusherManager.channel = null;

        // ACT
        pusherManager.subscribeChannelToAmbassador();

        // ASSERT
        Mockito.verify(pusherManager, Mockito.never()).requestSubscription();
        Mockito.verify(pusherManager, Mockito.never()).startNewChannel();
    }

    @Test
    public void subscribeChannelToAmbassadorDoesCallRequestSubscriptionWhenChannelConnected() {
        // ARRANGE
        Mockito.doNothing().when(pusherManager).startNewChannel();
        Mockito.doNothing().when(pusherManager).requestSubscription();
        channel.connectionState = ConnectionState.CONNECTED;
        channel.channelName = "name";

        // ACT
        pusherManager.subscribeChannelToAmbassador();

        // ASSERT
        Mockito.verify(pusherManager).requestSubscription();
        Mockito.verify(pusherManager, Mockito.never()).startNewChannel();
    }


    /*****************
     * Channel tests *
     *****************/

    @RunWith(PowerMockRunner.class)
    @PrepareForTest({

    })
    public static class ChannelTest {

        protected PusherManager.Channel channel;

        protected Pusher pusher;
        protected ConnectionEventListener connectionEventListener;

        @Before
        public void setUp() {
            this.channel = Mockito.spy(PusherManager.Channel.class);

            this.pusher = Mockito.mock(Pusher.class);
            channel.pusher = pusher;

            this.connectionEventListener = Mockito.spy(ConnectionEventListener.class);
            channel.connectionEventListener = connectionEventListener;
        }

        @Test
        public void connectDoesCallConnect() {
            // ARRANGE
            Mockito.doNothing().when(pusher).connect();

            // ACT
            channel.connect();

            // ASSERT
            Mockito.verify(pusher).connect(Mockito.eq(connectionEventListener), Mockito.eq(ConnectionState.ALL));
        }

        //@Test
        public void connectDoesSetConnectionState() {
            // ARRANGE
            final ConnectionStateChange connectionStateChange = Mockito.mock(ConnectionStateChange.class);
            Mockito.doReturn(ConnectionState.CONNECTED).doReturn(ConnectionState.DISCONNECTED).when(connectionStateChange).getCurrentState();

            Mockito.doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    ConnectionEventListener connectionEventListener = (ConnectionEventListener) invocation.getArguments()[0];
                    connectionEventListener.onConnectionStateChange(connectionStateChange);
                    return null;
                }
            }).doAnswer(new Answer() {
                @Override
                public Object answer(InvocationOnMock invocation) throws Throwable {
                    ConnectionEventListener connectionEventListener = (ConnectionEventListener) invocation.getArguments()[0];
                    connectionEventListener.onConnectionStateChange(connectionStateChange);
                    return null;
                }
            }).when(pusher).connect(Mockito.eq(connectionEventListener), Mockito.eq(ConnectionState.CONNECTED));

            // ACT
            channel.connect();

            // ASSERT
            Assert.assertEquals(ConnectionState.CONNECTED, channel.connectionState);

            // ACT
            channel.disconnect();

            // ASSERT
            Assert.assertEquals(ConnectionState.DISCONNECTED, channel.connectionState);
        }

    }

}
