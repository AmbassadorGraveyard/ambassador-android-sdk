package com.example.ambassador.ambassadorsdk;

import java.io.Serializable;

/**
 * Created by JakeDunahee on 7/22/15.
 */
public class RAFParameters implements Serializable {
    public String toolbarTitle, welcomeTitle, welcomeDescription;

    public RAFParameters () {
        toolbarTitle = "Refer your friends";
        welcomeTitle = "Spread the word";
        welcomeDescription = "Refer a friend to get a reward";
    }
}
