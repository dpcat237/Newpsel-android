package com.dpcat237.nps.task;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dpcat237.nps.MainActivity;
import com.dpcat237.nps.R;
import com.dpcat237.nps.helper.ApiHelper;
import com.dpcat237.nps.helper.GenericHelper;
import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.repository.FeedRepository;
import com.dpcat237.nps.repository.ItemRepository;

public class DownloadDataTask extends AsyncTask<Void, Integer, Void>{
	ApiHelper api;
	int progressStatus;
	private Context mContext;
	private View mView;
	ListView listView;
	ProgressBar progressBar;
	FeedRepository feedRepo;
	ItemRepository itemRepo;
	String msg = "not";
	
	public DownloadDataTask(Context context, View view, ListView list) {
		api = new ApiHelper();
        mContext = context;
        mView = view;
        listView = list;
        feedRepo = new FeedRepository(mContext);
        feedRepo.open();
        itemRepo = new ItemRepository(mContext);
        itemRepo.open();
        progressBar = (ProgressBar) mView.findViewById(R.id.progress);
    } 
    
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressStatus = 0;
		progressBar.setVisibility(View.VISIBLE);
		progressBar.setMax(100);
	}
     
	@Override
	protected Void doInBackground(Void... params) {
		syncFeeds();
		syncItems();
		feedRepo.unreadCountUpdate();

		return null;
	}
	
	private void syncFeeds () {
		Feed[] feeds = api.getFeeds(GenericHelper.generateKey(mContext), GenericHelper.getLastFeedsUpdate(mContext));
		
		if (feeds != null) {
			updateProgress(10); //TODO: count download progress
			Integer lastUpdate = 0;
			Integer count = 0;
			Integer total = feeds.length;
			
			for (Feed feed : feeds) {
				feedRepo.addFeed(feed);
				lastUpdate = (int) feed.getLastUpdate();
				
				count++;
				updateProgressIteration(10, 10, total, count);
		    }
			if (lastUpdate != 0) {
				GenericHelper.setLastFeedsUpdate(mContext, lastUpdate);
			}
		}
		
		if (progressStatus < 20) {
			updateProgress(20);
		}
	}
	
	private void syncItems () {
		JSONArray viewedItems = itemRepo.getItemsToSync();
		Item[] items = null;
		Boolean isDownload = true;
		
		items = api.getItems(GenericHelper.generateKey(mContext), viewedItems, isDownload);
		
		if (items != null) {
			updateProgress(50); //TODO: count download progress
			Integer count = 0;
			Integer total = items.length;
			
			
			for (Item item : items) {
				itemRepo.addItem(item);
				
				updateProgressIteration(50, 40, total, count);
		    }
		}
		
		if (progressStatus < 90) {
			updateProgress(90);
		}
		
		if (viewedItems.length() > 0) {
			itemRepo.removeReadItems();
		}
		updateProgress(100);
	}
 
	@Override
	protected void onProgressUpdate(Integer... values) {
	  super.onProgressUpdate(values);
	  progressBar.setProgress(values[0]);
   
	}
  
	@Override
 	protected void onPostExecute(Void result) {
	  super.onPostExecute(result);
	  progressBar.setVisibility(View.GONE);
	  publishProgress(0);
	  
	  ((MainActivity) mContext).reloadList();
	  Toast.makeText(mContext, R.string.sync_finished, Toast.LENGTH_SHORT).show();
	}
	
	private void updateProgress(Integer progress) {
		progressStatus = progress;
		publishProgress(progressStatus);
	}
	
	@SuppressLint("UseValueOf")
	private void updateProgressIteration(Integer previous, Integer stepTotal, Integer total, Integer count) {
		Float result = new Float(new Float(new Float(count*100) / new Float(total)) * stepTotal) / 100;
		Integer progress = (int) Math.round(result)+previous;
		publishProgress(progress);
	}
}
