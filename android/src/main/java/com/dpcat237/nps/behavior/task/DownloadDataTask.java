package com.dpcat237.nps.behavior.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.factory.ApiFactoryManager;
import com.dpcat237.nps.behavior.service.DownloadSongsService;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.database.repository.ItemRepository;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.database.repository.SharedRepository;
import com.dpcat237.nps.database.repository.SongRepository;
import com.dpcat237.nps.helper.ApiRequestHelper;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.model.Label;
import com.dpcat237.nps.ui.activity.MainActivity;

import org.json.JSONArray;

import java.util.ArrayList;
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
    private Integer itemsSyncLimit = 300;

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
	
	private void syncFeeds() {
        Map<String, Object> result = ApiRequestHelper.feedsSyncRequest(mContext, apiFactoryManager, feedRepo);
		Feed[] feeds = (Feed[]) result.get("feeds");
		Boolean error = (Boolean) result.get("error");

        if (feeds == null || error) {
            updateProgress(20);

            return;
        }

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
                lastUpdate = feed.getLastUpdate();
            }

            count++;
            updateProgressIteration(10, 10, total, count);
        }

        if (lastUpdate != 0 && total > 0) {
            PreferencesHelper.setLastFeedsUpdate(mContext, lastUpdate);
        }
		if (progressStatus < 20) {
			updateProgress(20);
		}
	}
	
	private void syncItems() {
        if (!checkSyncItems(itemRepo)) {
            itemRepo.close();

            return;
        }

        JSONArray viewedItems = itemRepo.getItemsToSync();
        Map<String, Object> result = ApiRequestHelper.itemsSyncRequest(mContext, apiFactoryManager, viewedItems, itemsSyncLimit);
        Item[] items = (Item[]) result.get("items");
        Boolean error = (Boolean) result.get("error");
        if (error || items.length < 1) {
            itemRepo.close();

            return;
        }

        SongRepository songRepo = new SongRepository(mContext);
        songRepo.open();
        updateProgress(50);
        Integer count = 0;
        Integer total = items.length;
        for (Item item : items) {
            if (item.isUnread()) {
                itemRepo.addItem(item);
            } else {
                itemRepo.deleteItem(item.getApiId());
                songRepo.markAsPlayed(item.getItemApiId(), SongConstants.GRABBER_TYPE_DICTATE_ITEM, true);
            }
            count++;
            updateProgressIteration(50, 20, total, count);
        }
        songRepo.close();

        if (progressStatus < 70) {
            updateProgress(70);
        }
        if (viewedItems.length() > 0) {
            itemRepo.removeReadItems();
        }
	}

    private Boolean checkSyncItems(ItemRepository itemRepo) {
        Boolean sync = false;
        Integer unreadCount = itemRepo.countUnreadItems();
        if (unreadCount < itemsSyncLimit) {
            sync = true;
            itemsSyncLimit = ((itemsSyncLimit - unreadCount) < 10)? 10 : (itemsSyncLimit - unreadCount);
        }

        return sync;
    }
	
	@SuppressWarnings("unchecked")
	private void syncLabels() {
        Map<String, Object> resultChanged = labelRepo.getLabelsToSync();
        ArrayList<Label> changedLabelsArray = (ArrayList<Label>) resultChanged.get("labelsArray");
        Map<String, Object> result = ApiRequestHelper.labelsSyncRequest(mContext, apiFactoryManager, resultChanged);
		Label[] labels = (Label[]) result.get("labels");
        Boolean error = (Boolean) result.get("error");
		updateProgress(75);

        if (error) {
            updateProgress(80);

            return;
        }

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
		updateProgress(80);
	}
	
	private void syncLaterItems() {
		JSONArray selectedItems = labelRepo.getSelectedItemsToSync();
		updateProgress(90);
        if (selectedItems.length() < 1) {
            updateProgress(95);

            return;
        }

        Map<String, Object> result = ApiRequestHelper.labelsSyncRequest(mContext, apiFactoryManager, selectedItems);
        Boolean error = (Boolean) result.get("error");

        if (!error) {
            labelRepo.removeLaterItems();
        }

		updateProgress(95);
	}
	
	private void syncSharedItems() {
		JSONArray sharedItems = sharedRepo.getSharedToSync();

        if (sharedItems.length() < 1) {
            updateProgress(100);

            return;
        }

        Map<String, Object> result = ApiRequestHelper.sharedItemsSyncRequest(mContext, apiFactoryManager, sharedItems);
        Boolean error = (Boolean) result.get("error");
        if (!error) {
            sharedRepo.removeSharedItems();
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
