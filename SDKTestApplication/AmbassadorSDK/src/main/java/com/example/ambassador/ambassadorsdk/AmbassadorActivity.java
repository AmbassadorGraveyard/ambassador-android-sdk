package com.example.ambassador.ambassadorsdk;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * Created by JakeDunahee on 7/22/15.
 */
public class AmbassadorActivity extends AppCompatActivity {
    CustomEditText etShortUrl;
    private ProgressDialog pd;
    private LinearLayout llMainLayout;
    private Timer networkTimer;
    private final android.os.Handler timerHandler = new android.os.Handler();
    private final String[] gridTitles = new String[]{"FACEBOOK", "TWITTER", "LINKEDIN", "EMAIL", "SMS"};
    private final Integer[] gridDrawables = new Integer[]{R.drawable.facebook_icon, R.drawable.twitter_icon, R.drawable.linkedin_icon,
            R.drawable.email_icon, R.drawable.sms_icon};

    final private Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(getApplicationContext(), "There seems to be an issue while attempting to load. Please try again.", Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    final private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        // Executed when Pusher data is received, used to update the shortURL editText if loading screen is present
        @Override
        public void onReceive(Context context, Intent intent) {
            tryAndSetURL(ambassadorConfig.getPusherInfo(), ambassadorConfig.getRafParameters().defaultShareMessage);
        }
    };

    @Inject
    TweetDialog tweetDialog;

    @Inject
    LinkedInDialog linkedInDialog;

    @Inject
    ShareDialog fbDialog;

    @Inject
    RequestManager requestManager;

    @Inject
    AmbassadorConfig ambassadorConfig;

    // ACTIVITY OVERRIDE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ambassador);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Roboto-RobotoRegular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        FacebookSdk.sdkInitialize(getApplicationContext());

        //tell Dagger to inject dependencies
        AmbassadorSingleton.getAmbModule().setContext(this);
        AmbassadorSingleton.getComponent().inject(this);

