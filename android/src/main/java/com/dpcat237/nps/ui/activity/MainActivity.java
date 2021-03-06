package com.dpcat237.nps.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.LocalBroadcastManager;
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

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.alarm.AlarmsControlAlarm;
import com.dpcat237.nps.behavior.task.SyncLauncherTask;
import com.dpcat237.nps.behavior.valueObject.PlayerServiceStatus;
import com.dpcat237.nps.common.constant.BroadcastConstants;
import com.dpcat237.nps.constant.MainActivityConstants;
import com.dpcat237.nps.constant.PreferenceConstants;
import com.dpcat237.nps.database.repository.DictateItemRepository;
import com.dpcat237.nps.helper.GcmHelper;
import com.dpcat237.nps.helper.GoogleServicesHelper;
import com.dpcat237.nps.helper.LoginHelper;
import com.dpcat237.nps.helper.NotificationHelper;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.ui.activity.Related.MainHelper;
import com.dpcat237.nps.ui.factory.MainFragmentFactory;
import com.dpcat237.nps.ui.factory.MainFragmentFactoryManager;

public class MainActivity extends Activity {
    private static final String TAG = "NPS:MainActivity";
	View mView;
	protected Context mContext;
	Boolean logged;
	public static Boolean UNREAD_NO_FIRST = false;
	Boolean ON_CREATE = false;
	public Boolean isInFront;
    private SharedPreferences pref;
    private Menu mainMenu = null;
    private MenuItem buttonSync;
    private MenuItem buttonDictate;
    private Boolean itemsActivated;
	//DrawerList
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    Bundle instanceState;
    private int lastPosition;
    private Integer countFragmentView = 0;
    private BroadcastReceiver receiver;
    private PlayerServiceStatus playerStatus;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instanceState = savedInstanceState;
		mContext = this;
	    mView = this.findViewById(android.R.id.content).getRootView();
		logged = LoginHelper.checkLogged(this);
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);

		if (logged) {
			ON_CREATE = true;
			showDrawer();
            GcmHelper.checkRegId(mContext);
		} else {
            GoogleServicesHelper.checkPlayServices(mContext, this);
			showWelcome();
		}

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                broadcastUpdate(intent.getStringExtra(BroadcastConstants.MAIN_ACTIVITY_MESSAGE));
            }
        };
        playerStatus = PlayerServiceStatus.getInstance();

        AlarmsControlAlarm alarmsControlAlarm = new AlarmsControlAlarm();
        alarmsControlAlarm.setAlarm(mContext);
	}

    public void broadcastUpdate(String command) {
        Log.d(TAG, "tut: broadcastUpdate a");
        if (command.equals(BroadcastConstants.COMMAND_A_MAIN_RELOAD_ITEMS)
                && (lastPosition == MainActivityConstants.DRAWER_MAIN_ITEMS)) {
            Log.d(TAG, "tut: broadcastUpdate b 1");
            reloadList();
        } else if (command.equals(BroadcastConstants.COMMAND_A_MAIN_RELOAD_LATER)
            && lastPosition == MainActivityConstants.DRAWER_MAIN_LATER_ITEMS) {
            Log.d(TAG, "tut: broadcastUpdate b 2");
            reloadList();
        } else if (command.equals(BroadcastConstants.COMMAND_A_MAIN_RELOAD_DICTATIONS)
                && lastPosition == MainActivityConstants.DRAWER_MAIN_DICTATE_ITEMS) {
            Log.d(TAG, "tut: broadcastUpdate b 3");
            reloadList();

            DictateItemRepository dictateRepo = new DictateItemRepository(mContext);
            dictateRepo.open();
            if (dictateRepo.countUnreadGrabberItems() > 0) {
                buttonDictate.setVisible(true);
            }
            dictateRepo.close();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(BroadcastConstants.MAIN_ACTIVITY));
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
	    	reloadList();
	    }

        if (logged) {
            itemsActivated = pref.getBoolean("pref_items_download_enable", true);
            drawerUpdateMenuItems();
        }
	}

	@Override
	protected void onPause() {
        if (playerStatus.hasActiveSong()) {
            finish();
        }

		super.onPause();
		isInFront = false;
	}

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

	private void showWelcome () {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
	}

	private void showDrawer () {
        Integer mainList = PreferencesHelper.getIntPreference(mContext, PreferenceConstants.MAIN_DRAWER_ITEM);
        lastPosition = mainList;
		if (ON_CREATE) {
			setContentView(R.layout.activity_main);

            String[] mListsTitles = getResources().getStringArray(R.array.drawer_main_activity);
	        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	        mDrawerList = (ListView) findViewById(R.id.left_drawer);

	        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
	        mDrawerList.setAdapter(new ArrayAdapter<>(mContext, R.layout.fragment_drawer_row, mListsTitles));
	        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	        getActionBar().setDisplayHomeAsUpEnabled(true);
	        getActionBar().setHomeButtonEnabled(true);

	        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close);
	        mDrawerLayout.setDrawerListener(mDrawerToggle);

	        selectDrawerItem(mainList);
			ON_CREATE = false;
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (logged) {
			mainMenu = menu;
			getMenuInflater().inflate(R.menu.main, menu);
            buttonSync = menu.findItem(R.id.buttonSync);
            buttonDictate = menu.findItem(R.id.buttonDictate);
            itemsActivated = pref.getBoolean("pref_items_download_enable", true);
            drawerUpdateMenuItems();

			return true;
		}

		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return MainHelper.OptionsItemSelector(mContext, this, mView, item);
	}

	public void reloadList() {
		if (logged) {
            MainFragmentFactory.updateItemsCount(mContext, lastPosition);

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
        countFragmentView++;
        Fragment fragment = new UnreadItemsFragment();
        Bundle args = new Bundle();
        args.putInt(UnreadItemsFragment.ARG_MAIN_LIST, lastPosition);
        args.putInt(UnreadItemsFragment.ARG_VIEWED, countFragmentView);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.content_fragment_main, fragment).commit();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectDrawerItem(position);
        }
    }

    private void selectDrawerItem(int position) {
        Boolean changed = (lastPosition != position);
        lastPosition = position;
        updateFragment();
        drawerUpdateMenuItems();

    	mDrawerList.setItemChecked(position, true);
    	mDrawerLayout.closeDrawer(mDrawerList);

    	if (UNREAD_NO_FIRST && !ON_CREATE) {
            PreferencesHelper.setIntPreference(mContext, PreferenceConstants.MAIN_DRAWER_ITEM, position);
            reloadList();
        }

        if (changed && MainFragmentFactory.necessarySync(lastPosition)) {
            SyncLauncherTask requireSync = new SyncLauncherTask(mContext, lastPosition);
            requireSync.execute();
        }
    }

    private void drawerUpdateMenuItems() {
        if (mainMenu == null) {
            return;
        }

        MainFragmentFactory.showRequiredMenuItems(mContext, mainMenu, lastPosition, itemsActivated);
    }

    public static class UnreadItemsFragment extends Fragment {
        private static final String TAG = "NPS:UnreadItemsFragment";
        public static final String ARG_MAIN_LIST = "main_list";
        public static final String ARG_VIEWED = "viewed";
        private ListView listView;
        private Integer mainList;
        private MainFragmentFactoryManager factoryManager;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            mainList = getArguments().getInt(ARG_MAIN_LIST);
            Integer viewed = getArguments().getInt(ARG_VIEWED);
            View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);
            factoryManager = new MainFragmentFactoryManager();
            listView = (ListView) rootView.findViewById(R.id.mainFragmentList);
            Integer countItems = factoryManager.prepareView(mainList, getActivity(), listView);
            if (countItems < 1 && viewed == 1) {
                NotificationHelper.showSimpleToast(getActivity(), getActivity().getString(R.string.no_new_articles));
            }

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