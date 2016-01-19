package com.ambassador.ambassadorsdk.internal.activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.ContactListAdapter;
import com.ambassador.ambassadorsdk.internal.ContactNameDialog;
import com.ambassador.ambassadorsdk.internal.ContactObject;
import com.ambassador.ambassadorsdk.internal.DividerItemDecoration;
import com.ambassador.ambassadorsdk.internal.PusherSDK;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.utils.Device;
import com.ambassador.ambassadorsdk.utils.StringResource;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import butterfork.Bind;
import butterfork.ButterFork;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 *
 */
public final class ContactSelectorActivity extends AppCompatActivity implements PusherSDK.IdentifyListener {

    // region Constants
    private static final int CHECK_CONTACT_PERMISSIONS = 1;
    private static final int MAX_SMS_LENGTH = 160;
    // endregion
    
    // region Views
    @Bind(B.id.action_bar)      protected Toolbar           toolbar;
    @Bind(B.id.rlSearch)        protected RelativeLayout    rlSearch;
    @Bind(B.id.etSearch)        protected EditText          etSearch;
    @Bind(B.id.btnDoneSearch)   protected Button            btnDoneSearch;
    @Bind(B.id.rvContacts)      protected RecyclerView      rvContacts;
    @Bind(B.id.llSendView)      protected LinearLayout      llSendView;
    @Bind(B.id.etShareMessage)  protected EditText          etShareMessage;
    @Bind(B.id.btnEdit)         protected ImageButton       btnEdit;
    @Bind(B.id.btnDone)         protected Button            btnDone;
    @Bind(B.id.rlSend)          protected RelativeLayout    rlSend;
    @Bind(B.id.tvSendContacts)  protected TextView          tvSendContacts;
    @Bind(B.id.tvSendCount)     protected TextView          tvSendCount;
    @Bind(B.id.tvNoContacts)    protected TextView          tvNoContacts;
    // endregion

    // region Dependencies
    @Inject protected PusherSDK         pusherSDK;
    @Inject protected BulkShareHelper   bulkShareHelper;
    @Inject protected AmbassadorConfig  ambassadorConfig;
    @Inject protected Device            device;
    // endregion

    // region Local members
    protected List<ContactObject>   contactList;
    protected ContactListAdapter    contactListAdapter;
    protected JSONObject            pusherData;
    protected boolean               showPhoneNumbers;
    // endregion
    
    private static int lengthBadColor;
    private static int lengthGoodColor;


