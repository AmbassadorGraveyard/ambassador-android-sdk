package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import org.w3c.dom.Text;


public class AmbassadorActivity extends ActionBarActivity {
    // PUBLIC properties
    public TextView tvWelcomeTitle, tvWelcomeDesc;

    // PRIVATE properties
    private Button btnCopyPaste;
    private EditText etShortUrl;
    private GridView gvSocialGrid;
    private RAFParameters rafParams;
    private final String[] gridTitles = new String[]{"Facebook", "Twitter", "LinkedIn", "Email", "SMS"};
    private final Integer[] gridDrawables = new Integer[]{R.mipmap.facebook_icon, R.mipmap.twitter_icon, R.mipmap.linkedin_icon,
                                                            R.mipmap.email_icon, R.mipmap.sms_icon};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambassador);

        rafParams = (RAFParameters) getIntent().getSerializableExtra("test");

        tvWelcomeTitle = (TextView)findViewById(R.id.tvWelcomeTitle);
        tvWelcomeDesc = (TextView)findViewById(R.id.tvWelcomeDesc);

        setCustomizedText(rafParams);

        gvSocialGrid = (GridView) findViewById(R.id.gvSocialGrid);
        SocialGridAdapter gridAdapter = new SocialGridAdapter(this, gridTitles, gridDrawables);
        gvSocialGrid.setAdapter(gridAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void setCustomizedText(RAFParameters params) {
        tvWelcomeTitle.setText(params.welcomeTitle);
        tvWelcomeDesc.setText(params.welcomeDescription);
    }
}



