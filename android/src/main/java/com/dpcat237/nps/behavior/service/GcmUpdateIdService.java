package com.dpcat237.nps.behavior.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dpcat237.nps.behavior.alarm.SyncNewsAlarm;
import com.dpcat237.nps.behavior.manager.GcmManager;
import com.dpcat237.nps.helper.ConnectionHelper;
import com.dpcat237.nps.helper.LoginHelper;

public class GcmUpdateIdService extends IntentService {
    private static final String TAG = "NPS:GcmUpdateIdService";
    private volatile static Boolean running = false;
    private Context mContext;
    private GcmManager gcmManager;


    public GcmUpdateIdService() {
        super("SyncLaterService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.mContext = getApplicationContext();
        if (gcmManager == null) {
            gcmManager = new GcmManager(mContext);
        }

        if (!running) {
            synchronized ("running") {
                if (!running && checkCanRun()) {
                    running = true;

                    try {
                        startProcess();
                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage());
                    }
                }
            }
        }

        SyncNewsAlarm.completeWakefulIntent(intent);
    }

    private Boolean checkCanRun() {
        return (LoginHelper.checkLogged(mContext) && ConnectionHelper.hasConnection(mContext));
    }

    private void startProcess() {
        gcmManager.updateId();

        running = false;
        stopSelf();
    }
}
