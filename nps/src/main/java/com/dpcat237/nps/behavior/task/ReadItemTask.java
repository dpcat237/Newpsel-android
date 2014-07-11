package com.dpcat237.nps.behavior.task;

import android.content.Context;
import android.os.AsyncTask;

import com.dpcat237.nps.database.repository.ItemRepository;

public class ReadItemTask extends AsyncTask<Void, Integer, Void>{
	private Context mContext;
	private Integer itemApiId;
	ItemRepository itemRepo;
	Boolean isUnread;
	
	public ReadItemTask(Context context, Integer itemApiId, Boolean unread) {
        this.mContext = context;
        this.itemApiId = itemApiId;
        this.isUnread = unread;
        this.itemRepo = new ItemRepository(mContext);
        itemRepo.open();
    }
    
	@Override
	protected Void doInBackground(Void... params) {
		itemRepo.readItem(itemApiId, isUnread);
		
		return null;
	}

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        itemRepo.close();
    }
}
