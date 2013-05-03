package com.dpcat237.nps;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.repository.FeedRepository;
import com.dpcat237.nps.task.DownloadDataTask;

public class MainActivity extends Activity {
	View mView;
	private FeedRepository datasource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = this.findViewById(android.R.id.content).getRootView();
		setContentView(R.layout.activity_main);
		
		ListView listView = (ListView) findViewById(R.id.mylist);
		datasource = new FeedRepository(this);
		datasource.open();
		
		List<Feed> values = datasource.getAllFeeds();
		// First paramenter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the View to which the data is written
		// Forth - the Array of data
		ArrayAdapter<Feed> adapter = new ArrayAdapter<Feed>(this, android.R.layout.simple_list_item_1, values);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(getApplicationContext(), "Click ListItem Number " + position, Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.buttonSync:
	    	downloadData();
	        return true;
	    }
		return false;
	}
	
	public void showFeeds(View view) {
		Intent intent = new Intent(this, FeedsActivity.class);
		startActivity(intent);
	}
	
	public void tryJson(View view) {
		Intent intent = new Intent(this, JsonActivity.class);
		startActivity(intent);
	}

	public void downloadData() {
		DownloadDataTask task = new DownloadDataTask(this, mView);
		task.execute();
	}
}
