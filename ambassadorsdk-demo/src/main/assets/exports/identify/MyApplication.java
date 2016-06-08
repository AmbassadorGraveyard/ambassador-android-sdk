package com.example.example;

import android.app.Application;
import android.os.Bundle;

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
        traits.putString("company", "{{COMPANY}}");
        traits.putString("phone", "{{PHONE}}");

        // Create an address bundle to go inside of traits bundle
        Bundle address = new Bundle();
        address.putString("street", "{{STREET}}");
        address.putString("city", "{{CITY}}");
        address.putString("state", "{{STATE}}");
        address.putString("postalCode", "{{POSTALCODE}}");
        address.putString("country", "{{COUNTRY}}");
        traits.putBundle("address", address);

        // Create bundle with option to auto-enroll user in campaign
        Bundle options = new Bundle();
        options.putString("campaign", "{{CAMPAIGN}}");

        AmbassadorSDK.identify("{{USERID}}", traits, options);
    }

}