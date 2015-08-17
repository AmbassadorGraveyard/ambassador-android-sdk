package com.example.ambassador.ambassadorsdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by JakeDunahee on 7/22/15.
 */
public class Ambassador {
    public static void presentRAF(Context context, RAFParameters rafParameters, String campaignID) {
        Intent intent = new Intent(context, AmbassadorActivity.class);

        if (rafParameters == null) {
            rafParameters = new RAFParameters();
        }

        intent.putExtra("test", rafParameters);
        context.startActivity(intent);

        AmbassadorSingleton.getInstance().setCampaignID(campaignID);
    }

    public static void identify(String email) {
        AmbassadorSingleton.getInstance().startIdentify(email);
    }
}
