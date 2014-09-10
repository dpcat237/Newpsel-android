package com.dpcat237.nps.behavior.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.manager.SyncNewsManager;
import com.dpcat237.nps.behavior.alarm.SyncNewsAlarm;
import com.dpcat237.nps.common.constant.BroadcastConstants;
import com.dpcat237.nps.helper.BroadcastHelper;
import com.dpcat237.nps.helper.ConnectionHelper;
import com.dpcat237.nps.helper.LoginHelper;
import com.dpcat237.nps.ui.activity.MainActivity;

public class SyncNewsService extends IntentService {
    private static final String TAG = "NPS:SyncNewsService";
    private volatile static Boolean running = false;
    private Context mContext;
    private SyncNewsManager syncManager;


    public SyncNewsService() {
        super("SyncNewsService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.mContext = getApplicationContext();
        if (syncManager == null) {
            syncManager = new SyncNewsManager(mContext);
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
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);

        return (LoginHelper.checkLogged(mContext) && pref.getBoolean("pref_items_download_enable", true) && ConnectionHelper.hasConnection(mContext));
    }

    private void startProcess() {
        syncManager.startSync();
        BroadcastHelper.launchBroadcast(mContext, BroadcastConstants.MAIN_ACTIVITY, BroadcastConstants.MAIN_ACTIVITY_MESSAGE, BroadcastConstants.COMMAND_A_MAIN_RELOAD_ITEMS);

        running = false;
        stopSelf();
    }
}