        ambassadorConfig.setRafParameters(
                getResources().getString(R.string.RAFdefaultShareMessage),
                getResources().getString(R.string.RAFtitleText),
                getResources().getString(R.string.RAFdescriptionText),
                getResources().getString(R.string.RAFtoolbarTitle));

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("pusherData"));

        // UI Components
        llMainLayout = (LinearLayout) findViewById(R.id.llMainLayout);
        GridView gvSocialGrid = (GridView) findViewById(R.id.gvSocialGrid);
        ImageButton btnCopyPaste = (ImageButton) findViewById(R.id.btnCopyPaste);
        TextView tvWelcomeTitle = (TextView) findViewById(R.id.tvWelcomeTitle);
        TextView tvWelcomeDesc = (TextView) findViewById(R.id.tvWelcomeDesc);
        etShortUrl = (CustomEditText) findViewById(R.id.etShortURL);

        tvWelcomeTitle.setText(ambassadorConfig.getRafParameters().titleText);
        tvWelcomeDesc.setText(ambassadorConfig.getRafParameters().descriptionText);
        _setUpToolbar(ambassadorConfig.getRafParameters().toolbarTitle);

        loadCustomImages();

        btnCopyPaste.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                copyShortURLToClipboard(etShortUrl.getText().toString(), getApplicationContext());
            }
        });

        btnCopyPaste.setColorFilter(getResources().getColor(R.color.ultraLightGray));

        // Sets up social gridView
        SocialGridAdapter gridAdapter = new SocialGridAdapter(this, gridTitles, gridDrawables);
        gvSocialGrid.setAdapter(gridAdapter);
        gvSocialGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                respondToGridViewClick(position);
            }
        });

        pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setOwnerActivity(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });

        pd.show();

        networkTimer = new Timer();
        networkTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                _showNetworkError();
            }
        }, 30000);

        requestManager.identifyRequest();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        if (networkTimer != null) { networkTimer.cancel(); }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish(); // Dismisses activity if back button pressed
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    // END ACTIVITY OVERRIDE METHODS


    // ONCLICK METHODS
    String copyShortURLToClipboard(String copyText, Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simpleText", copyText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();

        return clip.toString();
    }

    int respondToGridViewClick(int position) {
        // Functionality: Handles items being clicked in the gridview
        switch (position) {
            case 0:
                shareWithFacebook();
                break;
            case 1:
                shareWithTwitter();
                break;
            case 2:
                shareWithLinkedIn();
                break;
            case 3:
                goToContactsPage(false);
                break;
            case 4:
                goToContactsPage(true);
                break;
            default:
                return -1;
        }

        return position;
    }

    void loadCustomImages() {
        //first check if an image exists
        int drawableId = getResources().getIdentifier("raf_logo", "drawable", getPackageName());
        if (drawableId == 0) return;

        int pos;
        try {
            pos = Integer.parseInt(getString(R.string.RAFLogoPosition));
        }
        catch (NumberFormatException e) {
            pos = 0;
        }

        if (pos >= 1 && pos <= 5) {
            ImageView logo = new ImageView(this);
            logo.setImageDrawable(ContextCompat.getDrawable(this, drawableId));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, Utilities.getPixelSizeForDimension(R.dimen.raf_logo_height));
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.topMargin = Utilities.getPixelSizeForDimension(R.dimen.raf_logo_top_margin);
            logo.setLayoutParams(params);
            llMainLayout.addView(logo, pos-1);
        }
    }

    void goToContactsPage(Boolean showPhoneNumbers) {
        Intent contactIntent = new Intent(this, ContactSelectorActivity.class);
        contactIntent.putExtra("showPhoneNumbers", showPhoneNumbers);
        startActivity(contactIntent);
    }

    void shareWithFacebook() {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle(ambassadorConfig.getRafParameters().defaultShareMessage)
                .setContentUrl(Uri.parse(ambassadorConfig.getURL()))
                .build();

        fbDialog.show(content);
    }

    void shareWithTwitter() {
        // Presents twitter login screen if user has not logged in yet
        if (ambassadorConfig.getTwitterAccessToken() != null) {
            tweetDialog.setOwnerActivity(this);
            tweetDialog.show();
        } else {
            Intent i = new Intent(this, TwitterLoginActivity.class);
            startActivity(i);
        }
    }

    void shareWithLinkedIn() {
        // Presents login screen if user hasn't signed in yet
        if (ambassadorConfig.getLinkedInToken() != null) {
            linkedInDialog.setOwnerActivity(this);
            linkedInDialog.show();
        } else {
            Intent intent = new Intent(this, LinkedInLoginActivity.class);
            startActivity(intent);
        }
    }
    // END ONCLICK METHODS


    // UI SETTER METHODS
    void tryAndSetURL(String pusherString, String initialShareMessage) {
        // Functionality: Gets URL from Pusher
        // First checks to see if Pusher info has already been saved to SharedPreferencs
        if (pd != null) {
            pd.dismiss();
            networkTimer.cancel();
        }

        try {
            // We get a JSON object from the Pusher Info string saved to SharedPreferences
            JSONObject pusherData = new JSONObject(pusherString);
            JSONArray urlArray = pusherData.getJSONArray("urls");

            // Iterates throught all the urls in the Pusher object until we find one will a matching campaign ID
            for (int i = 0; i < urlArray.length(); i++) {
                JSONObject urlObj = urlArray.getJSONObject(i);
                int campID = urlObj.getInt("campaign_uid");
                int myUID = Integer.parseInt(ambassadorConfig.getCampaignID());
                if (campID == myUID) {
                    etShortUrl.setText(urlObj.getString("url"));
                    ambassadorConfig.setURL(urlObj.getString("url"));
                    ambassadorConfig.setShortCode(urlObj.getString("short_code"));
                    ambassadorConfig.setEmailSubject(urlObj.getString("subject"));
                    ambassadorConfig.setRafDefaultMessage(initialShareMessage + " " + urlObj.getString("url"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void _showNetworkError() {
        // Functionality: After 30 seconds of waiting for data from Pusher/Augur, shows toast to user
        // stating that there is a network error and then dismisses the RAF activity
        timerHandler.post(myRunnable);
    }

    private void _setUpToolbar(String toolbarTitle) {
        if (getSupportActionBar() != null) { getSupportActionBar().setTitle(toolbarTitle); }

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        if (toolbar == null) return;

        final Drawable arrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        arrow.setColorFilter(getResources().getColor(R.color.homeToolBarArrow), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(arrow);

        toolbar.setBackgroundColor(getResources().getColor(R.color.homeToolBar));
        toolbar.setTitleTextColor(getResources().getColor(R.color.homeToolBarText));
//        TextView toolbarTextView = (TextView) toolbar.findViewById(R.id.title);
//        toolbarTextView.setText("tacos");
    }
    // END UI SETTER METHODS
}