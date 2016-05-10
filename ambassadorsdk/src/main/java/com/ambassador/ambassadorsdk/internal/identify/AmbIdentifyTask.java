package com.ambassador.ambassadorsdk.internal.identify;

import com.ambassador.ambassadorsdk.internal.AmbSingleton;
import com.ambassador.ambassadorsdk.internal.data.User;
import com.ambassador.ambassadorsdk.internal.utils.Device;

import javax.inject.Inject;

public abstract class AmbIdentifyTask {

    @Inject protected User user;
    @Inject protected Device device;

    public AmbIdentifyTask() {
        AmbSingleton.inject(this);
    }

    public abstract void execute(OnCompleteListener onCompleteListener) throws Exception;

    public interface OnCompleteListener {
        void complete();
    }

}
