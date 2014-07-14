package com.dpcat237.nps.behavior.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.dpcat237.nps.behavior.receiver.AlarmRemoveOldReceiver;
import com.dpcat237.nps.database.repository.DictateItemRepository;

public class RemoveDictationsService extends IntentService {
    private static final String TAG = "NPS:RemoveDictationsService";
    private Intent mIntent;
    private Context mContext;


    public RemoveDictationsService() {
        super("SchedulingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.mIntent = intent;
        this.mContext = getApplicationContext();

        if (checkCanRun()) {
            try {
                startProcess();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private Boolean checkCanRun() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        Boolean dictationTitleEnabled = pref.getBoolean("pref_dictation_title_enable", false);
        Boolean dictationLaterEnabled = pref.getBoolean("pref_dictation_later_enable", false);

        return (dictationTitleEnabled || dictationLaterEnabled);
    }

    private void startProcess() {
        DictateItemRepository dictateRepo = new DictateItemRepository(mContext);
        dictateRepo.deleteItemsTtsError();
        dictateRepo.close();

        AlarmRemoveOldReceiver.completeWakefulIntent(mIntent);
        stopSelf();
    }
}
