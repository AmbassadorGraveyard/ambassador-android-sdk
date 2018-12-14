package com.ambassador.ambassadorsdk.internal.activities.contacts;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
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
import com.ambassador.ambassadorsdk.BuildConfig;
import com.ambassador.ambassadorsdk.R;
import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.internal.adapters.ContactListAdapter;
import com.ambassador.ambassadorsdk.internal.api.RequestManager;
import com.ambassador.ambassadorsdk.internal.data.Campaign;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.dialogs.AskNameDialog;
import com.ambassador.ambassadorsdk.internal.dialogs.AskUrlDialog;
import com.ambassador.ambassadorsdk.internal.models.Contact;
import com.ambassador.ambassadorsdk.internal.utils.ContactList;
import com.ambassador.ambassadorsdk.internal.utils.Device;
import com.ambassador.ambassadorsdk.internal.utils.res.ColorResource;
import com.ambassador.ambassadorsdk.internal.utils.res.StringResource;
import com.ambassador.ambassadorsdk.internal.views.CrossfadedTextView;
import com.ambassador.ambassadorsdk.internal.views.DividedRecyclerView;
import com.ambassador.ambassadorsdk.internal.views.PermissionView;

import java.util.List;

import javax.inject.Inject;

import butterfork.Bind;
import butterfork.ButterFork;

/**
 * Activity that handles contact selection and sharing using email or SMS.
 */
public final class ContactSelectorActivity extends AppCompatActivity {
    private static final int CHECK_CONTACT_PERMISSIONS = 1;
    private static final int CHECK_SEND_SMS_PERMISSIONS = 2;
    private static final int MAX_SMS_LENGTH = 160;
    private static final int LENGTH_GOOD_COLOR = new RAFOptions().get().getContactsSendButtonTextColor();
    private static final int LENGTH_BAD_COLOR = new ColorResource(android.R.color.holo_red_dark).getColor();

    @Nullable
    @Bind(B.id.action_bar)      protected Toolbar               toolbar;

    @Bind(B.id.rlSearch)        protected RelativeLayout        rlSearch;
    @Bind(B.id.etSearch)        protected EditText              etSearch;
    @Bind(B.id.btnDoneSearch)   protected Button                btnDoneSearch;
    @Bind(B.id.rvContacts)      protected DividedRecyclerView   rvContacts;
    @Bind(B.id.vListDim)        protected View                  vListDim;
    @Bind(B.id.llSendView)      protected LinearLayout          llSendView;
    @Bind(B.id.etShareMessage)  protected EditText              etShareMessage;
    @Bind(B.id.btnEdit)         protected ImageButton           btnEdit;
    @Bind(B.id.btnDone)         protected Button                btnDone;
    @Bind(B.id.rlSend)          protected RelativeLayout        rlSend;
    @Bind(B.id.tvSendContacts)  protected CrossfadedTextView    tvSendContacts;
    @Bind(B.id.tvSendCount)     protected TextView              tvSendCount;
    @Bind(B.id.tvNoContacts)    protected TextView              tvNoContacts;
    @Bind(B.id.permissionView)  protected PermissionView        permissionView;

    @Inject protected BulkShareHelper   bulkShareHelper;
    @Inject protected RequestManager    requestManager;
    @Inject protected User              user;
    @Inject protected Campaign          campaign;
    @Inject protected Device            device;
    @Inject protected RAFOptions        RAFOptions;
    @Inject protected Utilities         Utilities;
    @Inject protected RAFOptions        raf;

    protected List<Contact>             contactList;
    protected ContactListAdapter        contactListAdapter;
    protected boolean                   showPhoneNumbers;
    protected AskNameDialog             askNameDialog;
    protected ProgressDialog            progressDialog;
    protected float                     lastSendHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        AmbSingleton.getInstance().getAmbComponent().inject(this);
        ButterFork.bind(this);

        finishIfSingletonInvalid();
        if (isFinishing()) return;

