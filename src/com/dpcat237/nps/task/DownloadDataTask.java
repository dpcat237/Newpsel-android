package com.dpcat237.nps.task;

import java.util.ArrayList;
import java.util.Map;

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
import com.dpcat237.nps.model.Label;
import com.dpcat237.nps.repository.FeedRepository;
import com.dpcat237.nps.repository.ItemRepository;
import com.dpcat237.nps.repository.LabelRepository;
import com.dpcat237.nps.repository.SharedRepository;

public class DownloadDataTask extends AsyncTask<Void, Integer, Void>{
	ApiHelper api;
	int progressStatus;
	private Context mContext;
	private View mView;
	ListView listView;
	ProgressBar progressBar;
	FeedRepository feedRepo;
	ItemRepository itemRepo;
	LabelRepository labelRepo;
	SharedRepository sharedRepo;
	
	public DownloadDataTask(Context context, View view, ListView list) {
		api = new ApiHelper();
        mContext = context;
        mView = view;
        listView = list;
        feedRepo = new FeedRepository(mContext);
        feedRepo.open();
        itemRepo = new ItemRepository(mContext);
        itemRepo.open();
        labelRepo = new LabelRepository(mContext);
        labelRepo.open();
        sharedRepo = new SharedRepository(mContext);
        sharedRepo.open();
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
		syncFeeds();          //progressStatus = 0  -> 10; 10 -> 20
		syncItems();          //progressStatus = 20 -> 50; 50 -> 70
		feedRepo.unreadCountUpdate();
		syncLabels();         //progressStatus = 70 -> 75; 75 -> 80
		syncLaterItems();     //progressStatus = 80 -> 90; 90 -> 95
		syncSharedItems();    //progressStatus = 95 -> 100

		return null;
	}
	
	private void syncFeeds () {
		Map<String, Object> result = null;
		Boolean error = false;
		result = api.getFeeds(GenericHelper.generateKey(mContext), GenericHelper.getLastFeedsUpdate(mContext));
		Feed[] feeds = (Feed[]) result.get("feeds");
		error = (Boolean) result.get("error");
		
		if (feeds != null && !error) {
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
			if (lastUpdate != 0 && total > 0) {
				GenericHelper.setLastFeedsUpdate(mContext, lastUpdate);
			}
		}
		
		if (progressStatus < 20) {
			updateProgress(20);
		}
	}
	
	private void syncItems () {
		JSONArray viewedItems = itemRepo.getItemsToSync();
		Map<String, Object> result = null;
		Boolean isDownload = true;
		Boolean error = false;
		
		result = api.getItems(GenericHelper.generateKey(mContext), viewedItems, isDownload);
		Item[] items = (Item[]) result.get("items");
		error = (Boolean) result.get("error");
		
		if (items != null) {
			updateProgress(50); //TODO: count download progress
			Integer count = 0;
			Integer total = items.length;
			
			for (Item item : items) {
				itemRepo.addItem(item);
				updateProgressIteration(50, 20, total, count);
		    }
		}
		
		if (progressStatus < 70) {
			updateProgress(70);
		}
		
		if (viewedItems.length() > 0 && !error) {
			itemRepo.removeReadItems();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void syncLabels() {
		Map<String, Object> resultChanged = (Map<String, Object>) labelRepo.getLabelsToSync();
		JSONArray changedLabels = (JSONArray) resultChanged.get("labelsJson");
		ArrayList<Label> changedLabelsArray = (ArrayList<Label>) resultChanged.get("labelsArray");
		Map<String, Object> result = null;
		Boolean error = false;
		
		result = api.syncLabels(GenericHelper.generateKey(mContext), changedLabels, GenericHelper.getLastLabelsUpdate(mContext));
		Label[] labels = (Label[]) result.get("labels");
		error = (Boolean) result.get("error");
		updateProgress(75); //TODO: count download progress
		
		if (!error) {
			Integer lastUpdate = 0;
			
			if (labels != null) {
				for (Label label : labels) {
					if (label.getId() > 0) {
						labelRepo.setApiId(label.getId(), label.getApiId());
						labelRepo.setApiId(label.getId(), label.getApiId());
					} else {
						labelRepo.addLabel(label, false);
					}
					lastUpdate = (int) label.getLastUpdate();
			    }
			}
			
			if (changedLabelsArray.size() > 0) {
				for (Label changedLabel : changedLabelsArray) {
					labelRepo.setChanged(changedLabel.getId(), false);
			    }
			}
			
			if (lastUpdate != 0) {
				GenericHelper.setLastLabelsUpdate(mContext, lastUpdate);
			}
		}
		updateProgress(80);
	}
	
	private void syncLaterItems() {
		JSONArray selectedItems = labelRepo.getSelectedItemsToSync();
		updateProgress(90);
		Map<String, Object> result = null;
		Boolean error = false;
		
		if (selectedItems.length() > 0) {
			result = api.syncLaterItems(GenericHelper.generateKey(mContext), selectedItems);
			error = (Boolean) result.get("error");
			
			if (!error) {
				labelRepo.removeLaterItems();
			}
		}
		
		updateProgress(95);
	}
	
	private void syncSharedItems() {
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
	  feedRepo.close();
	  itemRepo.close();
	  labelRepo.close();
	  
	  if (((MainActivity) mContext).isInFront) {
		  ((MainActivity) mContext).reloadList();
		  
		  Toast.makeText(mContext, R.string.sync_finished, Toast.LENGTH_SHORT).show();
	  }
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
