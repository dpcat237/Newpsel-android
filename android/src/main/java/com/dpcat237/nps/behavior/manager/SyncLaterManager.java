package com.dpcat237.nps.behavior.manager;

import android.content.Context;

import com.dpcat237.nps.behavior.factory.SyncFactoryManager;
import com.dpcat237.nps.constant.SyncConstants;

public class SyncLaterManager {
    private static final String TAG = "NPS:SyncLaterManager";
    private Context mContext;
    private SyncFactoryManager syncManager;

    public SyncLaterManager(Context context)
    {
        mContext = context;
        syncManager = new SyncFactoryManager();
    }

    public void startSync() {
        syncManager.syncProcess(mContext, SyncConstants.SYNC_LATER_ITEMS);
    }
}
