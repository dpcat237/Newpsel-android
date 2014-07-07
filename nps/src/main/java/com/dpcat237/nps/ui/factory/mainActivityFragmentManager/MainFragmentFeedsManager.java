package com.dpcat237.nps.ui.factory.mainActivityFragmentManager;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.database.repository.FeedRepository;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.model.Feed;
import com.dpcat237.nps.ui.activity.ItemsActivity;
import com.dpcat237.nps.ui.adapter.FeedsAdapter;

import java.util.ArrayList;

public abstract class MainFragmentFeedsManager extends MainFragmentManager {
    private static final String TAG = "NPS:MainFragmentFeedsManager";
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
        PreferencesHelper.setSelectedFeed(mActivity, feedId);
        Intent intent = new Intent(mActivity, ItemsActivity.class);
        mActivity.startActivity(intent);
    }

    protected void showToast() {
        if (mAdapter.getCount() < 1) {
            Toast.makeText(mActivity, R.string.no_new_articles, Toast.LENGTH_SHORT).show();
        }
    }

    //abstract methods
    abstract protected void setTitle();
    abstract protected void getItems();
}