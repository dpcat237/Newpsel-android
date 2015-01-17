package com.dpcat237.nps.behavior.task;

import android.content.Context;
import android.os.AsyncTask;

import com.dpcat237.nps.behavior.manager.GcmManager;
import com.dpcat237.nps.helper.GcmHelper;

public class GcmIdUpdateTask extends AsyncTask<Void, Integer, Void> {
    private static final String TAG = "NPS:GcmIdUpdateTask";
	private Context mContext;
    private GcmManager gcmManager;


	public GcmIdUpdateTask(Context context) {
        mContext = context;
    }
    
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
        gcmManager = new GcmManager(mContext);
	}
     
	@Override
	protected Void doInBackground(Void... params) {
        String regId = GcmHelper.getRegId(mContext);
        if (!regId.isEmpty()) {
            return null;
        }
        gcmManager.updateId();

		return null;
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