    private static HashMap<Integer, String> phoneTypeMap;
    static {
        phoneTypeMap = new HashMap<>();
        phoneTypeMap.put(ContactsContract.CommonDataKinds.Phone.TYPE_HOME, "Home");
        phoneTypeMap.put(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE, "Mobile");
        phoneTypeMap.put(ContactsContract.CommonDataKinds.Phone.TYPE_WORK, "Work");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // Injection
        AmbassadorSingleton.getInstanceComponent().inject(this);
        ButterFork.bind(this);

        // Requirement checks
        finishIfContextInvalid();
        if (isFinishing()) return;

        // Layout
        setUpToolbar();


        pusherSDK.setIdentifyListener(this);

        _setUpToolbar(ambassadorConfig.getRafParameters().toolbarTitle);

        showPhoneNumbers = getIntent().getBooleanExtra("showPhoneNumbers", true);

        //setup progress dialog only once
        pd = new ProgressDialog(this);
        pd.setMessage(new StringResource(R.string.sharing).getValue());
        pd.setOwnerActivity(this);
        pd.setCancelable(false);

        pusherSDK.setIdentifyListener(this);

        if (_handleContactsPermission()) {
            _handleContactsPopulation();
        }

        // Sets share message to default message from RAF Parameters
        etShareMessage.setText(ambassadorConfig.getRafParameters().defaultShareMessage);

        if (showPhoneNumbers) {
            lengthGoodColor = getResources().getColor(R.color.contactsSendButtonText);
            lengthBadColor = getResources().getColor(android.R.color.holo_red_light);
            updateCharCounter(etShareMessage.getText().toString().length());
            etShareMessage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String text = etShareMessage.getText().toString();
                    updateCharCounter(text.length());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } else {
            tvSendCount.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvSendContacts.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            tvSendContacts.setLayoutParams(params);
        }

        rlSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean goodToGo = true;
                if (showPhoneNumbers && !(etShareMessage.getText().length() <= MAX_SMS_LENGTH)) {
                    negativeTextViewFeedback(tvSendCount);
                    goodToGo = false;
                }
                if (contactListAdapter.getSelectedContacts().size() <= 0) {
                    negativeTextViewFeedback(tvSendContacts);
                    goodToGo = false;
                }

                if (goodToGo) {
                    _sendToContacts();
                }
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
                contactListAdapter.filterList(etSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //get and store pusher data
        try {
            pusherData = new JSONObject(ambassadorConfig.getPusherInfo());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pusherSDK.setIdentifyListener(null);
    }

    private void updateCharCounter(int length) {
        tvSendCount.setText("(" + length + "/" + MAX_SMS_LENGTH + ")");
        if (length > MAX_SMS_LENGTH) {
            tvSendCount.setShadowLayer(2, -1, 0, Color.WHITE);
            tvSendCount.setTextColor(lengthBadColor);
        } else {
            tvSendCount.setShadowLayer(2, -1, 0, Color.TRANSPARENT);
            tvSendCount.setTextColor(lengthGoodColor);
        }
    }

    private void negativeTextViewFeedback(TextView textView) {
        Animation shake = new TranslateAnimation(-3, 3, 0, 0);
        shake.setInterpolator(new CycleInterpolator(3));
        shake.setDuration(500);

        textView.clearAnimation();
        textView.startAnimation(shake);
    }

    @Override
    protected void onPause() {
        if (cnd != null) {
            cnd.dismiss();
        }
        super.onPause();
    }

    /** Toolbar menu methods **/

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

    // region Requirement checks
    private void finishIfContextInvalid() {
        if (!AmbassadorSingleton.isValid()) {
            finish();
        }
    }
    // endregion


    /** Contact methods **/

    private void _getContactPhoneList() {
        contactList = new ArrayList<>();
        Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        if (phoneCursor == null) {
            return;
        }

        if (phoneCursor.moveToFirst()) {
            do {
                String name = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String thumbUri = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                String picUri = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String typeNum = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                String type = "Other";
                if (phoneTypeMap.containsKey(Integer.parseInt(typeNum))) {
                    type = phoneTypeMap.get(Integer.parseInt(typeNum));
                }

                ContactObject object = new ContactObject.Builder()
                        .setName(name)
                        .setPhoneNumber(phoneNumber)
                        .setType(type)
                        .setThumbnailUri(thumbUri)
                        .setPictureUri(picUri)
                        .build();

                contactList.add(object);
            } while (phoneCursor.moveToNext());
        }

        phoneCursor.close();

        if (!AmbassadorConfig.isReleaseBuild && contactList.size() <= 0) {
            _addDummyPhoneData(contactList);
        }

        if (contactList.size() <= 0) {
            tvNoContacts.setVisibility(View.VISIBLE);
        }

        Collections.sort(contactList);
    }

    private void _addDummyPhoneData(List<ContactObject> contactList) {
        String[] firstNames = new String[]{"Dylan", "Jake", "Corey", "Mitch", "Matt", "Brian", "Amanada", "Brandon"};
        String[] lastNames = new String[]{"Smith", "Johnson", "Stevens"};
        String[] types = new String[]{"Home", "Mobile", "Work"};
        String[] numbers = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"};

        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            String name = firstNames[rand.nextInt(firstNames.length)] + " " + lastNames[rand.nextInt(lastNames.length)];
            String type = types[rand.nextInt(types.length)];
            String phoneNumber = "";
            for (int j = 0; j < 12; j++) {
                if (j == 3 || j == 7) {
                    phoneNumber += "-";
                } else {
                    phoneNumber += numbers[rand.nextInt(numbers.length)];
                }
            }

            ContactObject contactObject = new ContactObject.Builder()
                    .setName(name)
                    .setPhoneNumber(phoneNumber)
                    .setType(type)
                    .build();

            contactList.add(contactObject);
        }
    }

    private void _getContactEmailList() {
        contactList = new ArrayList<>();
        Cursor emailCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, null, null, null);

        if (emailCursor == null) {
            return;
        }

        if (emailCursor.moveToFirst()) {
            do  {
                String name = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String thumbUri = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                String picUri = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                String emailAddress = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                ContactObject object = new ContactObject.Builder()
                        .setName(name)
                        .setEmailAddress(emailAddress)
                        .setThumbnailUri(thumbUri)
                        .setPictureUri(picUri)
                        .build();

                contactList.add(object);
            }
            while (emailCursor.moveToNext());
        }

        emailCursor.close();

        if (!AmbassadorConfig.isReleaseBuild && contactList.size() <= 0) {
            _addDummyEmailData(contactList);
        }

        if (contactList.size() <= 0) {
            tvNoContacts.setVisibility(View.VISIBLE);
        }

        Collections.sort(contactList);
    }

    private void _addDummyEmailData(List<ContactObject> contactList) {
        String[] firstNames = new String[]{"Dylan", "Jake", "Corey", "Mitch", "Matt", "Brian", "Amanada", "Brandon"};
        String[] lastNames = new String[]{"Smith", "Johnson", "Stevens"};

        Random rand = new Random();
        for (int i = 0; i < 100; i++) {
            String name = firstNames[rand.nextInt(firstNames.length)] + " " + lastNames[rand.nextInt(lastNames.length)];
            String email = name.substring(0, name.indexOf(" ")).toLowerCase() + "@getambassador.com";

            ContactObject contactObject = new ContactObject.Builder()
                    .setName(name)
                    .setEmailAddress(email)
                    .build();

            contactList.add(contactObject);
        }
    }

