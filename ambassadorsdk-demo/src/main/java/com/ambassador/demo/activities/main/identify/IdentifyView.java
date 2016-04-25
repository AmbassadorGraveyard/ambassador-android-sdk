package com.ambassador.demo.activities.main.identify;

import com.ambassador.demo.utils.Share;

public interface IdentifyView {

    void notifyNoEmail();
    void notifyInvalidEmail();
    void notifyIdentifying();

    void closeSoftKeyboard();

    void share(Share share);

}
