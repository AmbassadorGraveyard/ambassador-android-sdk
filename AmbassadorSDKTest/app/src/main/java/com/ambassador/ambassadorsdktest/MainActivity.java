package com.ambassador.ambassadorsdktest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.ConversionParameters;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //dev
        AmbassadorSDK.runWithKeys(getApplicationContext(), "SDKToken ***REMOVED***", "***REMOVED***");

        //prod
        //AmbassadorSDK.runWithKeys(getApplicationContext(), "SDKToken ***REMOVED***", "***REMOVED***");

        AmbassadorSDK.identify("test12345@getambassador.com");

        //convert on install - this would normally happen after a user is authenticated because email is required on all conversions
        ConversionParameters conversionParameters = new ConversionParameters();
        conversionParameters.email = "test12345@getambassador.com";
        conversionParameters.campaign = 260;
        conversionParameters.revenue = 100;
        AmbassadorSDK.registerConversion(conversionParameters, true);

        Button btnRaf = (Button) findViewById(R.id.btnRAF2);
        final Context context = this;

        btnRaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmbassadorSDK.presentRAF(context, "260");
            }
        });
        
        Button btnPurchase = (Button)findViewById(R.id.btnPurchase);
        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConversionParameters conversionParameters = new ConversionParameters();
                conversionParameters.firstName = "ATest";
                conversionParameters.lastName = "User";
                conversionParameters.email = "test12345@getambassador.com"; // COMMENT OUT THIS LINE TO THROW ConversionParametersException
                conversionParameters.campaign = 260;
                conversionParameters.revenue = 200;

                AmbassadorSDK.registerConversion(conversionParameters, false);

                Toast.makeText(getApplicationContext(), "Cool! (Conversion)", Toast.LENGTH_SHORT).show();
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
