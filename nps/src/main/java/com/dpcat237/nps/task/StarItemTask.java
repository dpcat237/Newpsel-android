package com.dpcat237.nps.task;

import android.content.Context;
import android.os.AsyncTask;

import com.dpcat237.nps.repository.ItemRepository;

public class StarItemTask extends AsyncTask<Void, Integer, Void>{
	private Context mContext;
	private Long itemId;
	ItemRepository itemRepo;
	Boolean staredStatus;
	
	public StarItemTask(Context context, Long itemIntentId, Boolean stared) {
        mContext = context;
        itemId = itemIntentId;
        staredStatus = stared;
        itemRepo = new ItemRepository(mContext);
        itemRepo.open();
    }
    
	@Override
	protected Void doInBackground(Void... params) {
		itemRepo.startedChange(itemId, staredStatus);
		
		return null;
	}
}
