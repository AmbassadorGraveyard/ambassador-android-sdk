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
        Contact contact = new Contact.Builder()
                .setName(name)
                .setThumbnailUri(thumb)
                .setPictureUri(pic)
                .setEmailAddress(email)
                .setType(type)
                .setPhoneNumber(phone)
                .build();

        contact.setThumbnailBitmap(thumbBmp);
        contact.setPictureBitmap(picBmp);

        // ASSERT
        Assert.assertEquals(name, contact.getName());
        Assert.assertEquals(thumb, contact.getThumbnailUri());
        Assert.assertEquals(pic, contact.getPictureUri());
        Assert.assertEquals(email, contact.getEmailAddress());
        Assert.assertEquals(type, contact.getType());
        Assert.assertEquals(phone, contact.getPhoneNumber());
        Assert.assertEquals(picBmp, contact.getPictureBitmap());
        Assert.assertEquals(thumbBmp, contact.getThumbnailBitmap());
    }

    @Test
    public void sortTest() {
        // ARRANGE
        List<Contact> toSort = new ArrayList<>();
        toSort.add(new Contact.Builder().setName("a").build());
        toSort.add(new Contact.Builder().setName("b").build());
        toSort.add(new Contact.Builder().setName("c").build());
        toSort.add(new Contact.Builder().setName("d").build());

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
        Contact contact = new Contact.Builder()
                .setName("name")
                .setThumbnailUri("thumb")
                .setPictureUri("pic")
                .setEmailAddress("email@test.com")
                .build();

        // ACT
        Contact contactNew = contact.copy();

        // ASSERT
        Assert.assertNotSame(contact, contactNew);
        Assert.assertEquals(contact.getName(), contactNew.getName());
        Assert.assertEquals(contact.getEmailAddress(), contactNew.getEmailAddress());
        Assert.assertEquals(contact.getThumbnailUri(), contactNew.getThumbnailUri());
        Assert.assertEquals(contact.getPictureUri(), contactNew.getPictureUri());
        Assert.assertEquals(contact.getPhoneNumber(), contactNew.getPhoneNumber());
        Assert.assertEquals(contact.getType(), contactNew.getType());
    }

    @Test
    public void clonePhoneTest() {
        // ARRANGE
        Contact contact = new Contact.Builder()
                .setName("name")
                .setThumbnailUri("thumb")
                .setPictureUri("pic")
                .setType("mobile")
                .setPhoneNumber("3133291104")
                .build();

        // ACT
        Contact contactNew = contact.copy();

        // ASSERT
        Assert.assertNotSame(contact, contactNew);
        Assert.assertEquals(contact.getName(), contactNew.getName());
        Assert.assertEquals(contact.getEmailAddress(), contactNew.getEmailAddress());
        Assert.assertEquals(contact.getThumbnailUri(), contactNew.getThumbnailUri());
        Assert.assertEquals(contact.getPictureUri(), contactNew.getPictureUri());
        Assert.assertEquals(contact.getPhoneNumber(), contactNew.getPhoneNumber());
        Assert.assertEquals(contact.getType(), contactNew.getType());
    }

    @Test
    public void copyAllTest() {
        // ARRANGE
        Contact contact = new Contact.Builder()
                .setName("name")
                .setThumbnailUri("thumb")
                .setPictureUri("pic")
                .setEmailAddress("email@test.com")
                .setType("mobile")
                .setPhoneNumber("3133291104")
                .build();

        // ACT
        Contact contactNew = contact.copy();

        // ASSERT
        Assert.assertNotSame(contact, contactNew);
        Assert.assertEquals(contact.getName(), contactNew.getName());
        Assert.assertEquals(contact.getEmailAddress(), contactNew.getEmailAddress());
        Assert.assertEquals(contact.getThumbnailUri(), contactNew.getThumbnailUri());
        Assert.assertEquals(contact.getPictureUri(), contactNew.getPictureUri());
        Assert.assertEquals(contact.getPhoneNumber(), contactNew.getPhoneNumber());
        Assert.assertEquals(contact.getType(), contactNew.getType());
    }

}
