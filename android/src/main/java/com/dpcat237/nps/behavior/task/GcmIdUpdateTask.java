package com.dpcat237.nps.behavior.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dpcat237.nps.behavior.factory.SyncFactoryManager;
import com.dpcat237.nps.constant.SyncConstants;
import com.dpcat237.nps.helper.GcmHelper;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class GcmIdUpdateTask extends AsyncTask<Void, Integer, Void> {
    private static final String TAG = "NPS:GcmIdUpdateTask";
	private Context mContext;
    private static final String SENDER_ID = "8108991373";

	public GcmIdUpdateTask(Context context) {
        mContext = context;
    }
    
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
     
	@Override
	protected Void doInBackground(Void... params) {
        String regId = GcmHelper.getRegId(mContext);
        if (!regId.isEmpty()) {
            return null;
        }

        regId = getRegistrationId();
        if (regId.isEmpty()) {
            return null;
        }

        SyncFactoryManager syncManager = new SyncFactoryManager();
        GcmHelper.setRegId(mContext, regId);
        syncManager.syncProcess(mContext, SyncConstants.SYNC_GCM_ID);

		return null;
	}

    private String getRegistrationId() {
        String regId = "";
        try {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);
            regId = gcm.register(SENDER_ID);
        } catch (IOException e) {
            Log.d(TAG, "tut: "+e.getMessage());
        }

        return regId;
    }

	@Override
	protected void onProgressUpdate(Integer... values) {
	  super.onProgressUpdate(values);
	}

	@Override
 	protected void onPostExecute(Void result) {
        super.onPostExecute(result);
	}
}
