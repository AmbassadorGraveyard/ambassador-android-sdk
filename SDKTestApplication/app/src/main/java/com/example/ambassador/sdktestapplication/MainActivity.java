package com.example.ambassador.sdktestapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.ambassador.ambassadorsdk.AmbassadorSDK;
import com.example.ambassador.ambassadorsdk.ConversionParameters;


public class MainActivity extends AppCompatActivity {
    Button btnRAF;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context cxt = this;

        //dev
        AmbassadorSDK.runWithKeys(getApplicationContext(), "SDKToken ***REMOVED***", "***REMOVED***");

        //prod
        //AmbassadorSDK.runWithKeys(getApplicationContext(), "SDKToken ***REMOVED***", "***REMOVED***");

        AmbassadorSDK.identify("jake@getambassador.com");

        btnRAF = (Button)findViewById(R.id.btnShowRAF);
        btnRAF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmbassadorSDK.presentRAF(cxt, "260");

                ConversionParameters conversionParameters = new ConversionParameters();
                conversionParameters.mbsy_first_name = "Jake";
                conversionParameters.mbsy_last_name = "Dunahee";
                conversionParameters.mbsy_email = "jake@getambassador.com"; // COMMENT OUT THIS LINE TO THROW ConversionParamtersException
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
