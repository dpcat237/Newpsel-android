package com.dpcat237.nps.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ShareActionProvider;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.service.PlayerService;
import com.dpcat237.nps.behavior.valueObject.PlayerServiceStatus;
import com.dpcat237.nps.constant.ItemConstants;
import com.dpcat237.nps.constant.PreferenceConstants;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.database.repository.DictateItemRepository;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.database.repository.SongRepository;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.common.model.DictateItem;
import com.dpcat237.nps.common.model.Feed;
import com.dpcat237.nps.ui.block.ItemBlock;
import com.dpcat237.nps.ui.dialog.LabelsDialog;

@SuppressLint("SimpleDateFormat")
public class DictateItemActivity extends Activity {
    private static final String TAG = "NPS:DictateItemActivity";
    private Context mContext;
	private Feed feed;
    private DictateItem item;
	private ShareActionProvider mShareActionProvider;
    private MenuItem buttonDictate;
    private MenuItem readButton;
    private MenuItem unreadButton;
    private DictateItemRepository itemRepo;
    private FeedRepository feedRepo;
    private SongRepository songRepo;
    private PlayerServiceStatus playerStatus;


	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        setTitle("");

	    setContentView(R.layout.activity_item_view);
        getNecessaryData();

        if (item == null) {
            finish();
            return;
        }
        WebView mWebView = (WebView) findViewById(R.id.itemContent);
        ItemBlock.prepareWebView(mWebView, pref.getString("pref_text_size", "100"), item.getLink(), item.getTitle(), feed.getTitle(), item.getContent(), item.getDateAdd());
        playerStatus = PlayerServiceStatus.getInstance();
	}

    private void openDB() {
        itemRepo = new DictateItemRepository(mContext);
        itemRepo.open();
        feedRepo = new FeedRepository(mContext);
        feedRepo.open();
        songRepo = new SongRepository(mContext);
        songRepo.open();
    }

    private void closeDB() {
        itemRepo.close();
        feedRepo.close();
        songRepo.close();
    }

    /**
     * Get item and feed data
     */
    private void getNecessaryData() {
        openDB();
        //Get passed item Id and them his and feed data
        Integer itemApiId = getItemApiId();
        PreferencesHelper.setIntPreference(mContext, PreferenceConstants.DICTATION_NOW_OPENED, itemApiId);
        item = itemRepo.getItem(itemApiId);
        if (item == null) {
            return;
        }

        if (item.getFeedApiId() > 0) {
            feed = feedRepo.getFeed(item.getFeedApiId());
        } else {
            feed = new Feed();
            feed.setTitle("Shared");
        }
    }

    private Integer getItemApiId() {
        Integer itemApiId = PreferencesHelper.getCurrentItemApiId(mContext);
        if (itemApiId > 1) {
            itemRepo.readItem(itemApiId, false);

            return itemApiId;
        }

        return getIntent().getIntExtra(ItemConstants.ITEM_API_ID, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item, menu);
        buttonDictate = menu.findItem(R.id.buttonDictate);
        readButton = menu.findItem(R.id.buttonRead);
        unreadButton = menu.findItem(R.id.buttonUnread);
        MenuItem buttonShare = menu.findItem(R.id.buttonShare);

        prepareDictateButton();
        prepareShareButton(buttonShare);

        //when come from player
        markRead();

        return true;
    }

    private void prepareDictateButton() {
        SongRepository songRepo = new SongRepository(mContext);
        songRepo.open();
        if (songRepo.checkSongGrabbed(item.getItemApiId(), SongConstants.GRABBER_TYPE_DICTATE_ITEM)) {
            buttonDictate.setVisible(true);
        }

        songRepo.close();
    }

    private void prepareShareButton(MenuItem buttonShare) {
        mShareActionProvider = (ShareActionProvider)buttonShare.getActionProvider();
        mShareActionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        mShareActionProvider.setShareIntent(createShareIntent());
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

    private void setLabel() {
        FragmentManager fm = ((Activity) mContext).getFragmentManager();
        LabelsDialog editNameDialog = new LabelsDialog(mContext, item.getItemApiId());
        editNameDialog.setRetainInstance(true);
        editNameDialog.show(fm, "fragment_select_label");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.buttonLabel:
                setLabel();
                return true;
            case R.id.buttonRead:
                markUnread();
                return true;
            case R.id.buttonUnread:
                markRead();
                return true;
            case R.id.buttonShare:
                setShareIntent(createShareIntent());
                return true;
            case R.id.buttonDictate:
                PlayerService.playPauseSong(mContext, SongConstants.GRABBER_TYPE_DICTATE_ITEM, item.getItemApiId());
                return true;
            case android.R.id.home:
                if (playerStatus.hasActiveSong()) {
                    launchMainActivity();
                }

                return super.onOptionsItemSelected(menuItem);
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        openDB();
    }

    @Override
    protected void onPause() {
        closeDB();
        if (playerStatus.hasActiveSong()) {
            finish();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        PreferencesHelper.setIntPreference(mContext, PreferenceConstants.DICTATION_NOW_OPENED, 0);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (playerStatus.hasActiveSong()) {
            launchMainActivity();
        }
        super.onBackPressed();
    }

    private void updateReadButton() {
        if (item.isUnread()) {
            readButton.setVisible(false);
            unreadButton.setVisible(true);
        } else {
            readButton.setVisible(true);
            unreadButton.setVisible(false);
        }
    }

    private void markRead() {
        item.setIsUnread(false);
        updateReadButton();
        itemRepo.readItem(item.getItemApiId(), false);
        songRepo.markAsPlayed(item.getItemApiId(), SongConstants.GRABBER_TYPE_DICTATE_ITEM, true);
    }

    private void markUnread() {
        item.setIsUnread(true);
        updateReadButton();
        itemRepo.readItem(item.getItemApiId(), true);
        songRepo.markAsPlayed(item.getItemApiId(), SongConstants.GRABBER_TYPE_DICTATE_ITEM, false);
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}