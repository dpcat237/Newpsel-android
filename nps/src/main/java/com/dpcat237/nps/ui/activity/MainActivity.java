package com.dpcat237.nps.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.service.DownloadSongsService;
import com.dpcat237.nps.behavior.service.PlayerService;
import com.dpcat237.nps.behavior.service.SyncDictationItemsService;
import com.dpcat237.nps.behavior.task.DownloadDataTask;
import com.dpcat237.nps.constant.MainActivityConstants;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.database.repository.DictateItemRepository;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.helper.ConnectionHelper;
import com.dpcat237.nps.helper.LoginHelper;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.ui.factory.MainFragmentFactoryManager;

public class MainActivity extends Activity {
    private static final String TAG = "NPS:MainActivity";
	View mView;
	protected Context mContext;
	private FeedRepository feedRepo;
	Boolean logged;
	public static Boolean UNREAD_NO_FIRST = false;
	Boolean ON_CREATE = false;
	public boolean isInFront;
    private SharedPreferences pref;
    private Menu mainMenu = null;
    private MenuItem buttonAddFeed;
    private MenuItem buttonSync;
    private MenuItem buttonDictate;
    private Boolean itemsActivated;
	//DrawerList
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mListsTitles;
    Bundle instanceState;
    private int lastPosition;

    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instanceState = savedInstanceState;
		mContext = this;
	    mView = this.findViewById(android.R.id.content).getRootView();
		logged = LoginHelper.checkLogged(this);
		feedRepo = new FeedRepository(this);
		feedRepo.open();
        PreferencesHelper.setPlayerActive(mContext, false);
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);

		if (logged) {
			ON_CREATE = true;
			showDrawer();
		} else {
			showWelcome();
		}
	}

	@SuppressLint("UseValueOf")
	@Override
	public void onResume() {
	    super.onResume();
	    isInFront = true;
	    logged = LoginHelper.checkLogged(this);
	    
	    if (!UNREAD_NO_FIRST) {
			UNREAD_NO_FIRST = true;
	    } else {
	    	feedRepo.open();
	    	reloadList();
	    }
	    
        if (logged) {
            itemsActivated = pref.getBoolean("pref_items_download_enable", false);
            drawerUpdateMenuItems();
        }
	}

	@Override
	protected void onPause() {
		super.onPause();
		isInFront = false;
		feedRepo.close();
	}
	
	private void showWelcome () {
		setContentView(R.layout.activity_welcome);
	}
	
	private void showDrawer () {
        Integer mainList = PreferencesHelper.getFeedsList(this);
		if (ON_CREATE) {
			setContentView(R.layout.activity_main);
			
			mListsTitles = getResources().getStringArray(R.array.feeds_list_array);
	        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	        mDrawerList = (ListView) findViewById(R.id.left_drawer);
	        
	        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
	        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.fragment_drawer_row, mListsTitles));
	        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	        getActionBar().setDisplayHomeAsUpEnabled(true);
	        getActionBar().setHomeButtonEnabled(true);
	        
	        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
	        mDrawerLayout.setDrawerListener(mDrawerToggle);
			
	        selectItem(mainList);
			ON_CREATE = false;
        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (logged) {
			mainMenu = menu;
			getMenuInflater().inflate(R.menu.main, menu);
            buttonAddFeed = menu.findItem(R.id.buttonAddFeed);
            buttonSync = menu.findItem(R.id.buttonSync);
            buttonDictate = menu.findItem(R.id.buttonDictate);
            itemsActivated = pref.getBoolean("pref_items_download_enable", false);
            drawerUpdateMenuItems();

			return true;
		}
		
		return false;
	}

    private void showButtonAddFeed() {
        if (itemsActivated) {
            buttonAddFeed.setVisible(true);
        } else {
            buttonAddFeed.setVisible(false);
        }
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
		
	    switch (item.getItemId()) {
		    case R.id.buttonSync:
		    	downloadData(item);
		        return true;
		    case R.id.buttonAddFeed:
		    	Intent intent = new Intent(this, AddFeedActivity.class);
				startActivity(intent);
		        return true;
		    case R.id.actionLogout:
		    	LoginHelper.doLogout(this);
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
            case R.id.buttonDictate:
                PlayerService.playpause(mContext, SongConstants.GRABBER_TYPE_DICTATE_ITEM, 0);
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
	
	public void downloadData(MenuItem item) {
        if (!ConnectionHelper.hasConnection(mContext)) {
            Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();

            return;
        }

        if (lastPosition == MainActivityConstants.DRAWER_ITEM_DICTATE_ITEMS) {
            Intent syncSongsService = new Intent(mContext, SyncDictationItemsService.class);
            startService(syncSongsService);

            Intent downloadSongsService = new Intent(mContext, DownloadSongsService.class);
            startService(downloadSongsService);
        } else {
            item.setEnabled(false);
            DownloadDataTask task = new DownloadDataTask(this, mView);
            task.execute();
		}
	}

	public void reloadList() {
		if (logged) {
			feedRepo.unreadCountUpdate();

			if (mainMenu != null) {
                buttonSync.setEnabled(true);
			}
            updateFragment();
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
        	// Pass any configuration change to the drawer toggles
        	mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    public void updateFragment() {
        Fragment fragment = new UnreadItemsFragment();
        Bundle args = new Bundle();
        args.putInt(UnreadItemsFragment.ARG_MAIN_LIST, lastPosition);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.content_fragment_main, fragment).commit();
    }
	
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    
    private void selectItem(int position) {
        lastPosition = position;
        updateFragment();
        drawerUpdateMenuItems();

    	mDrawerList.setItemChecked(position, true);
    	mDrawerLayout.closeDrawer(mDrawerList);
    	
    	if (UNREAD_NO_FIRST && !ON_CREATE) {
    		PreferencesHelper.setFeedsList(this, position);
            reloadList();
        }
        lastPosition = position;

    }

    private void drawerUpdateMenuItems() {
        if (mainMenu == null) {
            return;
        }

        if (lastPosition == MainActivityConstants.DRAWER_ITEM_DICTATE_ITEMS) {
            buttonAddFeed.setVisible(false);
            DictateItemRepository dictateRepo = new DictateItemRepository(mContext);
            dictateRepo.open();
            Integer unreadCount = dictateRepo.countUnreadItems();
            Log.d(TAG, "tut: drawerUpdateMenuItems unreadCount "+unreadCount);
            if (unreadCount > 0) {
                buttonDictate.setVisible(true);
            }
            dictateRepo.close();
        } else {
            showButtonAddFeed();
            buttonDictate.setVisible(false);
        }
    }

    public static class UnreadItemsFragment extends Fragment {
        private static final String TAG = "NPS:UnreadItemsFragment";
        public static final String ARG_MAIN_LIST = "main_list";
        private ListView listView;
        private Integer mainList;
        private MainFragmentFactoryManager factoryManager;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            mainList = getArguments().getInt(ARG_MAIN_LIST);
            View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);
            factoryManager = new MainFragmentFactoryManager();
            listView = (ListView) rootView.findViewById(R.id.mainFragmentList);
            factoryManager.prepareView(mainList, getActivity(), listView);

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            listView.setAdapter(null);
            factoryManager.prepareView(mainList, getActivity(), listView);
        }

    }
}