package com.dpcat237.nps.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dpcat237.nps.helper.ApiHelper;
import com.dpcat237.nps.helper.GenericHelper;
import com.dpcat237.nps.model.Shared;
import com.dpcat237.nps.repository.SharedRepository;

import org.json.JSONArray;

import java.util.Map;

public class SendSharedTask extends AsyncTask<Void, Integer, Void>{
	private Context mContext;
	private SharedRepository sharedRepo;
    ApiHelper api;

	public SendSharedTask(Context context) {
        mContext = context;
    }

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
        api = new ApiHelper();

        sharedRepo = new SharedRepository(mContext);
        sharedRepo.open();
	}

	@Override
	protected Void doInBackground(Void... params) {
        syncSharedItems();

		return null;
	}

    protected void syncSharedItems() {
        JSONArray sharedItems = sharedRepo.getSharedToSync();
        Map<String, Object> result = null;
        Boolean error = false;

        if (sharedItems.length() > 0) {
            result = api.syncSharedItems(GenericHelper.generateKey(mContext), sharedItems);
            error = (Boolean) result.get("error");

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
