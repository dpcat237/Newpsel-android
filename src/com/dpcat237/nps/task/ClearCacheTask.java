package com.dpcat237.nps.task;

import android.content.Context;
import android.os.AsyncTask;
import android.webkit.WebView;

import com.dpcat237.nps.helper.GenericHelper;

public class ClearCacheTask extends AsyncTask<Void, Integer, Void>{
	private Context mContext;
	private WebView mView;
	
	public ClearCacheTask(Context context, WebView view) {
		mContext = context;
		mView = view;
    }
    
	@Override
	protected Void doInBackground(Void... params) {
		Boolean check = GenericHelper.checkLastClearCache(mContext);
		if (check) {
			mView.clearCache(true);
		}
		
		return null;
	}
}
