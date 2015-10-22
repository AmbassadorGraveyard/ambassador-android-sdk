package com.ambassador.ambassadorsdk;

import javax.inject.Inject;


/**
 * Created by JakeDunahee on 9/1/15.
 */
class Identify implements IIdentify {
    IdentifyAugurSDK augur;

    @Inject
    AmbassadorConfig ambassadorConfig;

    public Identify() {
        AmbassadorSingleton.getComponent().inject(this);
        augur = new IdentifyAugurSDK();
    }

    @Override
    public void getIdentity() {
        augur.getAugur(ambassadorConfig);
    }
}
