package com.dpcat237.nps.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ShareActionProvider;

import com.dpcat237.nps.R;
import com.dpcat237.nps.common.model.Label;
import com.dpcat237.nps.common.model.LaterItem;
import com.dpcat237.nps.constant.ItemConstants;
import com.dpcat237.nps.constant.PreferenceConstants;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.database.repository.LaterItemRepository;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.ui.block.ItemBlock;
import com.dpcat237.nps.ui.dialog.LabelsDialog;

@SuppressLint("SimpleDateFormat")
public class LaterItemActivity extends Activity {
    private static final String TAG = "NPS:LaterItemActivity";
    private Context mContext;
	private Label label;
    private LaterItem item;
	private ShareActionProvider mShareActionProvider;
    private MenuItem readButton;
    private MenuItem unreadButton;
    private LaterItemRepository itemRepo;
    private LabelRepository labelRepo;


	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        setTitle("");

	    setContentView(R.layout.activity_item_view);
        getNecessaryData();

        WebView mWebView = (WebView) findViewById(R.id.itemContent);
        ItemBlock.prepareWebView(mWebView, preferences.getString("pref_text_size", "100"), item.getLink(), item.getTitle(), label.getName(), item.getContent(), item.getDateAdd());
	}

    private void openDB() {
        itemRepo = new LaterItemRepository(mContext);
        labelRepo = new LabelRepository(mContext);
        itemRepo.open();
        labelRepo.open();
    }

    private void closeDB() {
        itemRepo.close();
        labelRepo.close();
    }

    /**
     * Get item and feed data
     */
    private void getNecessaryData() {
        openDB();
        Integer itemApiId = getIntent().getIntExtra(ItemConstants.ITEM_API_ID, 0);
        PreferencesHelper.setIntPreference(mContext, PreferenceConstants.SAVED_ITEM_NOW_OPENED, itemApiId);
        item = itemRepo.getItem(itemApiId);
        label = labelRepo.getLabel(PreferencesHelper.getIntPreference(mContext, PreferenceConstants.LABEL_ID_ITEMS_LIST));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item, menu);

        //get menu items
        MenuItem shareItem = menu.findItem(R.id.buttonShare);
        readButton = menu.findItem(R.id.buttonRead);
        unreadButton = menu.findItem(R.id.buttonUnread);

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
        LabelsDialog editNameDialog = new LabelsDialog(mContext, item.getItemApiId());
        editNameDialog.setRetainInstance(true);
        editNameDialog.show(fm, "fragment_select_label");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        }

        return false;
    }

    private void markRead() {
        item.setIsUnread(false);
        readButton.setVisible(true);
        unreadButton.setVisible(false);
        itemRepo.readItem(item.getApiId(), false);
    }

    private void markUnread() {
        item.setIsUnread(true);
        readButton.setVisible(false);
        unreadButton.setVisible(true);
        itemRepo.readItem(item.getApiId(), true);
    }

    @Override
    public void onDestroy() {
        PreferencesHelper.setIntPreference(mContext, PreferenceConstants.SAVED_ITEM_NOW_OPENED, 0);
        closeDB();
        super.onDestroy();
    }
}