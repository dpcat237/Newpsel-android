package com.dpcat237.nps.task;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.helper.ApiHelper;
import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.repository.FeedRepository;

public class DownloadDataTask extends AsyncTask<Void, Integer, Void>{
	ApiHelper api;
	int progress_status;
	private Context mContext;
	private View mView;
	ProgressBar progressBar;
	//TextView txt_percentage;
	FeedRepository db;
	
	public DownloadDataTask(Context context, View view) {
		api = new ApiHelper();
        mContext = context;
        mView = view;
        db = new FeedRepository(mContext);
        db.open();
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
		Feed[] feeds = api.getFeeds();
		progressBar.setMax(feeds.length);
		
		for (Feed feed : feeds) {
			db.addFeed(feed);
			progress_status++;
			publishProgress(progress_status);
	    }
		
		
		/*while(progress_status<100){
			progress_status += 2;
			publishProgress(progress_status);
			SystemClock.sleep(100);
		}*/

		return null;
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
	  //txt_percentage.setText("download complete");
	}
}
