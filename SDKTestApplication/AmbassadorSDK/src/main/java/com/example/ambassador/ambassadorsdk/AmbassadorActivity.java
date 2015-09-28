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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
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


/**
 * Created by JakeDunahee on 7/22/15.
 */
public class AmbassadorActivity extends AppCompatActivity {
    CustomEditText etShortUrl;
    private ServiceSelectorPreferences rafParams;
    private ProgressDialog pd;
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
            tryAndSetURL(AmbassadorSingleton.getInstance().getPusherInfo(), rafParams.defaultShareMessage);
        }
    };

    //@Inject
    //TweetDialog tweetDialog;

    //@Inject
    //LinkedInDialog linkedInDialog;

    //@Inject
    //ShareDialog fbDialog;

    // ACTIVITY OVERRIDE METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambassador);

        FacebookSdk.sdkInitialize(getApplicationContext());

        //tell Dagger to inject dependencies
        MyApplication.getComponent().inject(this);

        rafParams = new ServiceSelectorPreferences();
        rafParams.defaultShareMessage = getResources().getString(R.string.RAFdefaultShareMessage);
        rafParams.titleText = getResources().getString(R.string.RAFtitleText);
        rafParams.descriptionText = getResources().getString(R.string.RAFdescriptionText);
        rafParams.toolbarTitle = getResources().getString(R.string.RAFtoolbarTitle);
        AmbassadorSingleton.getInstance().rafParameters = rafParams;
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("pusherData"));

        // UI Components
        GridView gvSocialGrid = (GridView) findViewById(R.id.gvSocialGrid);
        ImageButton btnCopyPaste = (ImageButton) findViewById(R.id.btnCopyPaste);
        TextView tvWelcomeTitle = (TextView) findViewById(R.id.tvWelcomeTitle);
        TextView tvWelcomeDesc = (TextView) findViewById(R.id.tvWelcomeDesc);
        etShortUrl = (CustomEditText) findViewById(R.id.etShortURL);

        tvWelcomeTitle.setText(rafParams.titleText);
        tvWelcomeDesc.setText(rafParams.descriptionText);
        _setUpToolbar(rafParams.toolbarTitle);

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

        Identify identify = new Identify(MyApplication.getAppContext(), AmbassadorSingleton.getInstance().getUserEmail());
        identify.performIdentifyRequest();
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

    void goToContactsPage(Boolean showPhoneNumbers) {
        Intent contactIntent = new Intent(this, ContactSelectorActivity.class);
        contactIntent.putExtra("showPhoneNumbers", showPhoneNumbers);
        startActivity(contactIntent);
    }

    void shareWithFacebook() {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle(rafParams.defaultShareMessage)
                .setContentUrl(Uri.parse(AmbassadorSingleton.getInstance().getURL()))
                .build();

        ShareDialog fbDialog = new ShareDialog(this);
        fbDialog.show(content);
    }

    void shareWithTwitter() {
        // Presents twitter login screen if user has not logged in yet
        if (AmbassadorSingleton.getInstance().getTwitterAccessToken() != null) {
            TweetDialog tweetDialog = new TweetDialog(this);
            tweetDialog.setOwnerActivity(this);
            tweetDialog.show();
        } else {
            Intent i = new Intent(this, TwitterLoginActivity.class);
            startActivity(i);
        }
    }

    void shareWithLinkedIn() {
        // Presents login screen if user hasn't signed in yet
        if (AmbassadorSingleton.getInstance().getLinkedInToken() != null) {
            LinkedInDialog linkedInDialog = new LinkedInDialog(this);
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
                int myUID = Integer.parseInt(AmbassadorSingleton.getInstance().getCampaignID());
                if (campID == myUID) {
                    etShortUrl.setText(urlObj.getString("url"));
                    AmbassadorSingleton.getInstance().saveURL(urlObj.getString("url"));
                    AmbassadorSingleton.getInstance().saveShortCode(urlObj.getString("short_code"));
                    AmbassadorSingleton.getInstance().saveEmailSubject(urlObj.getString("subject"));
                    AmbassadorSingleton.getInstance().setRafDefaultMessage(initialShareMessage + " " + urlObj.getString("url"));
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
    }
    // END UI SETTER METHODS
}