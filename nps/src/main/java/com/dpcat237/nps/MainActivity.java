package com.dpcat237.nps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dpcat237.nps.adapter.FeedsAdapter;
import com.dpcat237.nps.helper.GenericHelper;
import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.repository.FeedRepository;
import com.dpcat237.nps.task.DownloadDataTask;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
	View mView;
	Context mContext;
	private FeedRepository feedRepo;
	Boolean logged;
	ListView listView;
	FeedsAdapter mAdapter;
	public static Boolean UNREAD_NO_FIRST = false;
	Boolean ON_CREATE = false;
	public boolean isInFront;
	
	//DrawerList
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mListsTitles;
    Bundle instanceState;
    private Menu mainMenu = null;

    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instanceState = savedInstanceState;
		mContext = this;
	    mView = this.findViewById(android.R.id.content).getRootView();
		logged = GenericHelper.checkLogged(this);
		feedRepo = new FeedRepository(this);
		feedRepo.open();
		
		if (logged) {
			ON_CREATE = true;
			showList();
		} else {
			showWelcome();
		}
	}

	@SuppressLint("UseValueOf")
	@Override
	public void onResume() {
	    super.onResume();
	    isInFront = true;
	    
	    logged = GenericHelper.checkLogged(this);
	    
	    if (!UNREAD_NO_FIRST) {
			UNREAD_NO_FIRST = true;
	    } else {
	    	feedRepo.open();
	    	reloadList();
	    }
	    
	    if (logged && mAdapter.getCount() < 1) {
			Toast.makeText(this, R.string.no_feeds, Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		isInFront = false;
		feedRepo.close();
	}
	
	private void showWelcome () {
		setContentView(R.layout.welcome);
	}
	
	private void showList () {
		Integer feedList = GenericHelper.getFeedsList(this);
		if (ON_CREATE) {
			setContentView(R.layout.activity_main);
			
			mListsTitles = getResources().getStringArray(R.array.feeds_list_array);
	        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	        mDrawerList = (ListView) findViewById(R.id.left_drawer);
	        
	        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
	        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_item_list, mListsTitles));
	        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	        getActionBar().setDisplayHomeAsUpEnabled(true);
	        getActionBar().setHomeButtonEnabled(true);
	        
	        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
	        mDrawerLayout.setDrawerListener(mDrawerToggle);
			
	        selectItem(feedList);
			ON_CREATE = false;
        }
		listView = (ListView) findViewById(R.id.feedslist);

        List<Feed> feeds = new ArrayList<Feed>();
		if (feedList == 0) {
			feeds = feedRepo.getAllFeedsUnread();
		} else if (feedList == 1) {
			feeds = feedRepo.getAllFeeds();
		}

        mAdapter = new FeedsAdapter(this);
        mAdapter.addToDataset(feeds);
		listView.setAdapter(mAdapter);
		if (!feeds.isEmpty()) {
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (mAdapter.getCount() > 0) {
						Feed feed = (Feed) mAdapter.getItem(position);
						Integer feedId = (int) feed.getApiId();
						showItems(feedId);
					}
				}
			});
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (logged) {
			mainMenu = menu;
			getMenuInflater().inflate(R.menu.main, menu);

			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
		
	    switch (item.getItemId()) {
		    case R.id.buttonSync:
		    	downloadData();
		    	item.setEnabled(false);
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
		    case R.id.buttonActionLabels:
		    	Intent labelIntent = new Intent(this, LabelsActivity.class);
				startActivity(labelIntent);
		        return true;
		    case R.id.buttonActionSettings:
		    	showSettings();
		        return true;
		    case R.id.buttonAbout:
		    	showAbout();
		        return true;
	    }
		return false;
	}
	
	public void showSettings() {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void showAbout() {
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
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
	
	public void showItems(Integer feedId) {
		GenericHelper.setSelectedFeed(this, feedId);
		Intent intent = new Intent(this, ItemsActivity.class);
		startActivity(intent);
	}
	
	public void reloadList() {
		if (logged) {
			feedRepo.unreadCountUpdate();
			if (mAdapter.getCount() > 0) {
				listView.setAdapter(null);
			}
			
			showList();
			if (mainMenu != null) {
				mainMenu.findItem(R.id.buttonSync).setEnabled(true);
			}
		}
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        
        if (logged) {
        	// Sync the toggle state after onRestoreInstanceState has occurred.
        	mDrawerToggle.syncState();
        }
    }
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        
        if (logged) {
        	// Pass any configuration change to the drawer toggls
        	mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }
	
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    
    private void selectItem(int position) {
    	mDrawerList.setItemChecked(position, true);
    	mDrawerLayout.closeDrawer(mDrawerList);
    	
    	if (UNREAD_NO_FIRST && !ON_CREATE) {
    		GenericHelper.setFeedsList(this, position);
            reloadList();
        }
    }
}