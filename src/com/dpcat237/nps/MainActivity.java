package com.dpcat237.nps;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dpcat237.nps.helper.GenericHelper;
import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.repository.FeedRepository;
import com.dpcat237.nps.task.DownloadDataTask;
import com.dpcat237.nps.task.LoginTask;

public class MainActivity extends Activity {
	View mView;
	private FeedRepository datasource;
	Boolean logged;
	ArrayAdapter<Feed> adapter;
	public static String SELECTED_FEED_ID = "feedId";
	public static String SELECTED_FEED_TITLE = "feedTitle";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = this.findViewById(android.R.id.content).getRootView();
		logged = GenericHelper.checkLogged(this);

		if (logged) {
			showList();
		} else {
			showLogin();
		}
	}
	
	private void showLogin () {
		setContentView(R.layout.login);
	}
	
	private void showList () {
		setContentView(R.layout.activity_main);
		
		ListView listView = (ListView) findViewById(R.id.mylist);
		datasource = new FeedRepository(this);
		datasource.open();
		
		List<Feed> values = datasource.getAllFeeds();
		if (!values.isEmpty()) {
			adapter = new ArrayAdapter<Feed>(this, android.R.layout.simple_list_item_1, values);
			listView.setAdapter(adapter);
			
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (adapter.getCount() > 0) {
						Feed feed = (Feed) adapter.getItem(position);
						showItems(feed.api_id, feed.title);
						//adapter.remove(feed);
					}
				}
			});
		} else {
			Toast.makeText(this, "no feeds", Toast.LENGTH_SHORT).show();
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
		    case R.id.actionDropDb:
		    	dropDb();
		        return true;
	    }
		return false;
	}
	
	public void dropDb(){
		FeedRepository feedRepo = new FeedRepository(this);
        feedRepo.open();
        feedRepo.drop();
        GenericHelper.setLastFeedsUpdate(this, 0);
	}
	
	public void downloadData() {
		if (GenericHelper.hasConnection(this)) {
			DownloadDataTask task = new DownloadDataTask(this, mView);
			task.execute();
		} else {
			Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
		}
	}
	
	public void doLogin(View view) {
		if (GenericHelper.hasConnection(this)) {
			LoginTask task = new LoginTask(this, mView);
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
}