    /** Button methods **/

    private void _editBtnTapped() {
        rlSend.setEnabled(false);
        btnEdit.setVisibility(View.GONE);
        btnDone.setVisibility(View.VISIBLE);
        etShareMessage.setEnabled(true);
        etShareMessage.requestFocus();
        etShareMessage.setSelection(0);
        inputManager.showSoftInput(etShareMessage, 0);
    }

    private void _doneEditingMessage() {
        rlSend.setEnabled(true);
        btnEdit.setVisibility(View.VISIBLE);
        btnDone.setVisibility(View.GONE);
        etShareMessage.setEnabled(false);
    }

    private void _displayOrHideSearch() {
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
            /** Showing search **/
            _shrinkSendView(true);
            etSearch.requestFocus();
            device.openSoftKeyboard(etSearch);
        } else {
            /** Hiding search **/
            etSearch.setText("");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    _shrinkSendView(false);
                }
            }, 250);
            etSearch.clearFocus();
            device.closeSoftKeyboard(etSearch);

        }

        anim.setDuration(300);
        anim.start();
    }

    float lastSendHeight;

    /** Shrinks or inflates the send button while the user is searching, to make more room **/
    private void _shrinkSendView(Boolean shouldShrink) {
        if (shouldShrink) {
            lastSendHeight = llSendView.getHeight();
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)llSendView.getLayoutParams();
            params.height = 0;
            llSendView.setLayoutParams(params);
        } else {
            ValueAnimator anim = ValueAnimator.ofInt(0, (int) lastSendHeight);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int val = (Integer) animation.getAnimatedValue();
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) llSendView.getLayoutParams();
                    layoutParams.height = val;

                    if (val >= lastSendHeight) {
                        layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    }

                    llSendView.setLayoutParams(layoutParams);
                }
            });

            anim.setDuration(250);
            anim.start();
        }
    }

    // Adds and styles toolbar in place of the actionbar
    private void setUpToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(toolbarTitle);
        }

        Drawable arrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        arrow.setColorFilter(getResources().getColor(R.color.contactsToolBarArrow), PorterDuff.Mode.SRC_ATOP);

        if (toolbar == null) return;

        toolbar.setNavigationIcon(arrow);
        toolbar.setBackgroundColor(getResources().getColor(R.color.contactsToolBar));
        toolbar.setTitleTextColor(getResources().getColor(R.color.contactsToolBarText));

        Utilities.setStatusBar(getWindow(), getResources().getColor(R.color.contactsToolBar));
    }

    public void _updateSendButton(int numOfContacts) {
        if (numOfContacts == 0) {
            tvSendContacts.setText("NO CONTACTS SELECTED");
            return;
        }

        if (!etShareMessage.isEnabled() && !rlSend.isEnabled()) rlSend.setEnabled(true);
        String btnSendText = "SEND TO " + numOfContacts;
        btnSendText += (numOfContacts > 1) ? " CONTACTS" : " CONTACT";
        tvSendContacts.setText(btnSendText);
    }

    private void _sendToContacts() {
        if (etShareMessage.getText().toString().length() < 1) {
            Toast.makeText(getApplicationContext(), new StringResource(R.string.share_message_empty).getValue(), Toast.LENGTH_SHORT).show();
            return;
        }

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

            return;
        }

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

    private void _initiateSend() {
        //this method is called from two places, one of which could already be showing the progress dialog
        if (!pd.isShowing()) pd.show();

        bulkShareHelper.bulkShare(etShareMessage.getText().toString(), contactListAdapter.getSelectedContacts(), showPhoneNumbers, new BulkShareHelper.BulkShareCompletion() {
            @Override
            public void bulkShareSuccess() {
                pd.dismiss();
                finish();
                Toast.makeText(getApplicationContext(), new StringResource(R.string.post_success).getValue(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void bulkShareFailure() {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), new StringResource(R.string.post_failure).getValue(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void identified(long requestId) {
        _initiateSend();
    }

    private void _handleContactsPopulation() {
        showPhoneNumbers = getIntent().getBooleanExtra("showPhoneNumbers", true);
        if (showPhoneNumbers) {
            _getContactPhoneList();
        } else {
            _getContactEmailList();
        }

        contactListAdapter = new ContactListAdapter(this, contactList, showPhoneNumbers);
        contactListAdapter.setOnSelectedContactsChangedListener(new ContactListAdapter.OnSelectedContactsChangedListener() {
            @Override
            public void onSelectedContactsChanged(int selected) {
                _updateSendButton(selected);
            }
        });

        rvContacts.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvContacts.setLayoutManager(llm);
        rvContacts.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        rvContacts.setAdapter(contactListAdapter);
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
                    Utilities.presentNonCancelableMessageDialog(this, new StringResource(R.string.sorry).getValue(), new StringResource(R.string.contacts_permission_denied).getValue(), new DialogInterface.OnClickListener() {
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
