package com.ambassador.ambassadorsdk.internal;

import java.io.Serializable;

public class ServiceSelectorPreferences implements Serializable {
    public String toolbarTitle, titleText, descriptionText, defaultShareMessage;

    public ServiceSelectorPreferences () {
        toolbarTitle = "Refer your friends";
        titleText = "Spread the word";
        descriptionText = "Refer a friend to get a reward";
        defaultShareMessage = "Check out this awesome company!";
    }
}
