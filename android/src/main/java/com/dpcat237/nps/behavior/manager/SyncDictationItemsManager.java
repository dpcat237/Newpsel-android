package com.dpcat237.nps.behavior.manager;

import android.content.Context;
import android.util.Log;

import com.dpcat237.nps.behavior.factory.SyncFactoryManager;
import com.dpcat237.nps.constant.SyncConstants;

public class SyncDictationItemsManager {
    private static final String TAG = "NPS:SyncDictationItemsManager";
    private Context mContext;
    private SyncFactoryManager syncManager;

    public SyncDictationItemsManager(Context context)
    {
        mContext = context;
        syncManager = new SyncFactoryManager();
    }

    public void syncDictations() {
        Log.d(TAG, "tut: syncDictations");
        syncManager.syncProcess(mContext, SyncConstants.SYNC_DICTATION_ITEMS);
        Log.d(TAG, "tut: finish syncDictations");
    }
}
