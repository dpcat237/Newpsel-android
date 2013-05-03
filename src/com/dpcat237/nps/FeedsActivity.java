package com.dpcat237.nps;

import java.util.List;
import java.util.Random;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.repository.FeedRepository;

public class FeedsActivity extends ListActivity {
	private FeedRepository datasource;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.feeds_list);

	    datasource = new FeedRepository(this);
		datasource.open();
		
		List<Feed> values = datasource.getAllFeeds();
		ArrayAdapter<Feed> adapter = new ArrayAdapter<Feed>(this, android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.feeds, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_create_feed:
			createFeed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void createFeed() {
		Intent i = new Intent(this, FeedActivity.class);
		//startActivityForResult(i, ACTIVITY_CREATE);
	}
	
	// Will be called via the onClick attribute
		// of the buttons in main.xml
		public void onClick(View view) {
			@SuppressWarnings("unchecked")
			ArrayAdapter<Feed> adapter = (ArrayAdapter<Feed>) getListAdapter();
			Feed feed = null;
			switch (view.getId()) {
			case R.id.add:
				String[] feeds = new String[] { "Cool", "Very nice", "Hate it" };
				int nextInt = new Random().nextInt(3);
				// Save the new feed to the database
				feed = datasource.createFeed(feeds[nextInt]);
				adapter.add(feed);
				break;
			case R.id.delete:
				if (getListAdapter().getCount() > 0) {
					feed = (Feed) getListAdapter().getItem(0);
					datasource.deleteFeed(feed);
					adapter.remove(feed);
				}
				break;
			}
			adapter.notifyDataSetChanged();
		}

		@Override
		protected void onResume() {
			super.onResume();
			datasource.open();
		}

		@Override
		protected void onPause() {
			super.onPause();
			datasource.close();
		}

}


/*package com.dpcat237.nps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FeedsActivity extends Activity {
	private static final int ACTIVITY_CREATE = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.feeds_list);

	    final ListView listview = (ListView) findViewById(R.id.feeds_list);
	    String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
	        "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
	        "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
	        "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
	        "Android", "iPhone", "WindowsMobile" };

	    final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < values.length; ++i) {
	      list.add(values[i]);
	    }
	    final StableArrayAdapter adapter = new StableArrayAdapter(this,
	        android.R.layout.simple_list_item_1, list);
	    listview.setAdapter(adapter);

	    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	      @Override
	      public void onItemClick(AdapterView<?> parent, final View view,
	          int position, long id) {
	        final String item = (String) parent.getItemAtPosition(position);
	        view.animate().setDuration(2000).alpha(0)
	            .withEndAction(new Runnable() {
	              @Override
	              public void run() {
	                list.remove(item);
	                adapter.notifyDataSetChanged();
	                view.setAlpha(1);
	              }
	            });
	      }

	    });
		
	}
	
	private class StableArrayAdapter extends ArrayAdapter<String> {

	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

	    public StableArrayAdapter(Context context, int textViewResourceId,
	        List<String> objects) {
	      super(context, textViewResourceId, objects);
	      for (int i = 0; i < objects.size(); ++i) {
	        mIdMap.put(objects.get(i), i);
	      }
	    }

	    @Override
	    public long getItemId(int position) {
	      String item = getItem(position);
	      return mIdMap.get(item);
	    }

	    @Override
	    public boolean hasStableIds() {
	      return true;
	    }

	  }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.feeds, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_create_feed:
			createFeed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void createFeed() {
		Intent i = new Intent(this, FeedActivity.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

}*/