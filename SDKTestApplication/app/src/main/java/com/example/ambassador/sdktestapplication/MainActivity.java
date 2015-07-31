package com.example.ambassador.sdktestapplication;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.ambassador.ambassadorsdk.Ambassador;
import com.example.ambassador.ambassadorsdk.RAFParameters;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends ActionBarActivity {
    Button btnRAF;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context cxt = this;

        Ambassador.identify();

        btnRAF = (Button)findViewById(R.id.btnShowRAF);
        btnRAF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RAFParameters parameters = new RAFParameters();
                parameters.shareMessage = "Check out this company!";
                parameters.welcomeTitle = "RAF Params Welcome Title";
                parameters.welcomeDescription = "RAF Params Welcome Description";
                parameters.toolbarTitle = "RAF Params Toolbar Title";
                Ambassador.presentRAF(cxt, parameters);
            }
        });

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
