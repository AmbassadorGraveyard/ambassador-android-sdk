package com.example.ambassador.ambassadorsdk;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by JakeDunahee on 7/31/15.
 */
public class ContactSelectorActivity extends AppCompatActivity implements ContactNameDialog.ContactNameListener {
    private Button btnSend;
    private ImageButton btnEdit;
    private EditText etShareMessage, etSearch;
    private RelativeLayout rlSearch;
    private LinearLayout llSendView;
    private ArrayList<ContactObject> contactList;
    private InputMethodManager inputManager;
    private JSONObject pusherData;
    private TextView tvNoContacts;
    private ContactListAdapter adapter;
    private ProgressDialog pd;
    private ContactNameDialog cnd;
    Boolean showPhoneNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // Init UI components
        ListView lvContacts = (ListView)findViewById(R.id.lvContacts);
        btnEdit = (ImageButton)findViewById(R.id.btnEdit);
        btnSend = (Button)findViewById(R.id.btnSend);
        etShareMessage = (EditText) findViewById(R.id.etShareMessage);
        rlSearch = (RelativeLayout)findViewById(R.id.rlSearch);
        etSearch = (EditText) findViewById(R.id.etSearch);
        llSendView = (LinearLayout) findViewById(R.id.llSendView);
        tvNoContacts = (TextView) findViewById(R.id.tvNoContacts);
        inputManager = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        setUpToolbar();

        //setup progress dialog only once
        pd = new ProgressDialog(this);
        pd.setMessage("Sharing");
        pd.setOwnerActivity(this);
        pd.setCancelable(false);

        // Finds out whether to show emails or phone numbers
        showPhoneNumbers = getIntent().getBooleanExtra("showPhoneNumbers", true);
        if (showPhoneNumbers) {
            getContactPhoneList();
        } else {
            getContactEmailList();
        }

        // Sets share message to default message from RAF Parameters
        etShareMessage.setText(AmbassadorSingleton.getInstance().rafParameters.shareMessage);

        adapter = new ContactListAdapter(this, contactList, showPhoneNumbers);
        lvContacts.setAdapter(adapter);
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get checkmark image and animate in or out based on its selection state and updates arrays
                adapter.updateArrays(position, view);
                updateSendButton(adapter.selectedContacts.size());
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filterList(etSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //get and store pusher data
        try {
            pusherData = new JSONObject(AmbassadorSingleton.getInstance().getPusherInfo());
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
        Drawable drawable = menu.findItem(R.id.action_search).getIcon();

        // Sets search icon to darkgray
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.DKGRAY);
        menu.findItem(R.id.action_search).setIcon(drawable);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            displayOrHideSearch(null);
        } else {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    //endregion


    //region CONTACT FUNCTIONS
    void getContactPhoneList() {
        contactList = new ArrayList<>();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);

        if (phones.moveToFirst()) {
            do {
                ContactObject object = new ContactObject();
                object.name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                object.phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

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
            } while (phones.moveToNext());
        } else {
            tvNoContacts.setVisibility(View.VISIBLE);
        }

        phones.close();
        sortContactsAlphabetically();
    }

    void getContactEmailList() {
        contactList = new ArrayList<>();
        Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null, null, null, null);

        if (emails.moveToFirst()) {
            do  {
                ContactObject object = new ContactObject();
                object.name = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                object.emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contactList.add(object);
            }
            while (emails.moveToNext());
        } else {
            tvNoContacts.setVisibility(View.VISIBLE);
        }

        emails.close();
        sortContactsAlphabetically();
    }

    void sortContactsAlphabetically() {
        Collections.sort(contactList, new Comparator<ContactObject>() {
            @Override
            public int compare(ContactObject lhs, ContactObject rhs) {
                return lhs.name.compareTo(rhs.name);
            }
        });
    }
    //endregion


    //region BUTTON FUNCTIONS
    public void handleEditButtonTap(View view) {
        if (etShareMessage.isEnabled()) {
            doneEditingMessage();
        } else {
            editBtnTapped();
        }
    }

    void editBtnTapped() {
        btnEdit.setImageResource(R.drawable.done_button);
        btnSend.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
        etShareMessage.setEnabled(true);
        etShareMessage.requestFocus();
        inputManager.showSoftInput(etShareMessage, 0); // Presents keyboard
    }

    void doneEditingMessage() {
        btnSend.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        etShareMessage.setSelection(0);
        etShareMessage.setEnabled(false);
        btnEdit.setImageResource(R.drawable.pencil_edit);
    }

    public void displayOrHideSearch(View v) {
        // Float that helps converts dp to pixels based on device
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;

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
            shrinkSendView(true);
            etSearch.requestFocus();
            inputManager.showSoftInput(etSearch, 0);
        } else {
            // If HIDING search
            etSearch.setText("");
            shrinkSendView(false);
            etSearch.clearFocus();
            inputManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        }

        anim.setDuration(300);
        anim.start();
    }

    void shrinkSendView(Boolean shouldShrink) {
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

    // Adds and styles toolbar in place of the actionbar
    void setUpToolbar() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.action_bar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        if (getSupportActionBar() != null) { getSupportActionBar().setTitle("Refer your friends"); }
        toolbar.setTitleTextColor(Color.DKGRAY);
        toolbar.setBackgroundColor(Color.WHITE);
        if (toolbar.getNavigationIcon() != null) { toolbar.getNavigationIcon().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN); }
    }

    void updateSendButton(int numOfContacts) {
        if (numOfContacts > 0) {
            if (!btnSend.isEnabled()) { btnSend.setEnabled(true); }
            btnSend.setText("SEND TO " + numOfContacts + " CONTACTS");
        } else {
            btnSend.setText("NO CONTACTS SELECTED");
            btnSend.setEnabled(false);
        }
    }

    public void sendToContacts(View view) {
        //get and store pusher data
        try {
            //if user is doing sms and we don't have first or last name, we need to get it with a dialog
            if (showPhoneNumbers && //FOR TESTING INCLUDE THIS -->  true || //remove "true ||" for launch
                (!pusherData.has("firstName") || pusherData.getString("firstName").equals("null") || pusherData.getString("firstName").isEmpty()
                ||
                !pusherData.has("lastName") || pusherData.getString("lastName").equals("null") || pusherData.getString("lastName").isEmpty()))
            {
                //show dialog to get name
                cnd = new ContactNameDialog(this, pd);
                cnd.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        cnd.showKeyboard();
                    }
                });
                cnd.show();
                return;
            } else {
                _initiateSend();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            return;
        }
    }

    private void _initiateSend() {
        //this method is called from two places, one of which could already be showing the pd
        if (!pd.isShowing()) pd.show();

        // Call bulkShareHelper to handle sharing calls
        BulkShareHelper shareHelper = new BulkShareHelper(pd, etShareMessage.getText().toString());
        shareHelper.bulkShare(adapter.selectedContacts, showPhoneNumbers);
    }

    // Interface call from ContactNameDialog
    @Override
    public void namesHaveBeenUpdated() {
        _initiateSend();
    }
}