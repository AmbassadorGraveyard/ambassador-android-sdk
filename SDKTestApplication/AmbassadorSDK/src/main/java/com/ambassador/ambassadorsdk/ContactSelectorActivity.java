package com.ambassador.ambassadorsdk;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by JakeDunahee on 7/31/15.
 */
public class ContactSelectorActivity extends AppCompatActivity implements ContactNameDialog.ContactNameListener {
    private static final int CHECK_CONTACT_PERMISSIONS = 1;

    private ListView lvContacts;
    private Button btnSend;
    private ImageButton btnEdit;
    private Button btnDone;
    private EditText etShareMessage, etSearch;
    private RelativeLayout rlSearch;
    private LinearLayout llSendView;
    private List<ContactObject> contactList;
    private InputMethodManager inputManager;
    private JSONObject pusherData;
    private TextView tvNoContacts;
    private ContactListAdapter adapter;
    private ProgressDialog pd;
    private ContactNameDialog cnd;
    Boolean showPhoneNumbers;

    @Inject
    BulkShareHelper bulkShareHelper;

    @Inject
    AmbassadorConfig ambassadorConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utilities.setStatusBar(getWindow(), getResources().getColor(R.color.homeToolBar));

        setContentView(R.layout.activity_contacts);

        if (!AmbassadorSingleton.isValid()) {
            finish();
            return;
        }

        AmbassadorSingleton.getComponent().inject(this);

        lvContacts = (ListView)findViewById(R.id.lvContacts);
        Button btnDoneSearch = (Button) findViewById(R.id.btnDoneSearch);
        btnEdit = (ImageButton)findViewById(R.id.btnEdit);
        btnDone = (Button)findViewById(R.id.btnDone);
        btnSend = (Button)findViewById(R.id.btnSend);
        etShareMessage = (EditText) findViewById(R.id.etShareMessage);
        rlSearch = (RelativeLayout)findViewById(R.id.rlSearch);
        etSearch = (EditText) findViewById(R.id.etSearch);
        llSendView = (LinearLayout) findViewById(R.id.llSendView);
        tvNoContacts = (TextView) findViewById(R.id.tvNoContacts);
        inputManager = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        _setUpToolbar(ambassadorConfig.getRafParameters().toolbarTitle);

        //setup progress dialog only once
        pd = new ProgressDialog(this);
        pd.setMessage("Sharing");
        pd.setOwnerActivity(this);
        pd.setCancelable(false);

        if (_handleContactsPermission()) {
            _handleContactsPopulation();
        }

