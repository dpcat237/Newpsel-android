package com.dpcat237.nps.behavior.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.factory.ApiFactoryManager;
import com.dpcat237.nps.behavior.service.SyncNewsService;
import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.constant.PreferenceConstants;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.database.repository.ItemRepository;
import com.dpcat237.nps.helper.PreferencesHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddFeedTask extends AsyncTask<Void, Integer, Void>{
	private Context mContext;
	private View mView;
    private FeedRepository feedRepo;
    private ItemRepository itemRepo;
    private String url;
    private String appKey;
    private Boolean checkApi = false;
    private ProgressDialog dialog;
    private ApiFactoryManager apiFactoryManager;

	public AddFeedTask(Context context, View view) {
        mContext = context;
        mView = view;

        feedRepo = new FeedRepository(mContext);
        feedRepo.open();
        itemRepo = new ItemRepository(mContext);
        itemRepo.open();
        getData();
    }

	private void getData() {
		EditText urlText = (EditText) mView.findViewById(R.id.txtUrl);
		url = urlText.getText().toString();
		appKey = PreferencesHelper.generateKey(mContext);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = ProgressDialog.show(mContext, "Loading", "Please wait...", true);
        apiFactoryManager = new ApiFactoryManager();
	}

	@Override
	protected Void doInBackground(Void... params) {
        JSONObject jsonData = new JSONObject();
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            jsonData.put(ApiConstants.DEVICE_ID, appKey);
            jsonData.put("feed_url", url);
            result = apiFactoryManager.makeRequest(ApiConstants.URL_ADD_FEED, jsonData);
        } catch (JSONException e) {
            result.put("error", true);
        }

        Boolean error = (Boolean) result.get("error");
        if (!error) {
            PreferencesHelper.setBooleanPreference(mContext, PreferenceConstants.FEEDS_SYNC_REQUIRED, true);
            Intent syncNews = new Intent(mContext, SyncNewsService.class);
            mContext.startService(syncNews);
            checkApi = true;
        }

		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
	  super.onProgressUpdate(values);
	}

	@Override
 	protected void onPostExecute(Void result) {
		dialog.cancel();
        feedRepo.close();
        itemRepo.close();

		if (checkApi) {
			((Activity) mContext).finish();
		} else {
			Toast.makeText(mContext, R.string.error_306, Toast.LENGTH_SHORT).show();
		}
	}
}
