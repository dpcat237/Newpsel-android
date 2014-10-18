package com.dpcat237.nps.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ShareActionProvider;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.manager.ItemTtsManager;
import com.dpcat237.nps.behavior.task.StarItemTask;
import com.dpcat237.nps.behavior.valueObject.PlayerServiceStatus;
import com.dpcat237.nps.common.constant.BroadcastConstants;
import com.dpcat237.nps.common.model.Feed;
import com.dpcat237.nps.common.model.Item;
import com.dpcat237.nps.constant.ItemConstants;
import com.dpcat237.nps.constant.MainActivityConstants;
import com.dpcat237.nps.constant.PreferenceConstants;
import com.dpcat237.nps.constant.SongConstants;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.database.repository.ItemRepository;
import com.dpcat237.nps.database.repository.SongRepository;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.ui.block.ItemBlock;
import com.dpcat237.nps.ui.dialog.LabelsDialog;

@SuppressLint("SimpleDateFormat")
public class ItemActivity extends Activity {
    private static final String TAG = "NPS:ItemActivity";
    private Context mContext;
	private Feed feed;
    private Item item;
	private ShareActionProvider mShareActionProvider;
    private MenuItem dictateButton;
    private MenuItem dictateDisabledButton;
    private MenuItem readButton;
    private MenuItem unreadButton;
    private MenuItem favoriteButton;
    private MenuItem notFavoriteButton;
    private MenuItem stopButton;
    private SharedPreferences preferences;
    private ItemRepository itemRepo;
    private FeedRepository feedRepo;
    private SongRepository songRepo;
    private ItemTtsManager ttsManager;
    private BroadcastReceiver receiver;
    private PlayerServiceStatus playerStatus;


	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        setTitle("");

	    setContentView(R.layout.activity_item_view);
        playerStatus = PlayerServiceStatus.getInstance();
        getNecessaryData();

