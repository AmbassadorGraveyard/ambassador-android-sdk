package com.ambassador.ambassadorsdk.internal.activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.ambassador.ambassadorsdk.RAFOptions;
import com.ambassador.ambassadorsdk.internal.AmbassadorConfig;
import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;
import com.ambassador.ambassadorsdk.internal.BulkShareHelper;
import com.ambassador.ambassadorsdk.internal.ContactListAdapter;
import com.ambassador.ambassadorsdk.internal.ContactNameDialog;
import com.ambassador.ambassadorsdk.internal.models.Contact;
import com.ambassador.ambassadorsdk.internal.DividerItemDecoration;
import com.ambassador.ambassadorsdk.internal.PusherSDK;
import com.ambassador.ambassadorsdk.internal.Utilities;
import com.ambassador.ambassadorsdk.utils.ColorResource;
import com.ambassador.ambassadorsdk.utils.ContactList;
import com.ambassador.ambassadorsdk.utils.Device;
import com.ambassador.ambassadorsdk.utils.StringResource;

import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

import butterfork.Bind;
import butterfork.ButterFork;

/**
 * Activity that handles contact selection and sharing using email or SMS.
 */
public final class ContactSelectorActivity extends AppCompatActivity implements PusherSDK.IdentifyListener {

    // region Fields

    // region Constants
    private static final int CHECK_CONTACT_PERMISSIONS = 1;
    private static final int MAX_SMS_LENGTH = 160;
    private static final int LENGTH_GOOD_COLOR = RAFOptions.get().getContactsSendButtonTextColor(); // TODO: make this not suck
    private static final int LENGTH_BAD_COLOR = new ColorResource(android.R.color.holo_red_dark).getColor();
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
    protected  RAFOptions               raf = RAFOptions.get();
    protected List<Contact>       contactList;
    protected ContactListAdapter        contactListAdapter;
    protected JSONObject                pusherData;
    protected boolean                   showPhoneNumbers;
    protected ContactNameDialog         contactNameDialog;
    protected ProgressDialog            progressDialog;
    protected float                     lastSendHeight;
    // endregion

    // endregion

    // region Methods

    // region Activity overrides
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

        // Other setup
        processIntent();
        setTheme();
        setUpToolbar();
        setUpOnClicks();
        setUpProgressDialog();
        setUpUI();
        setUpPusher();

        populateContacts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pusherSDK.setIdentifyListener(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CHECK_CONTACT_PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    populateContacts();
                } else {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ambassador_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final Drawable searchIcon = ContextCompat.getDrawable(this, R.drawable.abc_ic_search_api_mtrl_alpha);
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
    // endregion

    // region Requirement checks
    private void finishIfContextInvalid() {
        if (!AmbassadorSingleton.isValid()) {
            finish();
        }
    }
    // endregion

    // region Setup
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
            actionBar.setTitle(ambassadorConfig.getRafParameters().toolbarTitle);
        }

        Drawable arrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
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
    }

    private void setUpProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(new StringResource(R.string.sharing).getValue());
        progressDialog.setOwnerActivity(this);
        progressDialog.setCancelable(false);
    }

    private void setUpUI() {
        etShareMessage.setText(ambassadorConfig.getRafParameters().defaultShareMessage);
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
        } else {
            tvSendCount.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvSendContacts.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            tvSendContacts.setLayoutParams(params);
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

    private void setUpPusher() {
        pusherData = ambassadorConfig.getPusherInfoObject();
        pusherSDK.setIdentifyListener(this);
    }
    // endregion

    // region OnClickListeners
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
    }

    private void btnDoneClicked() {
        rlSend.setEnabled(true);
        btnEdit.setVisibility(View.VISIBLE);
        btnDone.setVisibility(View.GONE);
        etShareMessage.setEnabled(false);
    }
    // endregion

    // region UI helpers
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

    private void negativeTextViewFeedback(TextView textView) {
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
    // endregion

    // region Contacts & sending
    private void populateContacts() {
        if (!handleContactsPermission()) {
            return;
        }

        if (showPhoneNumbers) contactList = new ContactList(ContactList.Type.PHONE).get(this);
        else contactList = new ContactList(ContactList.Type.EMAIL).get(this);
        contactList = (contactList.size() == 0 && AmbassadorConfig.isReleaseBuild)
                ? new ContactList(ContactList.Type.DUMMY).get(this) : contactList;

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

        rvContacts.setHasFixedSize(true);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        rvContacts.setAdapter(contactListAdapter);
    }

    private boolean handleContactsPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, CHECK_CONTACT_PERMISSIONS);
        }
        return false;
    }

    private boolean ableToSend() {
        boolean lengthToShort = showPhoneNumbers && !(etShareMessage.getText().length() <= MAX_SMS_LENGTH);
        boolean noneSelected = contactListAdapter.getSelectedContacts().size() <= 0;
        boolean emptyMessage = etShareMessage.getText().toString().length() <= 0;
        boolean noUrl = Utilities.containsURL(etShareMessage.getText().toString(), ambassadorConfig.getURL());
        boolean haveName = showPhoneNumbers && (!pusherHasKey("firstName") || !pusherHasKey("lastName"));

        if (lengthToShort && noneSelected) {
            negativeTextViewFeedback(tvSendCount);
            negativeTextViewFeedback(tvSendContacts);
            return false;
        } else if (lengthToShort) {
            negativeTextViewFeedback(tvSendCount);
            return false;
        } else if (noneSelected) {
            negativeTextViewFeedback(tvSendContacts);
            return false;
        } else if (emptyMessage) {
            Toast.makeText(getApplicationContext(), new StringResource(R.string.share_message_empty).getValue(), Toast.LENGTH_SHORT).show();
            return false;
        } else if (noUrl) {
            askForUrl();
            return false;
        } else if (haveName) {
            askForName();
        }

        return true;
    }

    private void askForUrl() {
        final String url = ambassadorConfig.getURL();
        AlertDialog dialogBuilder = new AlertDialog.Builder(this)
                .setTitle(new StringResource(R.string.hold_on).getValue())
                .setMessage(new StringResource(R.string.url_missing).getValue() + " " + url)
                .setPositiveButton("Continue Sending", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        send();
                    }
                })
                .setNegativeButton("Insert Link", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        insertURLIntoMessage(etShareMessage, url);

                        dialog.dismiss();
                    }
                }).show();

        dialogBuilder.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.twitter_blue));
        dialogBuilder.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.twitter_blue));
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
        contactNameDialog = new ContactNameDialog(this, progressDialog);
        contactNameDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                contactNameDialog.showKeyboard();
            }
        });
        contactNameDialog.show();
    }

    private boolean pusherHasKey(String key) {
        try {
            String value = pusherData.getString(key);
            return value != null && !value.equals("null") && !value.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void identified(long requestId) {
        send();
    }

    private void send() {
        if (!progressDialog.isShowing()) progressDialog.show();
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
    // endregion

    // endregion

}
