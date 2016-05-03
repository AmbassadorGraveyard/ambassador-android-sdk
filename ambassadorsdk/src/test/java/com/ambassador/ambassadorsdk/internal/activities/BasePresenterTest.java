package com.ambassador.ambassadorsdk.internal.activities;

import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.ref.WeakReference;

@RunWith(PowerMockRunner.class)
public class BasePresenterTest {

    protected BasePresenter<Object, Object> basePresenter;

    @Before
    public void setUp() {
        basePresenter = new BasePresenter<Object, Object>() {
            @Override
            protected void updateView() {
                // Mock, not implemented.
            }
        };

        basePresenter = Mockito.spy(basePresenter);
    }

    @Test
    public void testsSetModelDoesResetAndUpdateWhenSetupDone() {
        basePresenter.view = new WeakReference<Object>(Mockito.mock(View.class));
        basePresenter.setModel(new Object());
        Mockito.verify(basePresenter).resetState();
        Mockito.verify(basePresenter).updateView();
    }

    @Test
    public void testsSetModelDoesResetAndNotUpdateWhenSetupNotDone() {
        basePresenter.view = new WeakReference<Object>(null);
        basePresenter.setModel(new Object());
        Mockito.verify(basePresenter).resetState();
        Mockito.verify(basePresenter, Mockito.never()).updateView();
    }

    @Test
    public void testsBindViewDoesUpdateViewWhenSetupDone() {
        basePresenter.model = new Object();
        basePresenter.bindView(Mockito.mock(View.class));
        Mockito.verify(basePresenter).updateView();
    }

    @Test
    public void testsBindViewDoesNotUpdateViewWhenSetupNotDone() {
        basePresenter.model = null;
        basePresenter.bindView(Mockito.mock(View.class));
        Mockito.verify(basePresenter, Mockito.never()).updateView();
    }

}
