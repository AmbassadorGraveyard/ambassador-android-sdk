package com.ambassador.ambassadorsdk.internal;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Component;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyFloat;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Utilities.class
})
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
    public void filterListTest() throws Exception {
        // ARRANGE
        Activity mockActivity = mock(Activity.class);
        List<ContactObject> contactObjects = new ArrayList<>();
        contactObjects.add(new ContactObject("1", "1", "test", "test"));
        contactObjects.add(new ContactObject("catdog", "2", "test", "test"));
        contactObjects.add(new ContactObject("cadog", "ca", "test", "test"));
        contactObjects.add(new ContactObject("cat", "4", "test", "test"));

        ContactListAdapter contactListAdapter = new ContactListAdapter(mockActivity, contactObjects, false);
        ContactListAdapter spy = Mockito.spy(contactListAdapter);
        //List<ContactObject> selectedContacts = spy.getSelectedContacts();
        doNothing().when(spy).notifyDataSetChanged();

        Field f = contactListAdapter.getClass().getDeclaredField("filteredContactList");
        f.setAccessible(true);
        ArrayList<ContactObject> filteredList;

        // ACT
        spy.filterList(null);

        // ASSERT
        filteredList = (ArrayList) f.get(spy);
        assertTrue(filteredList.size() == contactObjects.size());

        // ACT
        spy.filterList("");

        // ASSERT
        filteredList = (ArrayList) f.get(spy);
        assertTrue(filteredList.size() == contactObjects.size());

        // ACT
        spy.filterList("cat");

        // ASSERT
        filteredList = (ArrayList) f.get(spy);
        assertTrue(filteredList.size() == 2);

        // ACT
        spy.filterList("ca");

        // ASSERT
        filteredList = (ArrayList) f.get(spy);
        assertTrue(filteredList.size() == 3);
    }

    @Test
    public void updateArraysTest() {
        // ARRANGE
        View mockView = Mockito.mock(View.class);
        ImageView mockImageView = Mockito.mock(ImageView.class);
        Activity mockActivity = Mockito.mock(Activity.class);

        ContactObject c1 = new ContactObject("1", "1", "test", "test");
        ContactObject c2 = new ContactObject("2", "2", "test", "test");
        ContactObject c3 = new ContactObject("3", "3", "test", "test");

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
        List<ContactObject> selectedContacts = contactListAdapter.getSelectedContacts();
        selectedContacts.add(c1);

        // ACT
        contactListAdapter.updateArrays(mockView, 0);

        // ASSERT
        assertTrue(!selectedContacts.contains(contactObjects.get(0)));

        // ARRANGE
        contactListAdapter = new ContactListAdapter(mockActivity, contactObjects, false);
        selectedContacts = contactListAdapter.getSelectedContacts();

        // ACT
        contactListAdapter.updateArrays(mockView, 0);

        // ASSERT
        assertTrue(selectedContacts.contains(contactObjects.get(0)));
    }

}
