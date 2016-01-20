package com.ambassador.ambassadorsdk.internal.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.ambassador.ambassadorsdk.internal.models.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class ContactList {

    private Type type;

    public enum Type {
        PHONE, EMAIL, DUMMY
    }

    private ContactList() {}

    public ContactList(@NonNull Type type) {
        this.type = type;
    }

    @NonNull
    public List<Contact> get(@NonNull Context context) {
        switch (type) {
            case PHONE:
                return getPhoneList(context);

            case EMAIL:
                return getEmailList(context);

            case DUMMY:
                return getDummyList();

            default:
                return new ArrayList<>();
        }
    }

    @NonNull
    private List<Contact> getPhoneList(@NonNull Context context) {
        List<Contact> tmp = new ArrayList<>();
        Cursor phoneCursor = context.getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if (phoneCursor == null) {
            return tmp;
        }

        if (phoneCursor.moveToFirst()) {
            do {
                String name = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String thumbUri = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                String picUri = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String typeNum = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                String type = getPhoneType(Integer.parseInt(typeNum));

                Contact object = new Contact.Builder()
                        .setName(name)
                        .setPhoneNumber(phoneNumber)
                        .setType(type)
                        .setThumbnailUri(thumbUri)
                        .setPictureUri(picUri)
                        .build();

                tmp.add(object);
            } while (phoneCursor.moveToNext());
        }

        phoneCursor.close();
        Collections.sort(tmp);
        return tmp;
    }

    @NonNull
    private String getPhoneType(int typeNum) {
        switch (typeNum) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                return "Home";
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                return "Mobile";
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return "Work";
            default:
                return "Other";
        }
    }

    @NonNull
    private List<Contact> getEmailList(@NonNull Context context) {
        List<Contact> tmp = new ArrayList<>();
        Cursor emailCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null, null, null);

        if (emailCursor == null) {
            return tmp;
        }

        if (emailCursor.moveToFirst()) {
            do  {
                String name = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String thumbUri = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                String picUri = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                String emailAddress = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                Contact object = new Contact.Builder()
                        .setName(name)
                        .setEmailAddress(emailAddress)
                        .setThumbnailUri(thumbUri)
                        .setPictureUri(picUri)
                        .build();

                tmp.add(object);
            }
            while (emailCursor.moveToNext());
        }

        emailCursor.close();
        Collections.sort(tmp);
        return tmp;
    }

    @NonNull
    private List<Contact> getDummyList() {
        List<Contact> tmp = new ArrayList<>();

        String[] firstNames = new String[]{"Dylan", "Jake", "Corey", "Mitch", "Matt", "Brian", "Amanada", "Brandon"};
        String[] lastNames = new String[]{"Smith", "Johnson", "Stevens"};
        String[] types = new String[]{"Home", "Mobile", "Work"};
        String[] numbers = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"};

        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            String name = firstNames[rand.nextInt(firstNames.length)] + " " + lastNames[rand.nextInt(lastNames.length)];
            String email = name.substring(0, name.indexOf(" ")).toLowerCase() + "@getambassador.com";
            String type = types[rand.nextInt(types.length)];
            String phoneNumber = "";
            for (int j = 0; j < 12; j++) {
                if (j == 3 || j == 7) {
                    phoneNumber += "-";
                } else {
                    phoneNumber += numbers[rand.nextInt(numbers.length)];
                }
            }

            Contact contact = new Contact.Builder()
                    .setName(name)
                    .setEmailAddress(email)
                    .setPhoneNumber(phoneNumber)
                    .setType(type)
                    .build();

            tmp.add(contact);
        }

        Collections.sort(tmp);
        return tmp;
    }

}
