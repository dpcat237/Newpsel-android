package com.dpcat237.nps.behavior.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.dpcat237.nps.behavior.valueObject.PlayerServiceStatus;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.database.repository.ItemRepository;
import com.dpcat237.nps.database.repository.SongRepository;
import com.dpcat237.nps.ui.activity.MainActivity;

public class ReadFeedItemsTask extends AsyncTask<Void, Integer, Void>{
	private Context mContext;
	private Integer feedId;
    private ItemRepository itemRepo;
    private SongRepository songRepo;
    private PlayerServiceStatus playerStatus;


	public ReadFeedItemsTask(Context context, Integer feedInternId) {
        mContext = context;
        feedId = feedInternId;
        itemRepo = new ItemRepository(mContext);
        itemRepo.open();
        songRepo = new SongRepository(mContext);
        songRepo.open();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        playerStatus = PlayerServiceStatus.getInstance();
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

        if (playerStatus.hasActiveSong()) {
            Intent intent = new Intent(mContext, MainActivity.class);
            mContext.startActivity(intent);
        }
        ((Activity) mContext).finish();
	}
}