        processIntent();
        setTheme();
        setUpToolbar();
        setUpOnClicks();
        setUpProgressDialog();
        setUpUI();
        populateContacts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (askNameDialog != null && askNameDialog.isShowing()) {
            askNameDialog.dismiss();
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CHECK_CONTACT_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionView.setVisibility(View.GONE);
                    populateContacts();
                } else {
                    permissionView.setVisibility(View.VISIBLE);
                }
                break;
            case CHECK_SEND_SMS_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    handleSendSMS();
                } else {
                    progressDialog.dismiss();
                    finish();
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 555 && data != null) {
            // Grabs boolean value from intent to check for successful save
            boolean shouldSend = data.getBooleanExtra("success", false);
            if (shouldSend) send();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ambassador_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final Drawable searchIcon = ContextCompat.getDrawable(this, R.drawable.ic_search_white_24dp);
        searchIcon.setColorFilter(raf.getContactsSearchIconColor(), PorterDuff.Mode.SRC_ATOP);
        searchItem.setIcon(searchIcon);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            toggleSearch();
            return true;
        } else {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void finishIfSingletonInvalid() {
        if (!AmbSingleton.getInstance().isValid()) {
            finish();
        }
    }

    private void processIntent() {
        Intent data = getIntent();
        showPhoneNumbers = data.getBooleanExtra("showPhoneNumbers", true);
    }

    private void setTheme() {
        rlSearch.setBackgroundColor(raf.getContactsSearchBarColor());
        llSendView.setBackgroundColor(raf.getContactsSendBackground());
        btnDone.setTextColor(raf.getContactsDoneButtonTextColor());
        rlSend.setBackgroundColor(raf.getContactsSendButtonColor());
        tvSendContacts.setTextColor(raf.getContactsSendButtonTextColor());
        tvSendCount.setTextColor(raf.getContactsSendButtonTextColor());
        rvContacts.setBackgroundColor(raf.getContactsListViewBackgroundColor());
        etShareMessage.setTypeface(raf.getContactSendMessageTextFont());
    }

    private void setUpToolbar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(raf.getToolbarTitle());
        }

        Drawable arrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white_24dp);
        arrow.setColorFilter(raf.getContactsToolbarArrowColor(), PorterDuff.Mode.SRC_ATOP);

        if (toolbar == null) return;

        toolbar.setNavigationIcon(arrow);
        toolbar.setBackgroundColor(raf.getContactsToolbarColor());
        toolbar.setTitleTextColor(raf.getContactsToolbarTextColor());

        Utilities.setStatusBar(getWindow(), raf.getContactsToolbarColor());
    }

    private void setUpOnClicks() {
        rlSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rlSendClicked();
            }
        });
        btnDoneSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDoneSearchClicked();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEditClicked();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnDoneClicked();
            }
        });
        permissionView.setOnButtonClickListener(new PermissionView.OnButtonClickListener() {
            @Override
            public void onClick() {
                handlePermission(Manifest.permission.READ_CONTACTS, CHECK_CONTACT_PERMISSIONS);
            }
        });
    }

    private void setUpProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(new StringResource(R.string.sharing).getValue());
        progressDialog.setOwnerActivity(this);
        progressDialog.setCancelable(false);
    }

    private void setUpUI() {
        tvSendContacts.setTextNoAnimation("NO CONTACTS SELECTED");
        etShareMessage.setText(raf.getDefaultShareMessage());
        btnEdit.setColorFilter(getResources().getColor(R.color.ultraLightGray));

        if (showPhoneNumbers) {
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
            tvSendContacts.setGravity(CrossfadedTextView.Gravity.LEFT);
        } else {
            tvSendCount.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvSendContacts.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            if (android.os.Build.VERSION.SDK_INT >= 17) params.addRule(RelativeLayout.ALIGN_PARENT_START, 0);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            tvSendContacts.setLayoutParams(params);
            tvSendContacts.setGravity(CrossfadedTextView.Gravity.CENTER);
        }

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
    }

    private void rlSendClicked() {
        if (ableToSend()) {
            send();
        }
    }

    private void btnDoneSearchClicked() {
        toggleSearch();
    }

    private void btnEditClicked() {
        rlSend.setEnabled(false);
        btnEdit.setVisibility(View.GONE);
        btnDone.setVisibility(View.VISIBLE);
        etShareMessage.setEnabled(true);
        etShareMessage.requestFocus();
        etShareMessage.setSelection(0);
        device.openSoftKeyboard(etShareMessage);
        vListDim.setVisibility(View.VISIBLE);
        vListDim.animate()
                .alpha(0.75f)
                .setDuration(150)
                .setListener(null)
                .start();
    }

    private void btnDoneClicked() {
        rlSend.setEnabled(true);
        btnEdit.setVisibility(View.VISIBLE);
        btnDone.setVisibility(View.GONE);
        etShareMessage.setEnabled(false);
        vListDim.animate()
                .alpha(0f)
                .setDuration(150)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        vListDim.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        vListDim.setVisibility(View.GONE);
                    }
                }).start();
    }

    private void toggleSearch() {
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

        if (finalHeight != 0) { // show search
            if (etShareMessage.isEnabled()) {
                btnDoneClicked();
            }
            shrinkSendView(true);
            etSearch.requestFocus();
            device.openSoftKeyboard(etSearch);
        } else { // hide search
            etSearch.setText("");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    shrinkSendView(false);
                }
            }, 250);
            etSearch.clearFocus();
            device.closeSoftKeyboard(etSearch);

        }

        anim.setDuration(300);
        anim.start();
    }

    private void updateCharCounter(int length) {
        String text = "(" + length + "/" + MAX_SMS_LENGTH + ")";
        tvSendCount.setText(text);
        if (length > MAX_SMS_LENGTH) {
            tvSendCount.setShadowLayer(2, -1, 0, Color.WHITE);
            tvSendCount.setTextColor(LENGTH_BAD_COLOR);
        } else {
            tvSendCount.setShadowLayer(2, -1, 0, Color.TRANSPARENT);
            tvSendCount.setTextColor(LENGTH_GOOD_COLOR);
        }
    }

    private void negativeTextViewFeedback(View textView) {
        Animation shake = new TranslateAnimation(-3, 3, 0, 0);
        shake.setInterpolator(new CycleInterpolator(3));
        shake.setDuration(500);

        textView.clearAnimation();
        textView.startAnimation(shake);
    }

    private void shrinkSendView(Boolean shouldShrink) {
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

    public void updateSendButton(int numOfContacts) {
        if (numOfContacts == 0) {
            String noneSelected = "NO CONTACTS SELECTED";
            tvSendContacts.setText(noneSelected);
            return;
        }

        if (!etShareMessage.isEnabled() && !rlSend.isEnabled()) rlSend.setEnabled(true);
        String btnSendText = "SEND TO " + numOfContacts;
        btnSendText += (numOfContacts > 1) ? " CONTACTS" : " CONTACT";
        tvSendContacts.setText(btnSendText);
    }

    private void populateContacts() {
        if (!handlePermission(Manifest.permission.READ_CONTACTS, CHECK_CONTACT_PERMISSIONS)) {
            return;
        }

        if (showPhoneNumbers) contactList = new ContactList(ContactList.Type.PHONE).get(this);
        else contactList = new ContactList(ContactList.Type.EMAIL).get(this);
        contactList = (contactList.isEmpty() && !BuildConfig.IS_RELEASE_BUILD) ? new ContactList(ContactList.Type.DUMMY).get(this) : contactList;

        if (contactList.size() == 0) {
            tvNoContacts.setVisibility(View.VISIBLE);
        }

        contactListAdapter = new ContactListAdapter(this, contactList, showPhoneNumbers);
        contactListAdapter.setOnSelectedContactsChangedListener(new ContactListAdapter.OnSelectedContactsChangedListener() {
            @Override
            public void onSelectedContactsChanged(int selected) {
                updateSendButton(selected);
            }
        });

        rvContacts.setAdapter(contactListAdapter);
    }

    private void handleSendSMS() {
        if (!handlePermission(Manifest.permission.SEND_SMS, CHECK_SEND_SMS_PERMISSIONS)) {
            return;
        }

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        PendingIntent sentPI = PendingIntent.getBroadcast(ContactSelectorActivity.this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(ContactSelectorActivity.this, 0, new Intent(DELIVERED), 0);

        //when the SMS has been sent
        registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                progressDialog.dismiss();
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        finish();
                        Toast.makeText(ContactSelectorActivity.this, new StringResource(R.string.post_success).getValue(), Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(ContactSelectorActivity.this, "There was a problem sending the SMS, please try again later. (RESULT_ERROR_GENERIC_FAILURE)", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(ContactSelectorActivity.this, "There was a problem sending the SMS, please try again later. (RESULT_ERROR_NO_SERVICE)", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(ContactSelectorActivity.this, "There was a problem sending the SMS, please try again later. (RESULT_ERROR_NULL_PDU)", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "There was a problem sending the SMS, please try again later. (RESULT_ERROR_RADIO_OFF)", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(getBaseContext(), "There was a problem sending the SMS, please try again later. (UNKNOWN)", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //NOTE: keeping this code in comments, because we may want to use delivered instead of sent in the future
        //when the SMS has been delivered
        /*registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(ContactSelectorActivity.this, "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(ContactSelectorActivity.this, "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));*/

        SmsManager sms = SmsManager.getDefault();
        Contact contact = contactListAdapter.getSelectedContacts().get(0);
        sms.sendTextMessage(contact.getPhoneNumber(), null, etShareMessage.getText().toString(), sentPI, deliveredPI);
    }

    private boolean handlePermission(String permission, int code) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, code);
        }

        return false;
    }

    private boolean ableToSend() {
        boolean lengthTooShort = showPhoneNumbers && etShareMessage.getText().length() > MAX_SMS_LENGTH;
        boolean noneSelected = contactListAdapter.getSelectedContacts().size() <= 0;
        boolean emptyMessage = etShareMessage.getText().toString().length() <= 0;
        boolean haveUrl = Utilities.containsURL(etShareMessage.getText().toString(), campaign.getUrl());
        boolean haveName = user.getAmbassadorIdentification().getFirstName() != null && !user.getAmbassadorIdentification().getFirstName().isEmpty() && user.getAmbassadorIdentification().getLastName() != null && !user.getAmbassadorIdentification().getLastName().isEmpty();

        if (lengthTooShort && noneSelected) {
            negativeTextViewFeedback(tvSendCount);
            negativeTextViewFeedback(tvSendContacts);
            return false;
        } else if (lengthTooShort) {
            negativeTextViewFeedback(tvSendCount);
            return false;
        } else if (noneSelected) {
            negativeTextViewFeedback(tvSendContacts);
            return false;
        } else if (emptyMessage) {
            Toast.makeText(getApplicationContext(), new StringResource(R.string.share_message_empty).getValue(), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!haveUrl) {
            askForUrl();
            return false;
        } else if (showPhoneNumbers && !haveName) {
            askForName();
            return false;
        }

        return true;
    }

    private void askForUrl() {
        final String url = campaign.getUrl();
        new AskUrlDialog(this, url)
                .setOnCompleteListener(new AskUrlDialog.OnCompleteListener() {
                    @Override
                    public void dontAdd() {
                        send();
                    }

                    @Override
                    public void doAdd() {
                        insertURLIntoMessage(etShareMessage, url);
                    }
                })
                .show();
    }

    private void insertURLIntoMessage(EditText editText, String url) {
        String appendingLink = url;

        if (editText.getText().toString().contains("http://")) {
            String sub = editText.getText().toString().substring(editText.getText().toString().indexOf("http://"));
            String replacementSubstring;
            replacementSubstring = (sub.contains(" ")) ? sub.substring(0, sub.indexOf(' ')) : sub;
            editText.setText(editText.getText().toString().replace(replacementSubstring, appendingLink));
            return;
        }

        if (editText.getText().toString().length() != 0 && editText.getText().toString().charAt(editText.getText().toString().length() - 1) != ' ') {
            appendingLink = " " + url;
            editText.setText(editText.getText().append(appendingLink));
        } else {
            appendingLink = url;
            editText.setText(editText.getText().append(appendingLink));
        }
    }

    private void askForName() {
        Intent nameIntent = new Intent(this, AskNameActivity.class);
        startActivityForResult(nameIntent, 555);
    }

    private void send() {
        if (!progressDialog.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                }
            });
        }

        //if only 1 contact and device is equipped to send, use native SMS for a better experience
        if (showPhoneNumbers && contactListAdapter.getSelectedContacts().size() == 1 &&AmbSingleton.getInstance().getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            handleSendSMS();
            return;
        }

        bulkShareHelper.bulkShare(etShareMessage.getText().toString(), contactListAdapter.getSelectedContacts(), showPhoneNumbers, new BulkShareHelper.BulkShareCompletion() {
            @Override
            public void bulkShareSuccess() {
                progressDialog.dismiss();
                finish();
                Toast.makeText(getApplicationContext(), new StringResource(R.string.post_success).getValue(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void bulkShareFailure() {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), new StringResource(R.string.post_failure).getValue(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
