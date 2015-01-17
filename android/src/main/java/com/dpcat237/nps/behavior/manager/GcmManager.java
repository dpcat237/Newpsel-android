package com.dpcat237.nps.behavior.manager;

import android.content.Context;
import android.util.Log;

import com.dpcat237.nps.behavior.factory.SyncFactoryManager;
import com.dpcat237.nps.constant.GcmConstants;
import com.dpcat237.nps.constant.SyncConstants;
import com.dpcat237.nps.helper.GcmHelper;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class GcmManager {
    private static final String TAG = "NPS:GcmManager";
    private Context mContext;


    public GcmManager(Context context)
    {
        mContext = context;
    }

    public void updateId() {
        String regId = getRegistrationId();
        if (regId.isEmpty()) {
            return;
        }

        SyncFactoryManager syncManager = new SyncFactoryManager();
        GcmHelper.setRegId(mContext, regId);
        syncManager.syncProcess(mContext, SyncConstants.SYNC_GCM_ID);
        Log.d(TAG, "tut: updated Id");
    }

    private String getRegistrationId() {
        String regId = "";
        try {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);
            regId = gcm.register(GcmConstants.SENDER_ID);
        } catch (IOException e) {
            Log.d(TAG, "tut: " + e.getMessage());
        }

        return regId;
    }
}