        WebView mWebView = (WebView) findViewById(R.id.itemContent);
        ItemBlock.prepareWebView(mWebView, preferences.getString("pref_text_size", "100"), item.getLink(), item.getTitle(), feed.getTitle(), item.getContent(), item.getDateAdd());

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                broadcastUpdate(intent.getStringExtra(BroadcastConstants.ITEM_ACTIVITY_MESSAGE));
            }
        };

        launchTts();
	}

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(BroadcastConstants.ITEM_ACTIVITY));
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
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (ttsManager != null) {
            ttsManager.stop();
        }
        PreferencesHelper.setIntPreference(mContext, PreferenceConstants.ITEM_NOW_OPENED, 0);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (playerStatus.hasActiveSong() && !preferences.getBoolean("pref_dictations_only_article", false)) {
            launchItemsActivity();
        }
        super.onBackPressed();
    }

    private void openDB() {
        if (itemRepo == null) {
            itemRepo = new ItemRepository(mContext);
            feedRepo = new FeedRepository(mContext);
            songRepo = new SongRepository(mContext);
        }

        itemRepo.open();
        feedRepo.open();
        songRepo.open();
    }

    private void closeDB() {
        itemRepo.close();
        feedRepo.close();
        songRepo.close();
    }

    private void launchTts() {
        if (item.getLanguage() == null) {
            return;
        }

        ttsManager = new ItemTtsManager();
        ttsManager.prepareTts(mContext, item.getLanguage());
    }

    public void broadcastUpdate(String command) {
        if (command.equals(BroadcastConstants.COMMAND_A_ITEM_TTS_ACTIVE)) {
            dictateDisabledButton.setVisible(false);
            dictateButton.setVisible(true);
        } else if (command.equals(BroadcastConstants.COMMAND_A_ITEM_TTS_FINISHED)) {
            stopButton.setVisible(false);
            dictateButton.setVisible(true);
        }
    }

    /**
     * Get item and feed data
     */
    private void getNecessaryData() {
        openDB();
        Integer itemApiId = getItemApiId();
        PreferencesHelper.setIntPreference(mContext, PreferenceConstants.ITEM_NOW_OPENED, itemApiId);
        item = itemRepo.getItem(itemApiId);
        Integer feedId = PreferencesHelper.getIntPreference(mContext, PreferenceConstants.FEED_ID_ITEMS_LIST);
        feed = feedRepo.getFeed(feedId);
    }

    private Integer getItemApiId() {
        Integer itemApiId = getIntent().getIntExtra(ItemConstants.ITEM_API_ID, 0);
        if (!itemApiId.equals(0)) {
            return itemApiId;
        }

        return playerStatus.getItemApiId();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item, menu);

        //get menu items
        MenuItem shareItem = menu.findItem(R.id.buttonShare);
        dictateButton = menu.findItem(R.id.buttonDictate);
        dictateDisabledButton = menu.findItem(R.id.buttonDictateDisabled);
        readButton = menu.findItem(R.id.buttonRead);
        unreadButton = menu.findItem(R.id.buttonUnread);
        favoriteButton = menu.findItem(R.id.buttonFavorite);
        notFavoriteButton = menu.findItem(R.id.buttonNotFavorite);
        stopButton = menu.findItem(R.id.buttonStop);

        if (item.isStared()) {
            notFavoriteButton.setVisible(false);
            favoriteButton.setVisible(true);
        }

        //prepare share button
        mShareActionProvider = (ShareActionProvider)shareItem.getActionProvider();
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

    private void setLabel()
    {
        FragmentManager fm = ((Activity) mContext).getFragmentManager();
        LabelsDialog editNameDialog = new LabelsDialog(mContext, item.getApiId());
        editNameDialog.setRetainInstance(true);
        editNameDialog.show(fm, "fragment_select_label");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.buttonDictate:
                dictate();
                return true;
            case R.id.buttonLabel:
                setLabel();
                return true;
            case R.id.buttonRead:
                markUnread();
                return true;
            case R.id.buttonUnread:
                markRead();
                return true;
            case R.id.buttonFavorite:
                markNotFavorite();
                return true;
            case R.id.buttonNotFavorite:
                markFavorite();
                return true;
            case R.id.buttonShare:
                setShareIntent(createShareIntent());
                return true;
            case R.id.buttonStop:
                stopDictate();
                return true;
            case android.R.id.home:
                if (playerStatus.hasActiveSong() && !preferences.getBoolean("pref_dictations_only_article", false)) {
                    launchItemsActivity();
                }

                return super.onOptionsItemSelected(item);
        }

        return false;
    }

    private void markRead() {
        item.setIsUnread(false);
        readButton.setVisible(true);
        unreadButton.setVisible(false);
        itemRepo.readItem(item.getApiId(), false);
        songRepo.markAsPlayed(item.getApiId(), SongConstants.GRABBER_TYPE_TITLE, true);
    }

    private void markUnread() {
        item.setIsUnread(true);
        readButton.setVisible(false);
        unreadButton.setVisible(true);
        itemRepo.readItem(item.getApiId(), true);
        songRepo.markAsPlayed(item.getApiId(), SongConstants.GRABBER_TYPE_TITLE, false);
    }

    private void markFavorite() {
        item.setIsStared(true);
        notFavoriteButton.setVisible(false);
        favoriteButton.setVisible(true);
        StarItemTask task = new StarItemTask(mContext, item.getId(), true);
        task.execute();
    }

    private void markNotFavorite() {
        item.setIsStared(false);
        favoriteButton.setVisible(false);
        notFavoriteButton.setVisible(true);
        StarItemTask task = new StarItemTask(mContext, item.getId(), false);
        task.execute();
    }

    /** Dictate methods **/
    private void dictate() {
        ttsManager.dictate(item.getContent(), Float.parseFloat(preferences.getString("pref_dictation_speed", "1.5f")));
        dictateButton.setVisible(false);
        stopButton.setVisible(true);
    }

    private void stopDictate() {
        ttsManager.stopDictation();
        stopButton.setVisible(false);
        dictateButton.setVisible(true);
    }

    private void launchItemsActivity() {
        PreferencesHelper.setIntPreference(mContext, PreferenceConstants.FEED_ID_ITEMS_LIST, item.getFeedId());
        PreferencesHelper.setIntPreference(mContext, PreferenceConstants.MAIN_DRAWER_ITEM, MainActivityConstants.DRAWER_MAIN_ITEMS);
        Intent intent = new Intent(this, ItemsActivity.class);
        startActivity(intent);
    }
}