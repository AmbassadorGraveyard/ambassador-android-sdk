package com.ambassador.ambassadorsdk.internal;

import android.graphics.Bitmap;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
    public void getterSetterTest() {
        // ARRANGE
        String name = "name";
        String thumb = "thumb";
        String pic = "pic";
        String email = "email";
        String type = "type";
        String phone = "phone";
        Bitmap thumbBmp = Mockito.mock(Bitmap.class);
        Bitmap picBmp = Mockito.mock(Bitmap.class);

        // ACT
        ContactObject contactObject = new ContactObject.Builder()
                .setName(name)
                .setThumbnailUri(thumb)
                .setPictureUri(pic)
                .setEmailAddress(email)
                .setType(type)
                .setPhoneNumber(phone)
                .build();

        contactObject.setThumbnailBitmap(thumbBmp);
        contactObject.setPictureBitmap(picBmp);

        // ASSERT
        Assert.assertEquals(name, contactObject.getName());
        Assert.assertEquals(thumb, contactObject.getThumbnailUri());
        Assert.assertEquals(pic, contactObject.getPictureUri());
        Assert.assertEquals(email, contactObject.getEmailAddress());
        Assert.assertEquals(type, contactObject.getType());
        Assert.assertEquals(phone, contactObject.getPhoneNumber());
        Assert.assertEquals(picBmp, contactObject.getPictureBitmap());
        Assert.assertEquals(thumbBmp, contactObject.getThumbnailBitmap());
    }

    @Test
    public void sortTest() {
        // ARRANGE
        List<ContactObject> toSort = new ArrayList<>();
        toSort.add(new ContactObject.Builder().setName("a").build());
        toSort.add(new ContactObject.Builder().setName("b").build());
        toSort.add(new ContactObject.Builder().setName("c").build());
        toSort.add(new ContactObject.Builder().setName("d").build());

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
        ContactObject contactObject = new ContactObject.Builder()
                .setName("name")
                .setThumbnailUri("thumb")
                .setPictureUri("pic")
                .setEmailAddress("email@test.com")
                .build();

        // ACT
        ContactObject contactObjectNew = contactObject.copy();

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
        ContactObject contactObject = new ContactObject.Builder()
                .setName("name")
                .setThumbnailUri("thumb")
                .setPictureUri("pic")
                .setType("mobile")
                .setPhoneNumber("3133291104")
                .build();

        // ACT
        ContactObject contactObjectNew = contactObject.copy();

        // ASSERT
        Assert.assertNotSame(contactObject, contactObjectNew);
        Assert.assertEquals(contactObject.getName(), contactObjectNew.getName());
        Assert.assertEquals(contactObject.getEmailAddress(), contactObjectNew.getEmailAddress());
        Assert.assertEquals(contactObject.getThumbnailUri(), contactObjectNew.getThumbnailUri());
        Assert.assertEquals(contactObject.getPictureUri(), contactObjectNew.getPictureUri());
        Assert.assertEquals(contactObject.getPhoneNumber(), contactObjectNew.getPhoneNumber());
        Assert.assertEquals(contactObject.getType(), contactObjectNew.getType());
    }

    public void copyAllTest() {
        // ARRANGE
        ContactObject contactObject = new ContactObject.Builder()
                .setName("name")
                .setThumbnailUri("thumb")
                .setPictureUri("pic")
                .setEmailAddress("email@test.com")
                .setType("mobile")
                .setPhoneNumber("3133291104")
                .build();

        // ACT
        ContactObject contactObjectNew = contactObject.copy();

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
