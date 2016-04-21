package com.ambassador.demo.activities.main.settings;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.ambassador.ambassadorsdk.internal.activities.BasePresenter;
import com.ambassador.demo.Demo;
import com.ambassador.demo.data.User;

public class SettingsPresenter extends BasePresenter<SettingsModel, SettingsView> {

    @Override
    protected void updateView() {
        view().setUserPicture(model.pictureUrl);
        view().setUserName(model.name);
        view().setUniversalId(model.universalId);
        view().setSdkToken(model.sdkToken);
    }

    @Override
    public void bindView(@NonNull SettingsView view) {
        super.bindView(view);

        if (model == null) {
            loadData();
        }
    }

    protected void loadData() {
        SettingsModel settingsModel = new SettingsModel();
        settingsModel.pictureUrl = User.get().getAvatarUrl();
        settingsModel.name = User.get().getName();
        settingsModel.universalId = User.get().getUniversalId();
        settingsModel.sdkToken = User.get().getSdkToken();
        setModel(settingsModel);
    }

    public void onCopyUniversalIdClicked() {
        copyToClipboard(model.universalId);
    }

    public void onCopySdkTokenClicked() {
        copyToClipboard(model.sdkToken);
    }

    public void onLogoutClicked() {
        User.logout();
        view().logout();
    }

    protected void copyToClipboard(String text) {
        ClipboardManager clipboardManager = (ClipboardManager) Demo.get().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("simpleText", text));
        Toast.makeText(Demo.get(), "Copied to clipboard!", Toast.LENGTH_SHORT).show();
    }

}
