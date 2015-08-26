package com.example.ambassador.ambassadorsdk;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.support.v7.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    public Boolean showPhoneNumbers;
    private InputMethodManager inputManager;
    private JSONObject pusherData;
    ContactListAdapter adapter;
    ProgressDialog pd;
    private String firstName, lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // Init UI components
        ListView lvContacts = (ListView)findViewById(R.id.lvContacts);
        Button btnDoneSearch = (Button) findViewById(R.id.btnDoneSearch);
        btnEdit = (ImageButton)findViewById(R.id.btnEdit);
        btnSend = (Button)findViewById(R.id.btnSend);
        etShareMessage = (EditText) findViewById(R.id.etShareMessage);
        rlSearch = (RelativeLayout)findViewById(R.id.rlSearch);
        etSearch = (EditText) findViewById(R.id.etSearch);
        llSendView = (LinearLayout) findViewById(R.id.llSendView);
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
                // Get checkmark image and animates in or out based on its selection state
                ImageView imageView = (ImageView) view.findViewById(R.id.ivCheckMark);
                if (adapter.selectedContacts.contains(adapter.filteredContactList.get(position))) {
                    adapter.selectedContacts.remove(adapter.filteredContactList.get(position));
                    imageView.animate().setDuration(100).x(view.getWidth()).start();
                } else {
                    adapter.selectedContacts.add(adapter.filteredContactList.get(position));
                    imageView.animate().setDuration(300).setInterpolator(new BounceInterpolator())
                            .x(view.getWidth() - imageView.getWidth() - 25).start();
                }

                updateSendButton(adapter.selectedContacts.size());
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filterList((String) etSearch.getText().toString());
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

        while (phones.moveToNext()) {
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
        }

        // TEMPORARY -- Adds fake contacts if phone has no contacts currently in it
        if (contactList.size() == 0) {
            ContactObject object1 = new ContactObject("John Doe", "Mobile", "555-555-5555");
            ContactObject object2 = new ContactObject("Jane Doe", "Mobile", "123-456-7890");
            ContactObject object3 = new ContactObject("Jim Doe", "Mobile", "098-765-4321");
            contactList.add(object1);
            contactList.add(object2);
            contactList.add(object3);
        }

        sortContactsAlphabetically();
    }

    void getContactEmailList() {
        contactList = new ArrayList<>();
        Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null, null, null, null);

        while (emails.moveToNext()) {
            ContactObject object = new ContactObject();
            object.name = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            object.emailAddress = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactList.add(object);
        }

        // TEMPORARY -- Adds fake contacts if phone has no contacts currently in it
        if (contactList.size() == 0) {
            ContactObject object1 = new ContactObject("John Doe", "johndoe@gmail.com");
            ContactObject object2 = new ContactObject("Jane Doe", "janedoe@gmail.com");
            ContactObject object3 = new ContactObject("Jim Doe", "jimdoe@gmail.com");
            contactList.add(object1);
            contactList.add(object2);
            contactList.add(object3);
        }

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
        btnEdit.setImageResource(R.mipmap.done_button);
        btnSend.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
        etShareMessage.setEnabled(true);
        etShareMessage.requestFocus();
        inputManager.showSoftInput(etShareMessage, 0); // Presents keyboard
    }

    void doneEditingMessage() {
        btnSend.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        etShareMessage.setSelection(0);
        etShareMessage.setEnabled(false);
        btnEdit.setImageResource(R.mipmap.pencil_edit);
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

    // Hides the send view while user is searching.  Mainly to make more room to see listview
    void shrinkSendView(Boolean shouldShrink) {
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
        if (toolbar.getNavigationIcon() != null) {
            toolbar.getNavigationIcon().setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_IN);
        }
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
            if (showPhoneNumbers && true || //remove "true ||"
                !pusherData.has("firstName") || pusherData.getString("firstName") == null
                ||
                !pusherData.has("lastName") || pusherData.getString("lastName") == null)
            {
                //show dialog to get name
                final ContactNameDialog cnd = new ContactNameDialog(this);
                cnd.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        cnd.showKeyboard();
                    }
                });
                cnd.show();
                return;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        _initiateSend();
    }

    @Override
    public void handleNameInput(String firstName, String lastName) {
        pd.show();
        try {
            pusherData.put("firstName", firstName);
            pusherData.put("lastName", lastName);
        }
        catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        //save to shared prefs
        AmbassadorSingleton.getInstance().savePusherInfo(pusherData.toString());

        //call api - on success we'll initiate the bulk share
        UpdateNameRequest unr = new UpdateNameRequest();
        unr.execute();
    }

    private void _initiateSend() {
        //this method is called from two places, one of which could already be showing the pd
        if (!pd.isShowing()) pd.show();

        BulkShareHelper shareHelper = new BulkShareHelper(pd);
        shareHelper.bulkSMSShare(adapter.selectedContacts, showPhoneNumbers);
    }

    private void handleNoContacts() {
        // TODO: Add functionality to show "No contacts" on top of listview if user has no contacts
    }

    class UpdateNameRequest extends AsyncTask<Void, Void, Void> {
        int statusCode;

        @Override
        protected Void doInBackground(Void... params) {
            String url = "http://dev-ambassador-api.herokuapp.com/universal/action/identify/";
            JSONObject DataObject = new JSONObject();
            JSONObject NameObject = new JSONObject();

            try {
                DataObject.put("email", pusherData.getString("email"));
                NameObject.put("first_name", firstName);
                NameObject.put("last_name", lastName);
                DataObject.put("update_data", NameObject);

                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", AmbassadorSingleton.API_KEY);
                connection.setRequestProperty("MBSY_UNIVERSAL_ID", AmbassadorSingleton.MBSY_UNIVERSAL_ID);

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(DataObject.toString());
                wr.flush();
                wr.close();

                statusCode = connection.getResponseCode();

                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            _initiateSend();
        }
    }
}