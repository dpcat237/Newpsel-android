package com.dpcat237.nps.behavior.factory;

import android.content.Context;
import android.util.Log;

import com.dpcat237.nps.behavior.factory.syncManager.SyncManager;

public class SyncFactoryManager {
    private static final String TAG = "NPS:SyncFactoryManager";

    public Boolean syncProcess(Context context, String type) {
        Boolean result;
        SyncManager syncManager = SyncFactory.createManager(type);

        try {
            syncManager.setup(context);
            if (syncManager.areError()) {
                syncManager.finish();

                return false;
            }

            syncManager.makeRequest();
            if (syncManager.areError()) {
                syncManager.finish();

                return false;
            }

            syncManager.processData();
            syncManager.finish();
            result = syncManager.getResult();
        } catch (Exception e) {
            result = false;
            Log.d(TAG+' '+type, "tut: Exception "+e.getMessage());
        }

        return result;
    }


}
