package com.dpcat237.nps.behavior.task;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.dpcat237.nps.ui.activity.MainActivity;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.database.repository.ItemRepository;
import com.dpcat237.nps.database.repository.SongRepository;

public class ReadFeedItemsTask extends AsyncTask<Void, Integer, Void>{
	private Context mContext;
	private Integer feedId;
    private ItemRepository itemRepo;
    private SongRepository songRepo;

	public ReadFeedItemsTask(Context context, Class<MainActivity> mainActivity, Integer feedInternId) {
        mContext = context;
        feedId = feedInternId;
        itemRepo = new ItemRepository(mContext);
        itemRepo.open();
        songRepo = new SongRepository(mContext);
        songRepo.open();
    }
    
	@Override
	protected Void doInBackground(Void... params) {
		itemRepo.readFeedItems(feedId);
        songRepo.markAsPlayedSongs(feedId, SongConstants.GRABBER_TYPE_TITLE);
		
		return null;
	}
	
	@Override
 	protected void onPostExecute(Void result) {
        itemRepo.close();
        songRepo.close();

		((Activity) mContext).finish();
	}
}
