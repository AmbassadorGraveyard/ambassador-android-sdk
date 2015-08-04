package com.example.ambassador.ambassadorsdk;

import android.animation.Animator;
import android.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.widget.ActionMenuPresenter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.facebook.internal.CollectionMapper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by JakeDunahee on 7/31/15.
 */
public class ContactSelectorActivity extends AppCompatActivity {
    private ListView lvContacts;
    private Button btnSend;
    private ImageButton btnEdit;
    private EditText etShareMessage;
    private ArrayList<ContactObject> contactList;
    private ArrayList<ContactObject> selectedContactList;
    private int originalSendButtonHeight;
    public Boolean showPhoneNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        setUpToolbar();

        showPhoneNumbers = getIntent().getBooleanExtra("showPhoneNumbers", true);

        if (showPhoneNumbers) {
            getContactPhoneList();
        } else {
            getContactEmailList();
        }

        sortContactsAlphabetically();

        selectedContactList = new ArrayList<>();

        lvContacts = (ListView)findViewById(R.id.lvContacts);
        btnEdit = (ImageButton)findViewById(R.id.btnEdit);
        btnSend = (Button)findViewById(R.id.btnSend);
        etShareMessage = (EditText) findViewById(R.id.etShareMessage);
        etShareMessage.setText(AmbassadorSingleton.getInstance().rafParameters.shareMessage);
        updateSendButton(0);

        final ContactListAdapter adapter = new ContactListAdapter(this,
                contactList, showPhoneNumbers);

        lvContacts.setAdapter(adapter);

        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView imageView = (ImageView) view.findViewById(R.id.ivCheckMark);
                if (adapter.selectedContacts.contains(contactList.get(position))) {
                    adapter.selectedContacts.remove(contactList.get(position));
                    imageView.animate().setDuration(100).x(view.getWidth()).start();
                } else {
                    adapter.selectedContacts.add(contactList.get(position));
                    imageView.animate().setDuration(300).setInterpolator(new BounceInterpolator())
                            .x(view.getWidth() - imageView.getWidth() - 15).start();
                }

                updateSendButton(adapter.selectedContacts.size());
            }
        });




        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etShareMessage.isEnabled() == true) {
                    doneEditingMessage();
                } else {
                    editBtnTapped();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ambassador_menu, menu);
        return true;
    }

    void getContactPhoneList() {
        contactList = new ArrayList<>();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        while (phones.moveToNext()) {
            ContactObject object = new ContactObject();
            String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));

            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            object.name = name;

            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            object.phoneNumber = phoneNumber;

            String typeNum = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

            switch (Integer.parseInt(typeNum)) {
                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    object.type = "Home";
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                    object.type = "Mobile";
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    object.type = "Work";
                    break;
                default:
                    object.type = "Other";
                    break;
            }

            contactList.add(object);
        }
    }

    void getContactEmailList() {
        contactList = new ArrayList<>();
        Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null, null, null, null);

        while (emails.moveToNext()) {
            ContactObject object = new ContactObject();
            String id = emails.getString(emails.getColumnIndex(ContactsContract.Contacts._ID));

            String name = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            object.name = name;

            String emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            object.emailAddress = emailAddress;

            contactList.add(object);
        }
    }

    void sortContactsAlphabetically() {
        Collections.sort(contactList, new Comparator<ContactObject>() {
            @Override
            public int compare(ContactObject lhs, ContactObject rhs) {
                return lhs.name.compareTo(rhs.name);
            }
        });
    }

    void editBtnTapped() {
        btnEdit.setImageResource(R.mipmap.done_button);
        originalSendButtonHeight = btnEdit.getHeight();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)btnSend.getLayoutParams();
        params.height = 0;
        btnSend.setLayoutParams(params);
        etShareMessage.setEnabled(true);
        etShareMessage.requestFocus();
        InputMethodManager lManager = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        lManager.showSoftInput(etShareMessage, 0);
    }

    void doneEditingMessage() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)btnSend.getLayoutParams();
        params.height = originalSendButtonHeight;
        btnSend.setLayoutParams(params);

        etShareMessage.setEnabled(false);
        btnEdit.setImageResource(R.mipmap.pencil_edit);
    }

    void setUpToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.action_bar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setTitle("Refer your friends");
        toolbar.setTitleTextColor(Color.DKGRAY);
        toolbar.getNavigationIcon().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);
        toolbar.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
//        toolbar.setMenu(new MenuBuilder(this), new ActionMenuPresenter(this));

        if (Build.VERSION.SDK_INT >= 21) {
            toolbar.setElevation(25);
        }
    }

    void updateSendButton(int numOfContacts) {
        if (numOfContacts > 0) {
            if (btnSend.isEnabled() == false) { btnSend.setEnabled(true); }
            btnSend.setText("SEND TO " + numOfContacts + " CONTACTS");
        } else {
            btnSend.setText("NO CONTACTS SELECTED");
            btnSend.setEnabled(false);
        }
    }
}




