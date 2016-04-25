package com.ambassador.demo.activities.main.conversion;

import com.ambassador.demo.utils.Share;

public interface ConversionView {

    void toggleEnrollAsAmbassadorInputs(boolean isChecked);
    void closeSoftKeyboard();
    void share(Share share);

}
