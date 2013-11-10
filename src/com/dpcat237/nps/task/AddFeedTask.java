package com.dpcat237.nps.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.helper.ApiHelper;
import com.dpcat237.nps.helper.GenericHelper;
import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.repository.FeedRepository;
import com.dpcat237.nps.repository.ItemRepository;

import java.util.Map;

public class AddFeedTask extends AsyncTask<Void, Integer, Void>{
	ApiHelper api;
	int progress_status;
	private Context mContext;
	private View mView;
	FeedRepository feedRepo;
	ItemRepository itemRepo;
	String url;
	String appKey;
	Boolean checkApi = false;
	ProgressDialog dialog;
	
	public AddFeedTask(Context context, View view) {
		api = new ApiHelper();
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
		appKey = GenericHelper.generateKey(mContext);
	}
    
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = ProgressDialog.show(mContext, "Loading", "Please wait...", true);
	}
     
	@Override
	protected Void doInBackground(Void... params) {
		Item[] items = null;
		items = ApiHelper.addFeed(appKey, url);
		
		if (items != null) {
			syncFeeds();
			
			for (Item item : items) {
				itemRepo.addItem(item);
		    }
			
			feedRepo.unreadCountUpdate();
			checkApi = true;
		}
		
		return null;
	}
	
	private void syncFeeds () {
		Map<String, Object> result = null;
		Boolean error = false;
		result = api.getFeeds(GenericHelper.generateKey(mContext), GenericHelper.getLastFeedsUpdate(mContext));
		Feed[] feeds = (Feed[]) result.get("feeds");
		error = (Boolean) result.get("error");
		
		if (feeds != null && !error) {
			Integer lastUpdate = 0;
			
			for (Feed feed : feeds) {
				feedRepo.addFeed(feed);
				lastUpdate = (int) feed.getLastUpdate();
		    }
			if (lastUpdate != 0) {
				GenericHelper.setLastFeedsUpdate(mContext, lastUpdate);
			}
		
		}
	}
 
	@Override
	protected void onProgressUpdate(Integer... values) {
	  super.onProgressUpdate(values);
	}
  
	@Override
 	protected void onPostExecute(Void result) {
		dialog.cancel();
		
		if (checkApi) {
			((Activity) mContext).finish();	
		} else {
			Toast.makeText(mContext, R.string.error_306, Toast.LENGTH_SHORT).show();
		}
	}
}
