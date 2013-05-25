package com.dpcat237.nps.task;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.helper.ApiHelper;
import com.dpcat237.nps.helper.GenericHelper;
import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.repository.FeedRepository;
import com.dpcat237.nps.repository.ItemRepository;

public class DownloadDataTask extends AsyncTask<Void, Integer, Void>{
	ApiHelper api;
	int progress_status;
	private Context mContext;
	private View mView;
	ProgressBar progressBar;
	//TextView txt_percentage;
	FeedRepository feedRepo;
	ItemRepository itemRepo;
	String msg = "not";
	
	public DownloadDataTask(Context context, View view) {
		api = new ApiHelper();
        mContext = context;
        mView = view;
        feedRepo = new FeedRepository(mContext);
        feedRepo.open();
        itemRepo = new ItemRepository(mContext);
        itemRepo.open();
        progressBar = (ProgressBar) mView.findViewById(R.id.progress);
        //txt_percentage= (TextView) mView.findViewById(R.id.txt_percentage);
    } 
    
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progress_status = 0;
		progressBar.setVisibility(View.VISIBLE);
		//Toast.makeText(mContext, "Invoke onPreExecute()", Toast.LENGTH_SHORT).show();
		//txt_percentage.setText("downloading 0%");
	}
     
	@Override
	protected Void doInBackground(Void... params) {
		syncFeeds();
		syncItems();
		//Toast.makeText(mContext, "hhhmm", Toast.LENGTH_SHORT).show();
		
		/*while(progress_status<100){
			progress_status += 2;
			publishProgress(progress_status);
			SystemClock.sleep(100);
		}*/

		return null;
	}
	
	private void syncFeeds () {
		Feed[] feeds = api.getFeeds(GenericHelper.generateKey(mContext), GenericHelper.getLastFeedsUpdate(mContext));
		progressBar.setMax(feeds.length);
		Integer lastUpdate = 0;
		
		for (Feed feed : feeds) {
			feedRepo.addFeed(feed);
			lastUpdate = (int) feed.getLastUpdate();
			progress_status++;
			publishProgress(progress_status);
	    }
		if (lastUpdate != 0) {
			GenericHelper.setLastFeedsUpdate(mContext, lastUpdate);
		}
	}
	
	private void syncItems () {
		Integer viewedFeeds = 0;
		Item[] items = null;
		Boolean isDownload = true;
		
		//items = api.getItems(GenericHelper.generateKey(mContext), items, isDownload);
		items = api.getItems(GenericHelper.generateKey(mContext), viewedFeeds, isDownload);
		
		//Integer length = 0;
		//String check = "";
		for (Item item : items) {
			itemRepo.addItem(item);
			//length = (int) item.getFeedId();
			//check = item.getTitle();
	    }
		
		//msg = length.toString();
	}
 
	@Override
	protected void onProgressUpdate(Integer... values) {
	  super.onProgressUpdate(values);
	  //txt_percentage.setText("downloading " +values[0]+"%");
	  progressBar.setProgress(values[0]);
   
	}
  
	@Override
 	protected void onPostExecute(Void result) {
	  super.onPostExecute(result);
	  progressBar.setVisibility(View.GONE);
	  Toast.makeText(mContext, R.string.feeds_downloaded, Toast.LENGTH_SHORT).show();
	  
	  
	  /*TextView resultTxt = (TextView) mView.findViewById(R.id.textTut);
	  resultTxt.setText("tut: "+msg);*/
	  //Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	  //txt_percentage.setText("download complete");
	}
}
