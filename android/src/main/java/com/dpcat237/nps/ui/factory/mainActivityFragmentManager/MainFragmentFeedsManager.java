package com.dpcat237.nps.ui.factory.mainActivityFragmentManager;

import android.widget.ArrayAdapter;

import com.dpcat237.nps.R;
import com.dpcat237.nps.common.model.Feed;
import com.dpcat237.nps.constant.MainActivityConstants;

public class MainFragmentFeedsManager extends MainFragmentFeeds {
    private static final String TAG = "NPS:MainFragmentFeedsManager";
    private ArrayAdapter<Feed> mAdapter;


    protected void initializeAdapter() { }

    protected void setItems() {
        getItems();
        mAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, items);
    }

    protected void setAdapter() {
        listView.setAdapter(mAdapter);
    }

    protected void setOnClickListener() { }

    protected Integer countItems() {
        return mAdapter.getCount();
    }

    protected void setCreatorType() {
        managerType = MainActivityConstants.DRAWER_MAIN_MANAGE_FEEDS;
    }

    protected void setTitle() {
        mActivity.setTitle(mActivity.getString(R.string.drawer_manage_feeds));
    }

    protected void getItems() {
        items = feedRepo.getAllFeeds();
    }
}