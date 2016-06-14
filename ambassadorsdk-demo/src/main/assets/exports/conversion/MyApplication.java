package com.example.example;

import android.app.Application;
import android.os.Bundle;
import com.ambassador.ambassadorsdk.ConversionParameters;
import com.ambassador.ambassadorsdk.AmbassadorSDK;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AmbassadorSDK.runWithKeys(this, "SDKToken {{SDKTOKEN}}", "{{UNIVERSALID}}");

        // Create bundle with traits about user
        Bundle traits = new Bundle();
        traits.putString("email", "{{EMAIL}}");
        traits.putString("firstName", "{{FIRSTNAME}}");
        traits.putString("lastName", "{{LASTNAME}}");
        traits.putString("addToGroups", "{{GROUPS}}");
        traits.putString("customLabel1", "{{CUSTOM1}}");
        traits.putString("customLabel2", "{{CUSTOM2}}");
        traits.putString("customLabel3", "{{CUSTOM3}}");

        // Create bundle with option to auto-enroll user in campaign
        Bundle options = new Bundle();
        options.putString("campaign", "{{CAMPAIGN}}");

        AmbassadorSDK.identify("{{USERID}}", traits, options);

        // Create properties bundle
        Bundle properties = new Bundle();
        properties.putInt("campaign", {{CAMPAIGN}});
        properties.putFloat("revenue", {{REVENUE}}f);
        properties.putInt("commissionApproved", {{COMMISSIONAPPROVED}});
        properties.putString("eventData1", "{{EVENTDATA1}}");
        properties.putString("eventData2", "{{EVENTDATA2}}");
        properties.putString("eventData3", "{{EVENTDATA3}}");
        properties.putString("orderId", "{{ORDERID}}");
        properties.putInt("emailNewAmbassadord", {{EMAILNEWAMBASSADOR}});

        // Create options bundle
        properties.putBoolean("conversion", true);

        AmbassadorSDK.trackEvent("Event Name", properties, options, new ConversionStatusListener() {
            @Override
            public void success() {
                println("Success!");
            }

            @Override
            public void pending() {
                println("Pending!");
            }

            @Override
            public void error() {
                println("Error!");
            }
        });
    }
}