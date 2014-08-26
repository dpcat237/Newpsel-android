package com.dpcat237.nps.behavior.task;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.factory.SyncFactory;
import com.dpcat237.nps.behavior.factory.SyncFactoryManager;
import com.dpcat237.nps.behavior.factory.syncManager.SyncManager;
import com.dpcat237.nps.behavior.service.DownloadSongsService;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.constant.SyncConstants;
import com.dpcat237.nps.database.repository.ItemRepository;
import com.dpcat237.nps.database.repository.SongRepository;
import com.dpcat237.nps.common.model.Item;
import com.dpcat237.nps.ui.activity.MainActivity;

import java.util.Map;

public class SyncNewsTask extends AsyncTask<Void, Integer, Void> {
    private static final String TAG = "NPS:DownloadDataTask";
    private int progressStatus;
	private Context mContext;
    private ProgressBar progressBar;
    private SyncFactoryManager syncManager;

	public SyncNewsTask(Context context, View view) {
        mContext = context;
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
    }
    
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressStatus = 0;
		progressBar.setVisibility(View.VISIBLE);
		progressBar.setMax(100);

        syncManager = new SyncFactoryManager();
	}
     
	@Override
	protected Void doInBackground(Void... params) {
        syncFeeds();       //progressStatus = 0  -> 10; 10 -> 20
        syncItems();       //progressStatus = 20 -> 50; 50 -> 70
		syncLabels();      //progressStatus = 70 -> 75; 75 -> 80
        syncLabelItems();  //progressStatus = 80 -> 90; 90 -> 95
		syncSharedItems(); //progressStatus = 95 -> 100*/

		return null;
	}

    private void forceProgress(Integer must) {
        if (progressStatus < must) {
            updateProgress(must);
        }
    }
	
	private void syncFeeds() {
        SyncManager syncFeedsManager = SyncFactory.createManager(SyncConstants.SYNC_FEEDS);
        syncFeedsManager.setup(mContext);
        if (syncFeedsManager.areError()) {
            syncFeedsManager.finish();
            forceProgress(20);

            return;
        }

        syncFeedsManager.makeRequest();
        if (syncFeedsManager.areError()) {
            syncFeedsManager.finish();
            forceProgress(20);

            return;
        }

        updateProgress(10);
        syncFeedsManager.processData();
        syncFeedsManager.finish();
        updateProgress(20);
	}
	
	private void syncItems() {
        SyncManager syncItemsManager = SyncFactory.createManager(SyncConstants.SYNC_ITEMS);
        syncItemsManager.setup(mContext);
        if (syncItemsManager.areError()) {
            syncItemsManager.finish();
            forceProgress(70);

            return;
        }

        syncItemsManager.makeRequest();
        if (syncItemsManager.areError()) {
            syncItemsManager.finish();
            forceProgress(70);

            return;
        }

        Map<String, Object> result = syncItemsManager.getDownloaded();
        Item[] items = (Item[]) result.get("items");
        if (items.length < 1) {
            syncItemsManager.finish();
            forceProgress(70);

            return;
        }

        ItemRepository itemRepo = new ItemRepository(mContext);
        itemRepo.open();
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
                songRepo.markAsPlayed(item.getItemApiId(), SongConstants.GRABBER_TYPE_TITLE, true);
            }
            count++;
            updateProgressIteration(50, 20, total, count);
        }
        itemRepo.close();
        songRepo.close();

        forceProgress(70);
        syncItemsManager.finish();
	}

	private void syncLabels() {
        updateProgress(75);
        syncManager.syncProcess(mContext, SyncConstants.SYNC_LABELS);
        updateProgress(80);
	}
	
	private void syncLabelItems() {
        updateProgress(90);
        syncManager.syncProcess(mContext, SyncConstants.SYNC_LABEL_ITEMS);
		updateProgress(95);
	}
	
	private void syncSharedItems() {
        syncManager.syncProcess(mContext, SyncConstants.SYNC_SHARED_ITEMS);
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
