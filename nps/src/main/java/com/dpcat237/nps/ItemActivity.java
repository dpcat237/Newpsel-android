package com.dpcat237.nps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ShareActionProvider;

import com.dpcat237.nps.helper.GenericHelper;
import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.repository.FeedRepository;
import com.dpcat237.nps.repository.ItemRepository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class ItemActivity extends Activity {
	View mView;
	Context mContext;
	WebView mWebView;
	private ItemRepository itemRepo;
	private FeedRepository feedRepo;
	private Item item;
	private ShareActionProvider mShareActionProvider;
	SharedPreferences pref;
	private Integer CACHE_MAX_SIZE = 5000;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		
		
		mView = this.findViewById(android.R.id.content).getRootView();
		mContext = this;
		pref = PreferenceManager.getDefaultSharedPreferences(mContext);
		
	    setContentView(R.layout.item_view);
	    itemRepo = new ItemRepository(this);
	    itemRepo.open();
	    feedRepo = new FeedRepository(this);
	    feedRepo.open();

	    Intent intent = getIntent();
	    Long itemId = intent.getLongExtra(ItemsActivity.ITEM_ID, 0);
	    item = itemRepo.getItem(itemId);
	    Integer feedId = GenericHelper.getSelectedFeed(mContext);
	    Feed feed = feedRepo.getFeed(feedId);
	    
	    mWebView = (WebView) findViewById(R.id.itemContent);
	    mWebView.setFocusable(false);
	    mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
	    WebSettings ws = mWebView.getSettings();
		ws.setSupportZoom(true);
		ws.setBuiltInZoomControls(false);
		String textSize = pref.getString("pref_text_size", "100");
		ws.setTextZoom(Integer.parseInt(textSize));
		
		ws.setCacheMode(WebSettings.LOAD_DEFAULT);
		ws.setAppCacheMaxSize(CACHE_MAX_SIZE);
		ws.setAppCacheEnabled(true);
		
        long timestamp = item.getDateAdd() * 1000;
        String date = getDate(timestamp);
		String itemLink = item.getLink();
		
		String contentHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
				"<div style='border-bottom:1px solid #d3d3d3; padding-bottom:4px; font-weight: bold; font-size:1em;'>" +
			"<a style='text-decoration: none; color:#12c;' href='"+itemLink+"'>"+item.getTitle()+"</a>" +
		"</div>" +
		"<p style='margin-top:1px; font-size:1em;'><font style='color:#12c;'>"+feed.getTitle()+"</font>" +
				" <font style='color:#d3d3d3;'>on "+date+"</font></p>";
		
		mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		String content = "<div style='padding:0px 3px 0px 2px;'>"+contentHeader+item.getContent()+"</div>";
		mWebView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
	}

    private String getDate(long timeStamp){
        try{
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-MM kk:mm:ss");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
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