package com.ambassador.ambassadorsdk.internal.identify;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.api.PusherManager;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.User;

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
        AmbSingleton.class
})
public class AmbIdentifyTest {

    protected AmbIdentify ambIdentify;

    protected RequestManager requestManager;
    protected User user;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(
                AmbSingleton.class
        );

        ambIdentify = Mockito.spy(AmbIdentify.get("jake@getambassador.com", new AmbassadorIdentification().setEmail("jake@getambassador.com")));
        user = Mockito.mock(User.class);
        ambIdentify.user = user;
        requestManager = Mockito.mock(RequestManager.class);
        ambIdentify.requestManager = requestManager;

    }

    @Test
    public void testsGetDoesCreateInstanceWithEmail() throws Exception {
        Assert.assertEquals("jake@getambassador.com", ambIdentify.userId);
    }

    @Test
    public void testsExecuteDoesWaitForLongRunningTasks() throws Exception {
        ambIdentify.identifyTasks = new AmbIdentifyTask[]{ Mockito.mock(AmbAugurTask.class), Mockito.mock(AmbGcmTokenTask.class) };
        Mockito.doNothing().when(ambIdentify).onPreExecutionComplete();
        Mockito.doNothing().when(ambIdentify).setupPusher();

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                AmbIdentifyTask.OnCompleteListener onCompleteListener = (AmbIdentifyTask.OnCompleteListener) invocation.getArguments()[0];
                onCompleteListener.complete();
                Mockito.verify(ambIdentify, Mockito.never()).onPreExecutionComplete();
                return null;
            }
        }).when(ambIdentify.identifyTasks[0]).execute(Mockito.any(AmbIdentifyTask.OnCompleteListener.class));

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                AmbIdentifyTask.OnCompleteListener onCompleteListener = (AmbIdentifyTask.OnCompleteListener) invocation.getArguments()[0];
                onCompleteListener.complete();
                Mockito.verify(ambIdentify).onPreExecutionComplete();
                return null;
            }
        }).when(ambIdentify.identifyTasks[1]).execute(Mockito.any(AmbIdentifyTask.OnCompleteListener.class));

        ambIdentify.execute();
    }

    @Test
    public void testsExecuteDoesContinueWhenTasksThrowExceptions() throws Exception {
        ambIdentify.identifyTasks = new AmbIdentifyTask[]{ Mockito.mock(AmbAugurTask.class), Mockito.mock(AmbGcmTokenTask.class) };
        Mockito.doThrow(new RuntimeException()).when(ambIdentify.identifyTasks[0]).execute(Mockito.any(AmbIdentifyTask.OnCompleteListener.class));
        Mockito.doThrow(new RuntimeException()).when(ambIdentify.identifyTasks[1]).execute(Mockito.any(AmbIdentifyTask.OnCompleteListener.class));

        Mockito.doNothing().when(ambIdentify).onPreExecutionComplete();
        Mockito.doNothing().when(ambIdentify).setupPusher();

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                AmbIdentifyTask.OnCompleteListener onCompleteListener = (AmbIdentifyTask.OnCompleteListener) invocation.getArguments()[0];
                onCompleteListener.complete();
                Mockito.verify(ambIdentify, Mockito.never()).onPreExecutionComplete();
                return null;
            }
        }).when(ambIdentify.identifyTasks[0]).execute(Mockito.any(AmbIdentifyTask.OnCompleteListener.class));

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                AmbIdentifyTask.OnCompleteListener onCompleteListener = (AmbIdentifyTask.OnCompleteListener) invocation.getArguments()[0];
                onCompleteListener.complete();
                Mockito.verify(ambIdentify).onPreExecutionComplete();
                return null;
            }
        }).when(ambIdentify.identifyTasks[1]).execute(Mockito.any(AmbIdentifyTask.OnCompleteListener.class));

        ambIdentify.execute();
    }

    @Test
    public void testsExecuteDoesClearAndSetEmail() throws Exception {
        Mockito.doNothing().when(ambIdentify).onPreExecutionComplete();
        Mockito.doNothing().when(ambIdentify).setupPusher();
        ambIdentify.execute();
        Mockito.verify(user).clear();
        Mockito.verify(user).setEmail(Mockito.eq("jake@getambassador.com"));
    }

    @Test
    public void testsPostExecuteDoesIdentifyIfSubscribed() {
        ambIdentify.subscribed = true;

        ambIdentify.onPreExecutionComplete();

        Mockito.verify(requestManager, Mockito.times(1)).identifyRequest(Mockito.any(PusherManager.class), Mockito.any(RequestManager.RequestCompletion.class));
    }

}
