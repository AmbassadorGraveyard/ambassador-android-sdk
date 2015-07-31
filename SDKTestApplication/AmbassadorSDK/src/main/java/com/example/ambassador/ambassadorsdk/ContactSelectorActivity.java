package com.example.ambassador.ambassadorsdk;

import android.animation.Animator;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by JakeDunahee on 7/31/15.
 */
public class ContactSelectorActivity extends ActionBarActivity {
    private ListView lvContacts;
    private ArrayList<ContactObject> contactList;
    private ArrayList<ContactObject> selectedContactList;
    public Boolean showPhoneNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        showPhoneNumbers = getIntent().getBooleanExtra("showPhoneNumbers", true);

        if (showPhoneNumbers) {
            getContactPhoneList();
        } else {
            getContactEmailList();
        }

        selectedContactList = new ArrayList<>();

        lvContacts = (ListView)findViewById(R.id.lvContacts);

        final ContactListAdapter adapter = new ContactListAdapter(this,
                contactList, showPhoneNumbers);

        lvContacts.setAdapter(adapter);

        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView imageView = (ImageView)view.findViewById(R.id.ivCheckMark);
                float checkMarkX;
                if (adapter.selectedContacts.contains(contactList.get(position))) {
                    adapter.selectedContacts.remove(contactList.get(position));
                    checkMarkX = imageView.getWidth() + 15;
                } else {
                    adapter.selectedContacts.add(contactList.get(position));
                    checkMarkX = -(imageView.getWidth() + 15);
                }

                imageView.animate().setDuration(300).setInterpolator(new BounceInterpolator()).translationXBy(checkMarkX).start();
            }
        });
    }

    void getContactPhoneList() {
        contactList = new ArrayList<>();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        while (phones.moveToNext()) {
            ContactObject object = new ContactObject();
            String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));

            String Name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            object.name = Name;

            String Number=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            object.phoneNumber = Number;

            System.out.println(object.emailAddress);
            contactList.add(object);
        }

//        Collections.sort(contactList, String.CASE_INSENSITIVE_ORDER);
    }

    void getContactEmailList() {
        contactList = new ArrayList<>();
        Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null, null, null, null);

        while (emails.moveToNext()) {
            ContactObject object = new ContactObject();
            String id = emails.getString(emails.getColumnIndex(ContactsContract.Contacts._ID));

            String Name=emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            object.name = Name;

            String Number=emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            object.emailAddress = Number;

            System.out.println(object.emailAddress);
            contactList.add(object);
        }
    }
}
