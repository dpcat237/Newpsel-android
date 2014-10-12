package com.dpcat237.nps.behavior.task;

import android.content.Context;
import android.os.AsyncTask;

import com.dpcat237.nps.behavior.factory.ApiFactoryManager;
import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.database.repository.SharedRepository;
import com.dpcat237.nps.helper.PreferencesHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendSharedTask extends AsyncTask<Void, Integer, Void>{
	private Context mContext;
	private SharedRepository sharedRepo;

	public SendSharedTask(Context context) {
        mContext = context;
    }

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

        sharedRepo = new SharedRepository(mContext);
        sharedRepo.open();
	}

	@Override
	protected Void doInBackground(Void... params) {
        syncSharedItems();

		return null;
	}

    protected void syncSharedItems() {
        ApiFactoryManager apiFactoryManager = new ApiFactoryManager();
        JSONArray sharedItems = sharedRepo.getSharedToSync();
        Map<String, Object> result = new HashMap<String, Object>();
        JSONObject jsonData = new JSONObject();

        if (sharedItems.length() > 0) {
            try {
                jsonData.put("appKey", PreferencesHelper.generateKey(mContext));
                jsonData.put("sharedItems", sharedItems);
                result = apiFactoryManager.makeRequest(ApiConstants.URL_SYNC_SHARED_ITEMS, jsonData);
            } catch (JSONException e) {
                result.put("error", true);
            }
            Boolean error = (Boolean) result.get("error");

            if (!error) {
                sharedRepo.removeSharedItems();
            }
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        sharedRepo.close();
    }
}
