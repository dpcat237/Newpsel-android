package com.dpcat237.nps.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.dpcat237.nps.MainActivity;
import com.dpcat237.nps.repository.ItemRepository;

public class ReadFeedItemsTask extends AsyncTask<Void, Integer, Void>{
	private Context mContext;
	private Integer feedId;
	ItemRepository itemRepo;
	Class<MainActivity> main;
	
	public ReadFeedItemsTask(Context context, Class<MainActivity> mainActivity, Integer feedInternId) {
        mContext = context;
        main = mainActivity;
        feedId = feedInternId;
        itemRepo = new ItemRepository(mContext);
        itemRepo.open();
    }
    
	@Override
	protected Void doInBackground(Void... params) {
		itemRepo.readFeedItems(feedId);
		
		return null;
	}
	
	@Override
 	protected void onPostExecute(Void result) {
		Intent intent = new Intent(mContext, main);
		mContext.startActivity(intent);
		((Activity) mContext).finish();
	}
}
