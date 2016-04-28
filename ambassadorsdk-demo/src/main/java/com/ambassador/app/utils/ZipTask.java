package com.ambassador.app.utils;


import android.os.AsyncTask;

import com.ambassador.app.Demo;
import com.ambassador.app.exports.IntegrationExport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZipTask extends AsyncTask<IntegrationExport, Void, Void> {

    protected static List<Long> runningTasks = new ArrayList<>();
    protected static Map<Long, List<OnTaskCompleteListener>> listeners = new HashMap<>();

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

        if (listeners.keySet().contains(id)) {
            for (OnTaskCompleteListener listener : listeners.get(id)) {
                listener.onTaskComplete();
            }

            listeners.get(id).clear();
        }
    }

    public static boolean isRunning(long id) {
        return runningTasks.contains(id);
    }

    public static void addOnTaskCompleteListener(long id, OnTaskCompleteListener onTaskCompleteListener) {
        if (listeners.keySet().contains(id)) {
            listeners.get(id).add(onTaskCompleteListener);
        } else {
            List<OnTaskCompleteListener> toAdd = new ArrayList<>();
            toAdd.add(onTaskCompleteListener);
            listeners.put(id, toAdd);
        }
    }

    public interface OnTaskCompleteListener {
        void onTaskComplete();
    }

}

