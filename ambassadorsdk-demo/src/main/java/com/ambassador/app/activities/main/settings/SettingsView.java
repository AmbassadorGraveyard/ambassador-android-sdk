package com.ambassador.app.activities.main.settings;

public interface SettingsView {

    void setUserPicture(String url);
    void setUserName(String name);
    void setUniversalId(String universalId);
    void setSdkToken(String sdkToken);
    void logout();
    void notifyCopiedToClipboard();

}
