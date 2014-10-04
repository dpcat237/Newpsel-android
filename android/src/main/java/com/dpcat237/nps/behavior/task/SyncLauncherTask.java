package com.dpcat237.nps.behavior.task;

import android.content.Context;
import android.os.AsyncTask;

import com.dpcat237.nps.behavior.valueObject.SyncLauncherStatus;

public class SyncLauncherTask extends AsyncTask<Void, Integer, Void> {
    private static final String TAG = "NPS:SyncLauncherTask";
    private Context mContext;
    private Integer service;
    private SyncLauncherStatus syncLauncher;

    public SyncLauncherTask(Context context, Integer service) {
        mContext = context;
        this.service = service;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        syncLauncher = SyncLauncherStatus.getInstance(mContext);
    }

    @Override
    protected Void doInBackground(Void... params) {
        syncLauncher.requireSync(service);

        return null;
    }
}