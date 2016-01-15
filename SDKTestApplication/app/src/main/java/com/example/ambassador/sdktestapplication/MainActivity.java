package com.example.ambassador.sdktestapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.AmbassadorSDK;
import com.ambassador.ambassadorsdk.ConversionParametersBuilder;
import com.ambassador.ambassadorsdk.internal.ConversionParameters;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this;

        //dev - run
        AmbassadorSDK.runWithKeys(getApplicationContext(), "SDKToken 9de5757f801ca60916599fa3f3c92131b0e63c6a", "abfd1c89-4379-44e2-8361-ee7b87332e32");

        //prod - run
        //AmbassadorSDK.runWithKeys(getApplicationContext(), "SDKToken 84444f4022a8cd4fce299114bc2e323e57e32188", "830883cd-b2a7-449c-8a3c-d1850aa8bc6b");

        AmbassadorSDK.identify("atestuser@getambassador.com");

        //convert on install - this would normally happen after a user is authenticated because email is required on all conversions
        ConversionParameters conversionParameters = new ConversionParameters();
        conversionParameters.mbsy_email = "atestuser@getambassador.com";
        conversionParameters.mbsy_campaign = 260;
        conversionParameters.mbsy_revenue = 100;

        /** Testing ConversionParametersBuilder */
        // STEP ONE: Create a ConversionParametersBuilder object
        ConversionParametersBuilder builder = new ConversionParametersBuilder();

        // STEP TWO: Set the REQUIRED properties
        builder.setRevenue(10);
        builder.setCampaign(101);
        builder.setEmail("user@example.com");

        // STEP THREE: Set any optional properties that you want
        builder.setAddToGroupId("123");
        builder.setFirstName("John");
        builder.setLastName("Doe");
        builder.setEmailNewAmbassador(0); // Boolean represented by int (Defaults to false)
        builder.setUid("mbsy_uid");
        builder.setCustom1("custom");
        builder.setCustom2("custom");
        builder.setCustom3("custom");
        builder.setAutoCreate(1); // Boolean represented by int (Defaults to true);
        builder.setDeactivateNewAmbassador(0); // Boolean represented by int (Defaults to false)
        builder.setTransactionUid("transaction_uid");
        builder.setEventData1("eventData1");
        builder.setEventData2("eventData2");
        builder.setEventData3("eventData3");
        builder.setIsApproved(1); // Boolean represented by int (Defaults to true);

        // STEP FOUR: Build the object into a ConversionParameters object.
        ConversionParameters conversionParameters1 = builder.build();
        /** ------ */


        AmbassadorSDK.registerConversion(conversionParameters, true);

        Button btnPresentRAF1 = (Button) findViewById(R.id.btnPresentRAF1);
        btnPresentRAF1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmbassadorSDK.presentRAF(context, "260", "raf1.xml");
            }
        });

        Button btnPresentRAF2 = (Button) findViewById(R.id.btnPresentRAF2);
        btnPresentRAF2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmbassadorSDK.presentRAF(context, "260", "raf2.xml");
            }
        });

        Button btnPresentRAF3 = (Button) findViewById(R.id.btnPresentRAF3);
        btnPresentRAF3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmbassadorSDK.presentRAF(context, "260", "raf3.xml");
            }
        });

        Button btnPurchase = (Button) findViewById(R.id.btnPurchase);
        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConversionParameters conversionParameters = new ConversionParameters();
                conversionParameters.mbsy_first_name = "ATest";
                conversionParameters.mbsy_last_name = "User";
                conversionParameters.mbsy_email = "atestuser@getambassador.com"; // COMMENT OUT THIS LINE TO THROW ConversionParametersException
                conversionParameters.mbsy_campaign = 260;
                conversionParameters.mbsy_revenue = 200;

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
