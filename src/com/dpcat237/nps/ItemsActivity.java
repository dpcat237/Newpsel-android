package com.dpcat237.nps;

import java.util.ArrayList;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dpcat237.nps.adapter.ItemsAdapter;
import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.repository.ItemRepository;
import com.dpcat237.nps.task.ReadFeedItemsTask;
import com.dpcat237.nps.task.ReadItemTask;
import com.dpcat237.nps.task.StarItemTask;

public class ItemsActivity extends Activity {
	private ItemRepository itemRepo;
	public static String ITEM_ID = "itemId";
	public static String ITEM_COLOR_READ;
	public static String ITEM_COLOR_UNREAD;
	Integer feedId = 0;
	String feedTitle;
	Context mContext;
	ListView listView;
	ItemsAdapter mAdapter;
	ContextMenu cMenu;
	//Context Menu
	private final Integer cmGourId = 1; //Menu.NONE
	private final Integer CM_OPTION_1 = 1;
	private final Integer CM_OPTION_2 = 2;
	private final Integer CM_OPTION_3 = 3;
	private final Integer CM_OPTION_4 = 4;
	private final Integer CM_OPTION_5 = 5;
	private final Integer CM_OPTION_6 = 6;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.items_list);
		mContext = this;
		ITEM_COLOR_READ = mContext.getString(R.string.color_read);
		ITEM_COLOR_UNREAD = mContext.getString(R.string.color_unread);
		
		Intent intent = getIntent();
	    if (feedId == 0) {
		    feedId = intent.getIntExtra(MainActivity.SELECTED_FEED_ID, 0);
		    feedTitle = intent.getStringExtra(MainActivity.SELECTED_FEED_TITLE);
		    TextView txtFeedTitle= (TextView) this.findViewById(R.id.feedTitle);
		    txtFeedTitle.setText(feedTitle);
	    }
		
		itemRepo = new ItemRepository(this);
	    itemRepo.open();
	    ArrayList<Item> items = itemRepo.getIsUnreadItems(feedId, true);
	    listView = (ListView) findViewById(R.id.itemsList);
	    mAdapter = new ItemsAdapter(this, R.layout.item_row, items);
	    listView.setAdapter(mAdapter);
	    registerForContextMenu(listView);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mAdapter.getCount() > 0) {
					Item item = (Item) mAdapter.getItem(position);
					showItem(item.getId());
					
					if (item.isUnread()) {
						markReadItem(item.getId(), view);
						item.setIsUnread(false);
					}
				}
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.feeds, menu);

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case R.id.buttonAccept:
		    	markAllRead();
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
    	    Item item = (Item) mAdapter.getItem(info.position);
    	    
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
		Item item = (Item) mAdapter.getItem(info.position);
		View line = info.targetView;
		
	    if (mItem.getGroupId() == cmGourId) {
	        switch(mItem.getItemId()) {
		        case 1: 
		        	markReadItem(item.getId(), line);
		        	item.setIsUnread(false);
		        	return true;
		        case 2:
		        	markUnreadItem(item.getId(), line);
		        	item.setIsUnread(true);
		        	return true;
		        case 3:
		        	if (info.position > 0) { 
		        		markPrevoiusRead(info.position);
		        	} else {
		        		markReadItem(item.getId(), line);
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
	
	public void markPrevoiusRead (Integer position) {
		for (int i = 0; i <= position; i++) {
			Item item = (Item) mAdapter.getItem(i);
			if (item.isUnread()) {
				LinearLayout line = (LinearLayout) listView.getChildAt(i);
				markReadItem(item.getId(), line);
	        	item.setIsUnread(false);
			}
		}
	}
	
	public void markReadItem(Long itemId, View line) {
		ReadItemTask task = new ReadItemTask(this, itemId, false);
		task.execute();
		
		line.setBackgroundColor(Color.parseColor(ITEM_COLOR_READ));
	}
	
	public void markUnreadItem(Long itemId, View line) {
		ReadItemTask task = new ReadItemTask(this, itemId, true);
		task.execute();
		
		line.setBackgroundColor(Color.parseColor(ITEM_COLOR_UNREAD));
	}
	
	public void starItem(Long itemId, View line) {
		ImageView stared = (ImageView) line.findViewById(R.id.itemStared);
		stared.setBackgroundResource(R.drawable.is_stared);
		StarItemTask task = new StarItemTask(mContext, itemId, true);
		task.execute();
	}
	
	public void unstarItem(Long itemId, View line) {
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
	
	public void showItem(Long itemId) {
		Intent intent = new Intent(this, ItemActivity.class);
		intent.putExtra(ITEM_ID, itemId);
		startActivity(intent);
	}
	
	public void markAllRead() {
		ReadFeedItemsTask task = new ReadFeedItemsTask(this, MainActivity.class, feedId);
		task.execute();
	}
}