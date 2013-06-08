package com.dpcat237.nps;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ShareActionProvider;

import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.repository.ItemRepository;

public class ItemActivity extends Activity {
	private ItemRepository itemRepo;
	private Item item;
	private ShareActionProvider mShareActionProvider;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(false);
	    setContentView(R.layout.item_view);
	    itemRepo = new ItemRepository(this);
	    itemRepo.open();

	    Intent intent = getIntent();
	    Long itemId = intent.getLongExtra(ItemsActivity.ITEM_ID, 0);
	    item = itemRepo.getItem(itemId);
	    
	    WebView viewItemContent = (WebView) findViewById(R.id.itemContent);
	    viewItemContent.loadData(item.getContent(), "text/html", "UTF-8");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.item, menu);
		
		MenuItem item = menu.findItem(R.id.buttonShare);
		mShareActionProvider = (ShareActionProvider)item.getActionProvider();
		mShareActionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
		mShareActionProvider.setShareIntent(createShareIntent());

		return true;
	}
	
	private Intent createShareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND); 
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, item.getTitle()); 
		shareIntent.putExtra(Intent.EXTRA_TEXT, item.getLink()); 
		shareIntent.setType("text/plain");
		
    	return shareIntent;	
    }
	
	
	private void setShareIntent(Intent shareIntent) {
		if (mShareActionProvider != null) {
			mShareActionProvider.setShareIntent(shareIntent);	
		}	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case R.id.buttonShare:
		    	setShareIntent(createShareIntent());
		        return true;
	    }
		return false;
	}
	
}