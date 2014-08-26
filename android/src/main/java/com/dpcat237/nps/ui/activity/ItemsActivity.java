package com.dpcat237.nps.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.service.PlayerService;
import com.dpcat237.nps.behavior.task.ReadFeedItemsTask;
import com.dpcat237.nps.behavior.task.ReadItemTask;
import com.dpcat237.nps.behavior.task.StarItemTask;
import com.dpcat237.nps.constant.ItemConstants;
import com.dpcat237.nps.constant.MainActivityConstants;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.database.repository.ItemRepository;
import com.dpcat237.nps.database.repository.SongRepository;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.common.model.Feed;
import com.dpcat237.nps.common.model.Item;
import com.dpcat237.nps.ui.adapter.ItemsAdapter;

import java.util.ArrayList;

public class ItemsActivity extends Activity {
    private static final String TAG = "NPS:ItemsActivity";
	private ItemRepository itemRepo;
	private FeedRepository feedRepo;
    private SongRepository songRepo;
	public static String ITEM_COLOR_READ;
	public static String ITEM_COLOR_UNREAD;
	Integer feedId = 0;
	Context mContext;
	View mView;
	ListView listView;
	ItemsAdapter mAdapter;
	ContextMenu cMenu;
	//Context Menu
	private final Integer cmGourId = 1;
	private final Integer CM_OPTION_1 = 1;
	private final Integer CM_OPTION_2 = 2;
	private final Integer CM_OPTION_3 = 3;
	private final Integer CM_OPTION_4 = 4;
	private final Integer CM_OPTION_5 = 5;
	private final Integer CM_OPTION_6 = 6;
    private Integer feedList;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);
		mContext = this;
		mView = this.findViewById(android.R.id.content).getRootView();
        setTitle(mContext.getString(R.string.activity_articles));
		ITEM_COLOR_READ = mContext.getString(R.string.color_read);
		ITEM_COLOR_UNREAD = mContext.getString(R.string.color_unread);
	    feedRepo = new FeedRepository(this);
	    feedRepo.open();
	    itemRepo = new ItemRepository(this);
	    itemRepo.open();
        songRepo = new SongRepository(mContext);
        songRepo.open();

	    feedId = PreferencesHelper.getMainListId(mContext);
	    Feed feed = feedRepo.getFeed(feedId);

	    TextView txtFeedTitle= (TextView) this.findViewById(R.id.feedTitle);
	    txtFeedTitle.setText(feed.getTitle());
	    feedList = PreferencesHelper.getMainDrawerOption(this);

	    listView = (ListView) findViewById(R.id.itemsList);
	    mAdapter = new ItemsAdapter(this);
        mAdapter.addToDataset(getItems());
	    listView.setAdapter(mAdapter);
	    registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mAdapter.getCount() > 0) {
					Item item = mAdapter.getItem(position);
					showItem(item.getApiId());

					if (item.isUnread()) {
						markReadItem(item.getApiId(), view);
						item.setIsUnread(false);
                        songRepo.markAsPlayed(item.getApiId(), SongConstants.GRABBER_TYPE_TITLE, true);
					}
				}
			}
		});
	}

	@Override
	public void onResume() {
	    super.onResume();

	    feedRepo.open();
	    itemRepo.open();
        songRepo.open();

        //finish activity if main list is different than "all items" and feed doesn't have unread items
        if (feedRepo.getFeedUnreadCount(feedId) < 1 && PreferencesHelper.getMainDrawerOption(mContext) != MainActivityConstants.DRAWER_ITEM_ALL_ITEMS) {
            finish();
        }

        mAdapter.updateList(getItems());
	}

	@Override
	protected void onPause() {
		super.onPause();
		feedRepo.close();
		itemRepo.close();
        songRepo.close();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.item_list, menu);

        if (songRepo.checkListHasGrabbedSongs(feedId, SongConstants.GRABBER_TYPE_TITLE)) {
            MenuItem dictateItem = menu.findItem(R.id.buttonDictate);
            dictateItem.setVisible(true);
        }

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case R.id.buttonAccept:
		    	markAllRead();
		        return true;
            case R.id.buttonDictate:
                PlayerService.playpause(mContext, SongConstants.GRABBER_TYPE_TITLE, feedId);
                return true;
	    }
		return false;
	}

	@Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
    	if (view.getId() == R.id.itemsList) {
    		cMenu = menu;
    		cMenu.clearHeader();
    		cMenu.clear();

    	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
    	    Item item = mAdapter.getItem(info.position);

    	    if (item.isUnread()) {
    	    	cMenu.add(cmGourId, CM_OPTION_1, 1, R.string.cm_mark_read);
    	    } else {
    	    	cMenu.add(cmGourId, CM_OPTION_2, 1, R.string.cm_mark_unread);
    	    }
    	    cMenu.add(cmGourId, CM_OPTION_3, 2, R.string.cm_mark_previous);
    		if (!item.isStared()) {
    			cMenu.add(cmGourId, CM_OPTION_4, 3, R.string.cm_add_star);
    	    } else {
    	    	cMenu.add(cmGourId, CM_OPTION_5, 3, R.string.cm_remove_star);
    	    }
    		cMenu.add(cmGourId, CM_OPTION_6, 4, R.string.cm_share);

    		super.onCreateContextMenu(cMenu, view, menuInfo);
    	}
    }

	@Override
	public boolean onContextItemSelected(MenuItem mItem) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)mItem.getMenuInfo();
		Item item = mAdapter.getItem(info.position);
		View line = info.targetView;

	    if (mItem.getGroupId() == cmGourId) {
	        switch(mItem.getItemId()) {
		        case 1:
		        	markReadItem(item.getApiId(), line);
		        	item.setIsUnread(false);
		        	return true;
		        case 2:
		        	markUnreadItem(item.getApiId(), line);
		        	item.setIsUnread(true);
                    songRepo.markAsPlayed(item.getApiId(), SongConstants.GRABBER_TYPE_TITLE, false);
		        	return true;
		        case 3:
		        	if (info.position > 0) {
                        markPreviousRead(info.position);
		        	} else {
		        		markReadItem(item.getApiId(), line);
                        songRepo.markAsPlayed(item.getApiId(), SongConstants.GRABBER_TYPE_TITLE, true);
			        	item.setIsUnread(false);
		        	}
		        	return true;
		        case 4:
		        	starItem(item.getId(), line);
		        	item.setIsStared(true);
		        	return true;
		        case 5:
		        	unstarItem(item.getId(), line);
		        	item.setIsStared(false);
		        	return true;
		        case 6:
		        	shareItem(item.getLink());
		        	return true;
		        default:
		            return super.onContextItemSelected(mItem);
	        }
	    }

	    return false;
	}

    private ArrayList<Item> getItems() {
        ArrayList<Item> items = null;
        if (feedList == 0) {
            items = itemRepo.getIsUnreadItems(feedId, true);
        } else if (feedList == 1) {
            items = itemRepo.getIsUnreadItems(feedId, false);
        }

        return items;
    }

	public void markPreviousRead (Integer position) {
		for (int i = listView.getFirstVisiblePosition(); i <= position; i++) {
			View line = listView.getChildAt(i - listView.getFirstVisiblePosition());
			line.setBackgroundColor(Color.parseColor(ITEM_COLOR_READ));
		}

		for (int i = 0; i <= position; i++) {
			Item item = mAdapter.getItem(i);
        	item.setIsUnread(false);
            songRepo.markAsPlayed(item.getApiId(), SongConstants.GRABBER_TYPE_TITLE, true);

        	ReadItemTask task = new ReadItemTask(this, item.getApiId(), false);
    		task.execute();
		}
	}

	public void markReadItem(Integer itemApiId, View line) {
		ReadItemTask task = new ReadItemTask(this, itemApiId, false);
		task.execute();

		line.setBackgroundColor(Color.parseColor(ITEM_COLOR_READ));
	}

	public void markUnreadItem(Integer itemApiId, View line) {
		ReadItemTask task = new ReadItemTask(this, itemApiId, true);
		task.execute();

		line.setBackgroundColor(Color.parseColor(ITEM_COLOR_UNREAD));
	}

	public void starItem(Integer itemId, View line) {
		ImageView stared = (ImageView) line.findViewById(R.id.itemStared);
		stared.setBackgroundResource(R.drawable.is_stared);
		StarItemTask task = new StarItemTask(mContext, itemId, true);
		task.execute();
	}

	public void unstarItem(Integer itemId, View line) {
		ImageView stared = (ImageView) line.findViewById(R.id.itemStared);
		stared.setBackgroundResource(R.drawable.isnt_stared);
		StarItemTask task = new StarItemTask(mContext, itemId, false);
		task.execute();
	}

	public void shareItem(String link) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, link);

		startActivity(Intent.createChooser(intent, "Share:"));
	}

	public void showItem(Integer itemApiId) {
		Intent intent = new Intent(this, ItemActivity.class);
		intent.putExtra(ItemConstants.ITEM_API_ID, itemApiId);
        PreferencesHelper.setCurrentItemApiId(mContext, 0);
		startActivity(intent);
	}

	public void markAllRead() {
		ReadFeedItemsTask task = new ReadFeedItemsTask(this, MainActivity.class, feedId);
		task.execute();
	}
}