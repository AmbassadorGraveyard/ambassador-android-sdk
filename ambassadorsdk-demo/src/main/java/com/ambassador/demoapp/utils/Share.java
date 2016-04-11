package com.ambassador.demoapp.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;

import com.ambassador.demoapp.Demo;

import java.io.File;

public class Share {

    protected Uri uri;

    public Share(String filename) {
        this(new File(Demo.get().getFilesDir(), filename));
    }

    public Share(File file) {
        this(FileProvider.getUriForFile(Demo.get(), "com.ambassador.fileprovider", file));
    }

    public Share(Uri uri) {
        this.uri = uri;
    }

    public void execute(Activity activity) {
        if (uri == null) return;

        final Intent intent = ShareCompat.IntentBuilder.from(activity)
                .setType("*/*")
                .setStream(uri)
                .setChooserTitle("Share Integration")
                .createChooserIntent()
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        activity.startActivity(intent);
    }

}
