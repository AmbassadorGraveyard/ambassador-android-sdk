package com.ambassador.ambassadorsdk;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.widget.ImageView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Component;
import javassist.bytecode.analysis.Util;

import static junit.framework.Assert.assertTrue;


/**
 * Created by dylan on 11/09/15.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Utilities.class})
public class ContactListAdapterTest {

    @Singleton
    @Component(modules = {AmbassadorApplicationModule.class})
    public interface TestComponent {
        void inject(ContactListAdapterTest contactListAdapterTest);
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        AmbassadorApplicationModule amb = new AmbassadorApplicationModule();
        amb.setMockMode(true);

        TestComponent component = DaggerContactListAdapterTest_TestComponent.builder().ambassadorApplicationModule(amb).build();
        component.inject(this);

        PowerMockito.mockStatic(Utilities.class);
        when(Utilities.getPixelSizeForDimension(anyInt())).thenReturn(0);
    }

    @Test
    public void filterListTest() {
        // ARRANGE

        // ACT

        // ASSERT
    }

    @Test
    public void updateArraysTest() {
        // ARRANGE
        View mockView = Mockito.mock(View.class);
        ImageView mockImageView = Mockito.mock(ImageView.class);
        Activity mockActivity = Mockito.mock(Activity.class);

        ContactObject c1 = new ContactObject("1", "1");
        ContactObject c2 = new ContactObject("2", "2");
        ContactObject c3 = new ContactObject("3", "3");

        List<ContactObject> contactObjects = new ArrayList<>();
        contactObjects.add(c1);

        when(mockView.findViewById(anyInt())).thenReturn(mockImageView);
        when(mockView.getWidth()).thenReturn(0);
        when(mockImageView.getWidth()).thenReturn(0);

        ViewPropertyAnimator mockAnimator = mock(ViewPropertyAnimator.class);
        when(mockImageView.animate()).thenReturn(mockAnimator);

        when(mockAnimator.setDuration(anyLong())).thenReturn(mockAnimator);
        when(mockAnimator.x(anyFloat())).thenReturn(mockAnimator);
        when(mockAnimator.setListener(any(Animator.AnimatorListener.class))).thenReturn(mockAnimator);
        when(mockAnimator.setInterpolator(any(TimeInterpolator.class))).thenReturn(mockAnimator);
        doNothing().when(mockAnimator).start();

        doNothing().when(mockImageView).setVisibility(View.VISIBLE);

        ContactListAdapter contactListAdapter = new ContactListAdapter(mockActivity, contactObjects, false);
        List<ContactObject> selectedContacts = contactListAdapter.selectedContacts;
        selectedContacts.add(c1);

        // ACT
        contactListAdapter.updateArrays(0, mockView);

        // ASSERT
        assertTrue(!selectedContacts.contains(contactObjects.get(0)));

        // ARRANGE
        contactListAdapter = new ContactListAdapter(mockActivity, contactObjects, false);
        selectedContacts = contactListAdapter.selectedContacts;

        // ACT
        contactListAdapter.updateArrays(0, mockView);

        // ASSERT
        assertTrue(selectedContacts.contains(contactObjects.get(0)));
    }

}
