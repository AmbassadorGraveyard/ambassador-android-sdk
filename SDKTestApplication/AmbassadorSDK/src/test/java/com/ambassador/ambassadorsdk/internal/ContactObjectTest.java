package com.ambassador.ambassadorsdk.internal;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({

})
public class ContactObjectTest {

    @Test
    public void sortTest() {
        // ARRANGE
        List<ContactObject> toSort = new ArrayList<>();
        toSort.add(new ContactObject("a", null, null, null));
        toSort.add(new ContactObject("b", null, null, null));
        toSort.add(new ContactObject("c", null, null, null));
        toSort.add(new ContactObject("d", null, null, null));

        // ACT
        Collections.shuffle(toSort);
        Collections.sort(toSort);

        // ASSERT
        Assert.assertTrue(toSort.get(0).getName().equals("a"));
        Assert.assertTrue(toSort.get(1).getName().equals("b"));
        Assert.assertTrue(toSort.get(2).getName().equals("c"));
        Assert.assertTrue(toSort.get(3).getName().equals("d"));
    }

    @Test
    public void cloneEmailTest() {
        // ARRANGE
        ContactObject contactObject = new ContactObject("name", "thumb", "pic", "email@google.com");

        // ACT
        ContactObject contactObjectNew = contactObject.clone();

        // ASSERT
        Assert.assertNotSame(contactObject, contactObjectNew);
        Assert.assertEquals(contactObject.getName(), contactObjectNew.getName());
        Assert.assertEquals(contactObject.getEmailAddress(), contactObjectNew.getEmailAddress());
        Assert.assertEquals(contactObject.getThumbnailUri(), contactObjectNew.getThumbnailUri());
        Assert.assertEquals(contactObject.getPictureUri(), contactObjectNew.getPictureUri());
        Assert.assertEquals(contactObject.getPhoneNumber(), contactObjectNew.getPhoneNumber());
        Assert.assertEquals(contactObject.getType(), contactObjectNew.getType());
    }

    @Test
    public void clonePhoneTest() {
        // ARRANGE
        ContactObject contactObject = new ContactObject("name1", "thumb1", "pic1", "mobile", "3133291104");

        // ACT
        ContactObject contactObjectNew = contactObject.clone();

        // ASSERT
        Assert.assertNotSame(contactObject, contactObjectNew);
        Assert.assertEquals(contactObject.getName(), contactObjectNew.getName());
        Assert.assertEquals(contactObject.getEmailAddress(), contactObjectNew.getEmailAddress());
        Assert.assertEquals(contactObject.getThumbnailUri(), contactObjectNew.getThumbnailUri());
        Assert.assertEquals(contactObject.getPictureUri(), contactObjectNew.getPictureUri());
        Assert.assertEquals(contactObject.getPhoneNumber(), contactObjectNew.getPhoneNumber());
        Assert.assertEquals(contactObject.getType(), contactObjectNew.getType());
    }

    public void cloneAllTest() {
        // ARRANGE
        ContactObject contactObject = new ContactObject("name1", "thumb2", "pic2", "email@test.com", "mobile", "3133291104");

        // ACT
        ContactObject contactObjectNew = contactObject.clone();

        // ASSERT
        Assert.assertNotSame(contactObject, contactObjectNew);
        Assert.assertEquals(contactObject.getName(), contactObjectNew.getName());
        Assert.assertEquals(contactObject.getEmailAddress(), contactObjectNew.getEmailAddress());
        Assert.assertEquals(contactObject.getThumbnailUri(), contactObjectNew.getThumbnailUri());
        Assert.assertEquals(contactObject.getPictureUri(), contactObjectNew.getPictureUri());
        Assert.assertEquals(contactObject.getPhoneNumber(), contactObjectNew.getPhoneNumber());
        Assert.assertEquals(contactObject.getType(), contactObjectNew.getType());
    }

}
