package com.ambassador.app.utils;


import android.os.AsyncTask;

import com.ambassador.app.Demo;
import com.ambassador.app.exports.IntegrationExport;

import java.util.ArrayList;
import java.util.List;

public class ZipTask extends AsyncTask<IntegrationExport, Void, Void> {

    protected static List<Long> runningTasks = new ArrayList<>();

    protected long id;

    public ZipTask(long id) {
        super();
        this.id = id;
    }

    @Override
    protected Void doInBackground(IntegrationExport... params) {
        if (params.length < 0) {
            return null;
        }

        runningTasks.add(id);

        IntegrationExport export = params[0];
        export.zip(Demo.get());

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        runningTasks.remove(id);
    }

    public static boolean isRunning(long id) {
        return runningTasks.contains(id);
    }

}