        // Sets share message to default message from RAF Parameters
        etShareMessage.setText(ambassadorConfig.getRafParameters().defaultShareMessage);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _sendToContacts();
            }
        });

        btnDoneSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _displayOrHideSearch();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editBtnTapped();
            }
        });
        btnEdit.setColorFilter(getResources().getColor(R.color.ultraLightGray));

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _doneEditingMessage();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filterList(etSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //get and store pusher data
        try {
            pusherData = new JSONObject(ambassadorConfig.getPusherInfo());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        if (cnd != null) { cnd.dismiss(); }
        super.onPause();
    }

    //region TOOLBAR MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ambassador_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final Drawable searchIcon = ContextCompat.getDrawable(this, R.drawable.abc_ic_search_api_mtrl_alpha);
        searchIcon.setColorFilter(getResources().getColor(R.color.contactsSearchIcon), PorterDuff.Mode.SRC_ATOP);
        searchItem.setIcon(searchIcon);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            _displayOrHideSearch();
        } else {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    //endregion


    //region CONTACT FUNCTIONS
    private void _getContactPhoneList() {
        contactList = new ArrayList<>();

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if (phones.moveToFirst()) {
            do {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                String typeNum = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                String type;
                switch (Integer.parseInt(typeNum)) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        type = "Home";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        type = "Mobile";
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                        type = "Work";
                        break;
                    default:
                        type = "Other";
                        break;
                }

                ContactObject object = new ContactObject(name, type, phoneNumber);

                contactList.add(object);
            } while (phones.moveToNext());
        }

        if (!AmbassadorConfig.isReleaseBuild && contactList.size() < 2) {
            contactList.add(new ContactObject("Cool Guy", "Mobile", "123-345-9999"));
            contactList.add(new ContactObject("Cool Guy", "Mobile", "123-345-9999"));
            contactList.add(new ContactObject("Cool Guy", "Mobile", "123-345-9999"));
            contactList.add(new ContactObject("Cool Guy", "Mobile", "123-345-9999"));
            contactList.add(new ContactObject("Cool Guy", "Mobile", "123-345-9999"));
            contactList.add(new ContactObject("Cool Guy", "Mobile", "123-345-9999"));
            contactList.add(new ContactObject("Cool Guy", "Mobile", "123-345-9999"));
            contactList.add(new ContactObject("Cool Guy", "Mobile", "123-345-9999"));
            contactList.add(new ContactObject("Cool Guy", "Mobile", "123-345-9999"));
        }

        if (contactList.size() < 1) {
            tvNoContacts.setVisibility(View.VISIBLE);
        }

        phones.close();
        _sortContactsAlphabetically();
    }

    private void _getContactEmailList() {
        contactList = new ArrayList<>();



        Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null, null, null, null);

        if (emails.moveToFirst()) {
            do  {
                String name = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                ContactObject object = new ContactObject(name, emailAddress);
                contactList.add(object);
            }
            while (emails.moveToNext());
        }

        if (!AmbassadorConfig.isReleaseBuild && contactList.size() < 2) {
            contactList.add(new ContactObject("John Jones", "corey@getambassador.com"));
            contactList.add(new ContactObject("Cool Guy", "corey@getambassador.com"));
            contactList.add(new ContactObject("Friend One", "corey@getambassador.com"));
            contactList.add(new ContactObject("John Doe", "corey@getambassador.com"));
            contactList.add(new ContactObject("Greg Lastname", "corey@getambassador.com"));
            contactList.add(new ContactObject("Mike Ambassador", "corey@getambassador.com"));
            contactList.add(new ContactObject("Cool Friend", "corey@getambassador.com"));
            contactList.add(new ContactObject("Brian Davidson", "corey@getambassador.com"));
            contactList.add(new ContactObject("Jim Harbaugh", "corey@getambassador.com"));
            contactList.add(new ContactObject("Ambassador Diplomat", "corey@getambassador.com"));
            contactList.add(new ContactObject("Cool Guy", "corey@getambassador.com"));
            contactList.add(new ContactObject("Friend One", "corey@getambassador.com"));
            contactList.add(new ContactObject("John Doe", "corey@getambassador.com"));
            contactList.add(new ContactObject("Greg Lastname", "corey@getambassador.com"));
            contactList.add(new ContactObject("Mike Ambassador", "corey@getambassador.com"));
            contactList.add(new ContactObject("Cool Friend", "corey@getambassador.com"));
            contactList.add(new ContactObject("Brian Davidson", "corey@getambassador.com"));
            contactList.add(new ContactObject("Jim Harbaugh", "corey@getambassador.com"));
            contactList.add(new ContactObject("Ambassador Diplomat", "corey@getambassador.com"));
            contactList.add(new ContactObject("Ambassador Diplomat2", "corey@getambassador.com"));
        }

        if (contactList.size() < 1) {
            tvNoContacts.setVisibility(View.VISIBLE);
        }

        emails.close();
        _sortContactsAlphabetically();
    }

    private void _sortContactsAlphabetically() {
        Collections.sort(contactList);
    }
    //endregion


    // BUTTON METHODS
    private void _editBtnTapped() {
        btnSend.setEnabled(false);
        btnEdit.setVisibility(View.GONE);
        btnDone.setVisibility(View.VISIBLE);
        etShareMessage.setEnabled(true);
        etShareMessage.requestFocus();
        etShareMessage.setSelection(0);
        inputManager.showSoftInput(etShareMessage, 0); // Presents keyboard
    }

    private void _doneEditingMessage() {
        if (adapter.selectedContacts.size() > 0) btnSend.setEnabled(true);
        btnEdit.setVisibility(View.VISIBLE);
        btnDone.setVisibility(View.GONE);
        etShareMessage.setEnabled(false);
    }

    private void _displayOrHideSearch() {
        // Float that helps converts dp to pixels based on device
        final float scale = Utilities.getScreenDensity();

        int finalHeight = (rlSearch.getHeight() > 0) ? 0 : (int) (50 * scale + 0.5f);

        ValueAnimator anim = ValueAnimator.ofInt(rlSearch.getMeasuredHeight(), finalHeight);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer) animation.getAnimatedValue();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlSearch.getLayoutParams();
                layoutParams.height = val;
                rlSearch.setLayoutParams(layoutParams);
            }
        });

        if (finalHeight != 0) {
            // If SHOWING search
            _shrinkSendView(true);
            etSearch.requestFocus();
            inputManager.showSoftInput(etSearch, 0);
        } else {
            // If HIDING search
            etSearch.setText("");
            _shrinkSendView(false);
            etSearch.clearFocus();
            inputManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        }

        anim.setDuration(300);
        anim.start();
    }

    private void _shrinkSendView(Boolean shouldShrink) {
        // Functionality: Hides the send view while user is searching.  Mainly to make more room to see listview
        if (shouldShrink) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)llSendView.getLayoutParams();
            params.height = 0;
            llSendView.setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)llSendView.getLayoutParams();
            params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            llSendView.setLayoutParams(params);
        }
    }
    // END BUTTON METHODS

    // Adds and styles toolbar in place of the actionbar
    private void _setUpToolbar(String toolbarTitle) {
        if (getSupportActionBar() != null) { getSupportActionBar().setTitle(toolbarTitle); }

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        if (toolbar == null) return;

        final Drawable arrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        arrow.setColorFilter(getResources().getColor(R.color.contactsToolBarArrow), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(arrow);

        toolbar.setBackgroundColor(getResources().getColor(R.color.contactsToolBar));
        toolbar.setTitleTextColor(getResources().getColor(R.color.contactsToolBarText));
    }

    private void _updateSendButton(int numOfContacts) {
        if (numOfContacts == 0) {
            btnSend.setText("NO CONTACTS SELECTED");
            btnSend.setEnabled(false);
            return;
        }

        if (!etShareMessage.isEnabled() && !btnSend.isEnabled()) btnSend.setEnabled(true);
        String btnSendText = "SEND TO " + numOfContacts;
        btnSendText += (numOfContacts > 1) ? " CONTACTS" : " CONTACT";
        btnSend.setText(btnSendText);
    }

    private void _sendToContacts() {
        if (Utilities.containsURL(etShareMessage.getText().toString(), ambassadorConfig.getURL())) {
            //get and store pusher data
            try {
                //if user is doing sms and we don't have first or last name, we need to get it with a dialog
                if (showPhoneNumbers && //FOR TESTING INCLUDE THIS -->  true || //remove "true ||" for launch
                        (!pusherData.has("firstName") || pusherData.getString("firstName").equals("null") || pusherData.getString("firstName").isEmpty()
                                ||
                                !pusherData.has("lastName") || pusherData.getString("lastName").equals("null") || pusherData.getString("lastName").isEmpty())) {
                    //show dialog to get name
                    cnd = new ContactNameDialog(this, pd);
                    cnd.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            cnd.showKeyboard();
                        }
                    });
                    cnd.show();
                } else {
                    _initiateSend();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Utilities.presentUrlDialog(this, etShareMessage, ambassadorConfig.getURL(), new Utilities.UrlAlertInterface() {
                @Override
                public void sendAnywayTapped(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                    _initiateSend();
                }

                @Override
                public void insertUrlTapped(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                }
            });
        }
    }

    private void _initiateSend() {
        //this method is called from two places, one of which could already be showing the progress dialog
        if (!pd.isShowing()) pd.show();

        bulkShareHelper.bulkShare(etShareMessage.getText().toString(), adapter.selectedContacts, showPhoneNumbers, new BulkShareHelper.BulkShareCompletion() {
            @Override
            public void bulkShareSuccess() {
                pd.dismiss();
                finish();
                Toast.makeText(getApplicationContext(), "Message successfully shared!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void bulkShareFailure() {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Unable to share message. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Interface call from ContactNameDialog
    @Override
    public void namesHaveBeenUpdated() {
        _initiateSend();
    }

    private void _handleContactsPopulation() {
        // Finds out whether to show emails or phone numbers
        showPhoneNumbers = getIntent().getBooleanExtra("showPhoneNumbers", true);
        if (showPhoneNumbers) {
            _getContactPhoneList();
        } else {
            _getContactEmailList();
        }

        adapter = new ContactListAdapter(this, contactList, showPhoneNumbers);
        lvContacts.setAdapter(adapter);
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get checkmark image and animate in or out based on its selection state and updates arrays
                adapter.updateArrays(position, view);
                _updateSendButton(adapter.selectedContacts.size());
            }
        });
    }

    private boolean _handleContactsPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true; // have permission, proceed as normal
        } else if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, CHECK_CONTACT_PERMISSIONS);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CHECK_CONTACT_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    _handleContactsPopulation();
                } else {
                    // Permission denied, kick em out
                    Utilities.presentNonCancelableMessageDialog(this, getString(R.string.contacts_permission_denied_title), getString(R.string.contacts_permission_denied_message), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                }
                break;
        }
    }
}