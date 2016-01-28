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
import com.ambassador.ambassadorsdk.ConversionParameters;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this;

        //dev - run
        AmbassadorSDK.runWithKeys(getApplicationContext(), "SDKToken ***REMOVED***", "***REMOVED***");

        //prod - run
        //AmbassadorSDK.runWithKeys(getApplicationContext(), "SDKToken ***REMOVED***", "***REMOVED***");

        AmbassadorSDK.identify("atestuser@getambassador.com");

        //convert on install - this would normally happen after a user is authenticated because email is required on all conversions
        ConversionParameters conversionParameters = new ConversionParameters();
        conversionParameters.email = "atestuser@getambassador.com";
        conversionParameters.campaign = 260;
        conversionParameters.revenue = 100;

        /** Testing ConversionParametersBuilder */
        // STEP ONE: Create a ConversionParametersBuilder object
        ConversionParameters.Builder builder = new ConversionParameters.Builder();

        // STEP TWO: Set the REQUIRED properties
        builder.setRevenue(10);
        builder.setCampaign(101);
        builder.setEmail("user@example.com");

        // STEP THREE: Set any optional properties that you want
        builder.setAddToGroupId("123");
        builder.setFirstName("John");
        builder.setLastName("Doe");
        builder.setEmailNewAmbassador(0); // Boolean represented by int (Defaults to false)
        builder.setUid("uid");
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

        Button btnPresentRAF4 = (Button) findViewById(R.id.btnPresentRAF4);
        btnPresentRAF4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmbassadorSDK.presentRAF(context, "260");
            }
        });

        Button btnPresentRAF5 = (Button) findViewById(R.id.btnPresentRAF5);
        btnPresentRAF5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmbassadorSDK.presentRAF(context, "260", "raf5.xml");
            }
        });

        Button btnPresentRAF6 = (Button) findViewById(R.id.btnPresentRAF6);
        btnPresentRAF6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmbassadorSDK.presentRAF(context, "999");
            }
        });

        Button btnPresentRAF7 = (Button) findViewById(R.id.btnPresentRAF7);
        btnPresentRAF7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmbassadorSDK.presentRAF(context, "260", "raf7.xml");
            }
        });

        Button btnPresentRAF8 = (Button) findViewById(R.id.btnPresentRAF8);
        btnPresentRAF8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmbassadorSDK.presentRAF(context, "260", "raf8.xml");
            }
        });

        Button btnPresentRAF9 = (Button) findViewById(R.id.btnPresentRAF9);
        btnPresentRAF9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AmbassadorSDK.presentRAF(context, "12312312321", "raf9.xml");
            }
        });

        Button btnPurchase = (Button) findViewById(R.id.btnPurchase);
        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConversionParameters conversionParameters = new ConversionParameters();
                conversionParameters.firstName = "ATest";
                conversionParameters.lastName = "User";
                conversionParameters.email = "atestuser@getambassador.com"; // COMMENT OUT THIS LINE TO THROW ConversionParametersException
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
