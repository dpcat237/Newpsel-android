package com.dpcat237.nps.behavior.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.factory.ApiFactoryManager;
import com.dpcat237.nps.behavior.service.DownloadSongsService;
import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.database.repository.ItemRepository;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.database.repository.SharedRepository;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.model.Label;
import com.dpcat237.nps.ui.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DownloadDataTask extends AsyncTask<Void, Integer, Void> {
    private static final String TAG = "NPS:DownloadDataTask";
    private int progressStatus;
	private Context mContext;
    private ProgressBar progressBar;
    private FeedRepository feedRepo;
    private ItemRepository itemRepo;
    private LabelRepository labelRepo;
    private SharedRepository sharedRepo;
    private SharedPreferences pref;
    private ApiFactoryManager apiFactoryManager;

	public DownloadDataTask(Context context, View view) {
        mContext = context;
        openDBs();
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
    }
    
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressStatus = 0;
		progressBar.setVisibility(View.VISIBLE);
		progressBar.setMax(100);

        apiFactoryManager = new ApiFactoryManager();
	}
     
	@Override
	protected Void doInBackground(Void... params) {
        syncFeeds();          //progressStatus = 0  -> 10; 10 -> 20

        Boolean itemsActivated = pref.getBoolean("pref_items_download_enable", true);
        if (itemsActivated) {
            syncItems();      //progressStatus = 20 -> 50; 50 -> 70
            feedRepo.unreadCountUpdate();
        }

		syncLabels();         //progressStatus = 70 -> 75; 75 -> 80
		syncLaterItems();     //progressStatus = 80 -> 90; 90 -> 95
		syncSharedItems();    //progressStatus = 95 -> 100*/

		return null;
	}
	
	private void syncFeeds () {
        JSONObject jsonData = new JSONObject();
        Map<String, Object> result = new HashMap<String, Object>();
        ArrayList<Feed> feedsNow = feedRepo.getAllFeeds();
        Integer feedsUpdate = 0;
        if (feedsNow.size() > 0) {
             feedsUpdate = PreferencesHelper.getLastFeedsUpdate(mContext);
        }

        try {
            jsonData.put("appKey", PreferencesHelper.generateKey(mContext));
            jsonData.put("lastUpdate", feedsUpdate);
            result = apiFactoryManager.makeRequest(ApiConstants.URL_GET_FEEDS, jsonData);
        } catch (JSONException e) {
            result.put("error", true);
        }
		Feed[] feeds = (Feed[]) result.get("feeds");
		Boolean error = (Boolean) result.get("error");

		if (feeds != null && !error) {
			updateProgress(10);
			Integer lastUpdate = 0;
			Integer count = 0;
			Integer total = feeds.length;
			
			for (Feed feed : feeds) {
				if (feedRepo.checkFeedExists(feed.getApiId())) {
					feedRepo.updateFeed(feed);
				} else {
					feedRepo.addFeed(feed);
				}
				
				if (feed.getLastUpdate() > lastUpdate) {
					lastUpdate = (int) feed.getLastUpdate();
				}

				count++;
				updateProgressIteration(10, 10, total, count);
		    }
			if (lastUpdate != 0 && total > 0) {
				PreferencesHelper.setLastFeedsUpdate(mContext, lastUpdate);
			}
		}
		
		if (progressStatus < 20) {
			updateProgress(20);
		}
	}
	
	private void syncItems () {
		JSONArray viewedItems = itemRepo.getItemsToSync();
        JSONObject jsonData = new JSONObject();
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            jsonData.put("appKey", PreferencesHelper.generateKey(mContext));
            jsonData.put("viewedItems", viewedItems);
            jsonData.put("isDownload", true);
            result = apiFactoryManager.makeRequest(ApiConstants.URL_SYNC_ITEMS_UNREAD, jsonData);
        } catch (JSONException e) {
            result.put("error", true);
        }
		Item[] items = (Item[]) result.get("items");
        Boolean error = (Boolean) result.get("error");
        Log.d(TAG, "tut: items "+items.length);

		if (items != null) {
			updateProgress(50);
			Integer count = 0;
			Integer total = items.length;
			
			for (Item item : items) {
				itemRepo.addItem(item);
				updateProgressIteration(50, 20, total, count);
		    }
            PreferencesHelper.setNewItems(mContext, true);
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
		Map<String, Object> resultChanged = labelRepo.getLabelsToSync();
		JSONArray changedLabels = (JSONArray) resultChanged.get("labelsJson");
		ArrayList<Label> changedLabelsArray = (ArrayList<Label>) resultChanged.get("labelsArray");
        JSONObject jsonData = new JSONObject();
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            jsonData.put("appKey", PreferencesHelper.generateKey(mContext));
            jsonData.put("changedLabels", changedLabels);
            jsonData.put("lastUpdate", PreferencesHelper.getLastLabelsUpdate(mContext));
            result = apiFactoryManager.makeRequest(ApiConstants.URL_SYNC_LABELS, jsonData);
        } catch (JSONException e) {
            result.put("error", true);
        }
		Label[] labels = (Label[]) result.get("labels");
        Boolean error = (Boolean) result.get("error");
		updateProgress(75);
		
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
					lastUpdate = label.getLastUpdate();
			    }
			}
			
			if (changedLabelsArray.size() > 0) {
				for (Label changedLabel : changedLabelsArray) {
					labelRepo.setChanged(changedLabel.getId(), false);
			    }
			}
			
			if (lastUpdate != 0) {
				PreferencesHelper.setLastLabelsUpdate(mContext, lastUpdate);
			}
		}
		updateProgress(80);
	}
	
	private void syncLaterItems() {
		JSONArray selectedItems = labelRepo.getSelectedItemsToSync();
		updateProgress(90);
        JSONObject jsonData = new JSONObject();
        Map<String, Object> result = new HashMap<String, Object>();

		if (selectedItems.length() > 0) {
            try {
                jsonData.put("appKey", PreferencesHelper.generateKey(mContext));
                jsonData.put("laterItems", selectedItems);
                result = apiFactoryManager.makeRequest(ApiConstants.URL_SYNC_LATER_ITEMS, jsonData);
            } catch (JSONException e) {
                result.put("error", true);
            }
            Boolean error = (Boolean) result.get("error");

			if (!error) {
				labelRepo.removeLaterItems();
			}
		}
		
		updateProgress(95);
	}
	
	private void syncSharedItems() {
		JSONArray sharedItems = sharedRepo.getSharedToSync();
        JSONObject jsonData = new JSONObject();
        Map<String, Object> result = new HashMap<String, Object>();
		
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
		
		updateProgress(100);
	}

    private void openDBs() {
        feedRepo = new FeedRepository(mContext);
        feedRepo.open();
        itemRepo = new ItemRepository(mContext);
        itemRepo.open();
        labelRepo = new LabelRepository(mContext);
        labelRepo.open();
        sharedRepo = new SharedRepository(mContext);
        sharedRepo.open();
    }

    private void closeDBs() {
        feedRepo.close();
        itemRepo.close();
        labelRepo.close();
        sharedRepo.close();
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
        closeDBs();

        if (((MainActivity) mContext).isInFront) {
          ((MainActivity) mContext).reloadList();

          Toast.makeText(mContext, R.string.sync_finished, Toast.LENGTH_SHORT).show();
        }

        Intent mServiceIntent = new Intent((mContext), DownloadSongsService.class);
        (mContext).startService(mServiceIntent);
	}
	
	private void updateProgress(Integer progress) {
		progressStatus = progress;
		publishProgress(progressStatus);
	}
	
	@SuppressLint("UseValueOf")
	private void updateProgressIteration(Integer previous, Integer stepTotal, Integer total, Integer count) {
		Float result = new Float(new Float(new Float(count*100) / new Float(total)) * stepTotal) / 100;
		Integer progress = Math.round(result)+previous;
		publishProgress(progress);
	}
}
