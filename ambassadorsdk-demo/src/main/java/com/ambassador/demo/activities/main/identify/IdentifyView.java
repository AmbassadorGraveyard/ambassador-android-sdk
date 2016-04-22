package com.ambassador.demo.activities.main.identify;

public interface IdentifyView {

    String getEmailAddress();

    void notifyNoEmail();
    void notifyInvalidEmail();
    void notifyIdentifying();

    void closeSoftKeyboard();

}
