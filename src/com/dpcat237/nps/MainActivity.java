package com.dpcat237.nps;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.dpcat237.nps.adapter.FeedsAdapter;
import com.dpcat237.nps.helper.GenericHelper;
import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.repository.FeedRepository;
import com.dpcat237.nps.task.DownloadDataTask;

public class MainActivity extends Activity {
	View mView;
	Context mContext;
	private FeedRepository feedRepo;
	Boolean logged;
	ListView listView;
	FeedsAdapter mAdapter;
	public static String SELECTED_FEED_ID = "feedId";
	public static String SELECTED_FEED_TITLE = "feedTitle";
	public static Boolean UNREAD_NO_FIRST = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
	    mView = this.findViewById(android.R.id.content).getRootView();
		logged = GenericHelper.checkLogged(this);

		if (logged) {
			showList();
		} else {
			showWelcome();
		}
	}
	
	private void showWelcome () {
		setContentView(R.layout.welcome);
	}
	
	private void showList () {
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.feedslist);
		feedRepo = new FeedRepository(this);
		feedRepo.open();
		
		ArrayList<Feed> feeds = feedRepo.getAllFeedsUnread();
		mAdapter = new FeedsAdapter(this, R.layout.feed_row, feeds);
		listView.setAdapter(mAdapter);
		if (!feeds.isEmpty()) {
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (mAdapter.getCount() > 0) {
						Feed feed = (Feed) mAdapter.getItem(position);
						showItems(feed.api_id, feed.title);
					}
				}
			});
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (logged) {
			getMenuInflater().inflate(R.menu.main, menu);

			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case R.id.buttonSync:
		    	downloadData();
		        return true;
		    case R.id.buttonAddFeed:
		    	Intent intent = new Intent(this, AddFeedActivity.class);
				startActivity(intent);
		        return true;
		    case R.id.actionLogout:
		    	dropDb();
		    	GenericHelper.doLogout(this);
		    	finish();
		        return true;
		    /*case R.id.actionCheck:
		    	feedRepo.unreadCountUpdate();
		    	
		    	Toast.makeText(this, "test: ok", Toast.LENGTH_SHORT).show();
		        return true;*/
	    }
		return false;
	}
	
	public void goSignIn(View view) {
		Intent intent = new Intent(this, SignInActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void goSignUp(View view) {
		Intent intent = new Intent(this, SignUpActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void dropDb(){
		FeedRepository feedRepo = new FeedRepository(this);
        feedRepo.open();
        feedRepo.drop();
        GenericHelper.setLastFeedsUpdate(this, 0);
	}
	
	public void downloadData() {
		if (GenericHelper.hasConnection(this)) {
			DownloadDataTask task = new DownloadDataTask(this, mView, listView);
			task.execute();
		} else {
			Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
		}
	}
	
	public void showItems(Integer feedId, String feedTitle) {
		Intent intent = new Intent(this, ItemsActivity.class);
		intent.putExtra(SELECTED_FEED_ID, feedId);
		intent.putExtra(SELECTED_FEED_TITLE, feedTitle);
		startActivity(intent);
	}
	
	public void reloadList() {
		if (logged) {
			feedRepo.unreadCountUpdate();
			if (mAdapter.getCount() > 0) {
				mAdapter.clear();
			}
			showList();
			
			
		}
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    logged = GenericHelper.checkLogged(this);
	    
	    if (!UNREAD_NO_FIRST) {
			UNREAD_NO_FIRST = true;
	    } else {
	    	reloadList();
	    }
	    
	    if (logged && mAdapter.getCount() < 1) {
			Toast.makeText(this, R.string.no_feeds, Toast.LENGTH_SHORT).show();
		}
	}
	
}