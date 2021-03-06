package com.dpcat237.nps.ui.factory.mainActivityFragmentManager;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.dpcat237.nps.common.model.Feed;
import com.dpcat237.nps.constant.PreferenceConstants;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.ui.activity.ItemsActivity;
import com.dpcat237.nps.ui.adapter.FeedsAdapter;

import java.util.ArrayList;

public abstract class MainFragmentFeeds extends MainFragmentManager {
    private static final String TAG = "NPS:MainFragmentFeeds";
    protected FeedRepository feedRepo;
    private FeedsAdapter mAdapter;
    protected ArrayList<Feed> items;


    public void finish() {
        super.finish();
        feedRepo.close();
    }

    protected void openDB() {
        feedRepo = new FeedRepository(mActivity);
        feedRepo.open();
    }

    protected void initializeAdapter() {
        mAdapter = new FeedsAdapter(mActivity);
    }

    protected void setItems() {
        getItems();
        mAdapter.addToDataset(items);
    }

    protected void setAdapter() {
        listView.setAdapter(mAdapter);
    }

    protected void setOnClickListener() {
        if (!items.isEmpty()) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mAdapter.getCount() > 0) {
                        Feed feed = mAdapter.getItem(position);
                        Integer feedId = feed.getApiId();
                        showItems(feedId);
                    }
                }
            });
        }
    }

    public void showItems(Integer feedId) {
        PreferencesHelper.setIntPreference(mActivity, PreferenceConstants.FEED_ID_ITEMS_LIST, feedId);
        Intent intent = new Intent(mActivity, ItemsActivity.class);
        mActivity.startActivity(intent);
    }

    protected Integer countItems() {
        return mAdapter.getCount();
    }

    //abstract methods
    abstract protected void setTitle();
    abstract protected void getItems();
}