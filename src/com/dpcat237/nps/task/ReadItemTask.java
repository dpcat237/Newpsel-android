package com.dpcat237.nps.task;

import android.content.Context;
import android.os.AsyncTask;

import com.dpcat237.nps.repository.ItemRepository;

public class ReadItemTask extends AsyncTask<Void, Integer, Void>{
	private Context mContext;
	private Long itemId;
	ItemRepository itemRepo;
	Boolean isUnread;
	
	public ReadItemTask(Context context, Long itemIntentId, Boolean unread) {
        mContext = context;
        itemId = itemIntentId;
        isUnread = unread;
        itemRepo = new ItemRepository(mContext);
        itemRepo.open();
    }
    
	@Override
	protected Void doInBackground(Void... params) {
		itemRepo.readItem(itemId, isUnread);
		
		return null;
	}
}
