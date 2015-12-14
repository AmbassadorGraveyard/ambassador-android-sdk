package com.ambassador.ambassadorsdk.conversions;

import android.content.Context;
import android.content.SharedPreferences;

import com.ambassador.ambassadorsdk.internal.AmbassadorSingleton;

/**
 * Created by dylan on 12/14/15.
 */
public class ConversionConfig {

    private Context context = AmbassadorSingleton.get();
    private SharedPreferences sharePrefs = context.getSharedPreferences("appContext", Context.MODE_PRIVATE);

    String getReferralShortCode() { return sharePrefs.getString("referralShortCode", null); }

    String getIdentifyObject() { return sharePrefs.getString("identifyObject", null); }

}
