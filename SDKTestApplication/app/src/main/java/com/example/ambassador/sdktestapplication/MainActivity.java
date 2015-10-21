package com.example.ambassador.sdktestapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.ConversionParameters;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this;

        //dev
        AmbassadorSDK.runWithKeys(getApplicationContext(), "SDKToken 9de5757f801ca60916599fa3f3c92131b0e63c6a", "abfd1c89-4379-44e2-8361-ee7b87332e32");

        //prod
        //AmbassadorSDK.runWithKeys(getApplicationContext(), "SDKToken 84444f4022a8cd4fce299114bc2e323e57e32188", "830883cd-b2a7-449c-8a3c-d1850aa8bc6b");

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                AmbassadorSDK.identify("jake@getambassador.com");
            }
        }, 10000);

        Button btnRAF = (Button)findViewById(R.id.btnShowRAF);
        btnRAF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmbassadorSDK.presentRAF(context, "260");

                ConversionParameters conversionParameters = new ConversionParameters();
                conversionParameters.mbsy_first_name = "Jake";
                conversionParameters.mbsy_last_name = "Dunahee";
                conversionParameters.mbsy_email = "jake@getambassador.com"; // COMMENT OUT THIS LINE TO THROW ConversionParametersException
                conversionParameters.mbsy_campaign = 305;
                conversionParameters.mbsy_revenue = 200;

                AmbassadorSDK.registerConversion(conversionParameters);
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
