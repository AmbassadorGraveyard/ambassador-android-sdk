package com.ambassador.ambassadorsdk.internal.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.ambassador.ambassadorsdk.internal.models.Contact;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        ContactList.class
})
public class ContactListTest {

    private Context context;
    private ContentResolver contentResolver;
    private Cursor cursor;

    @Before
    public void setUp() {
        context = Mockito.mock(Context.class);
        contentResolver = Mockito.mock(ContentResolver.class);
        cursor = Mockito.mock(Cursor.class);

        Mockito.when(context.getContentResolver()).thenReturn(contentResolver);
    }

    @Test
    public void getPhoneListTest() {
        // ARRANGE
        ContactList list = PowerMockito.spy(new ContactList(ContactList.Type.PHONE));

        Mockito.when(contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)).thenReturn(cursor);
        Mockito.when(cursor.moveToFirst()).thenReturn(true);
        Mockito.when(cursor.moveToNext()).thenReturn(true).thenReturn(true).thenReturn(false);

        Mockito.when(cursor.getColumnIndex(Mockito.eq(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))).thenReturn(1);
        Mockito.when(cursor.getString(Mockito.eq(1))).thenReturn("name1").thenReturn("name2").thenReturn("name3");

        Mockito.when(cursor.getColumnIndex(Mockito.eq(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))).thenReturn(2);
        Mockito.when(cursor.getString(Mockito.eq(2))).thenReturn("thumb1").thenReturn("thumb2").thenReturn("thumb3");

        Mockito.when(cursor.getColumnIndex(Mockito.eq(ContactsContract.Contacts.PHOTO_URI))).thenReturn(3);
        Mockito.when(cursor.getString(Mockito.eq(3))).thenReturn("photo1").thenReturn("photo2").thenReturn("photo3");

        Mockito.when(cursor.getColumnIndex(Mockito.eq(ContactsContract.CommonDataKinds.Phone.NUMBER))).thenReturn(4);
        Mockito.when(cursor.getString(Mockito.eq(4))).thenReturn("number1").thenReturn("number2").thenReturn("number3");

        Mockito.when(cursor.getColumnIndex(Mockito.eq(ContactsContract.CommonDataKinds.Phone.TYPE))).thenReturn(5);
        Mockito.when(cursor.getString(Mockito.eq(5))).thenReturn("1").thenReturn("2").thenReturn("3");

        Mockito.when(list.getPhoneType(1)).thenReturn("Home");
        Mockito.when(list.getPhoneType(2)).thenReturn("Mobile");
        Mockito.when(list.getPhoneType(3)).thenReturn("Other");

        // ACT
        List<Contact> contactList = list.get(context);

        // ASSERT
        Assert.assertEquals(3, contactList.size());
        Assert.assertEquals("name1", contactList.get(0).getName());
        Assert.assertEquals("name2", contactList.get(1).getName());
        Assert.assertEquals("name3", contactList.get(2).getName());

        Assert.assertEquals("thumb1", contactList.get(0).getThumbnailUri());
        Assert.assertEquals("thumb2", contactList.get(1).getThumbnailUri());
        Assert.assertEquals("thumb3", contactList.get(2).getThumbnailUri());

        Assert.assertEquals("photo1", contactList.get(0).getPictureUri());
        Assert.assertEquals("photo2", contactList.get(1).getPictureUri());
        Assert.assertEquals("photo3", contactList.get(2).getPictureUri());

        Assert.assertEquals("number1", contactList.get(0).getPhoneNumber());
        Assert.assertEquals("number2", contactList.get(1).getPhoneNumber());
        Assert.assertEquals("number3", contactList.get(2).getPhoneNumber());

        Assert.assertEquals("Home", contactList.get(0).getType());
        Assert.assertEquals("Mobile", contactList.get(1).getType());
        Assert.assertEquals("Other", contactList.get(2).getType());
    }

    @Test
    public void getPhoneListNullCursorTest() {
        // ARRANGE
        Mockito.when(contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)).thenReturn(null);

        // ACT
        List<Contact> contactList = new ContactList(ContactList.Type.PHONE).get(context);

        // ASSERT
        Assert.assertTrue(contactList.isEmpty());
    }

    @Test
    public void getEmailListTest() {
        // ARRANGE
        ContactList list = PowerMockito.spy(new ContactList(ContactList.Type.EMAIL));

        Mockito.when(contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)).thenReturn(cursor);
        Mockito.when(cursor.moveToFirst()).thenReturn(true);
        Mockito.when(cursor.moveToNext()).thenReturn(true).thenReturn(true).thenReturn(false);

        Mockito.when(cursor.getColumnIndex(Mockito.eq(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))).thenReturn(1);
        Mockito.when(cursor.getString(Mockito.eq(1))).thenReturn("name1").thenReturn("name2").thenReturn("name3");

        Mockito.when(cursor.getColumnIndex(Mockito.eq(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))).thenReturn(2);
        Mockito.when(cursor.getString(Mockito.eq(2))).thenReturn("thumb1").thenReturn("thumb2").thenReturn("thumb3");

        Mockito.when(cursor.getColumnIndex(Mockito.eq(ContactsContract.Contacts.PHOTO_URI))).thenReturn(3);
        Mockito.when(cursor.getString(Mockito.eq(3))).thenReturn("photo1").thenReturn("photo2").thenReturn("photo3");

        Mockito.when(cursor.getColumnIndex(Mockito.eq(ContactsContract.CommonDataKinds.Phone.NUMBER))).thenReturn(4);
        Mockito.when(cursor.getString(Mockito.eq(4))).thenReturn("email1").thenReturn("email2").thenReturn("email3");

        // ACT
        List<Contact> contactList = list.get(context);

        // ASSERT
        Assert.assertEquals(3, contactList.size());
        Assert.assertEquals("name1", contactList.get(0).getName());
        Assert.assertEquals("name2", contactList.get(1).getName());
        Assert.assertEquals("name3", contactList.get(2).getName());

        Assert.assertEquals("thumb1", contactList.get(0).getThumbnailUri());
        Assert.assertEquals("thumb2", contactList.get(1).getThumbnailUri());
        Assert.assertEquals("thumb3", contactList.get(2).getThumbnailUri());

        Assert.assertEquals("photo1", contactList.get(0).getPictureUri());
        Assert.assertEquals("photo2", contactList.get(1).getPictureUri());
        Assert.assertEquals("photo3", contactList.get(2).getPictureUri());

        Assert.assertEquals("email1", contactList.get(0).getEmailAddress());
        Assert.assertEquals("email2", contactList.get(1).getEmailAddress());
        Assert.assertEquals("email3", contactList.get(2).getEmailAddress());
    }

    @Test
    public void getEmailListNullCursorTest() {
        // ARRANGE
        Mockito.when(contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null, null, null)).thenReturn(null);

        // ACT
        List<Contact> contactList = new ContactList(ContactList.Type.EMAIL).get(context);

        // ASSERT
        Assert.assertTrue(contactList.isEmpty());
    }

    @Test
    public void getDummyListTest() {
        // ACT
        List<Contact> contactList = new ContactList(ContactList.Type.DUMMY).get(context);

        // ASSERT
        Assert.assertEquals(100, contactList.size());
        Assert.assertNotNull(contactList.get(0).getName());
        Assert.assertNotNull(contactList.get(0).getEmailAddress());
        Assert.assertNotNull(contactList.get(0).getPhoneNumber());
        Assert.assertNotNull(contactList.get(0).getType());
    }

}
