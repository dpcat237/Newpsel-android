package com.dpcat237.nps.ui.factory.mainActivityFragmentManager;


import android.widget.ArrayAdapter;

import com.dpcat237.nps.R;
import com.dpcat237.nps.common.model.Label;
import com.dpcat237.nps.constant.MainActivityConstants;

public class MainFragmentLabelsManager extends MainFragmentLabels {
    protected static final String TAG = "NPS:MainFragmentLabelsManager";
    protected ArrayAdapter<Label> mAdapter;


    protected void initializeAdapter() { }

    protected void setItems() {
        getItems();
        mAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_list_item_1, labels);
    }

    protected void setAdapter() {
        listView.setAdapter(mAdapter);
    }

    protected void setOnClickListener() { }

    protected Integer countItems() {
        return mAdapter.getCount();
    }

    protected void setCreatorType() {
        managerType = MainActivityConstants.DRAWER_MAIN_MANAGE_LABELS;
    }

    protected void setTitle() {
        mActivity.setTitle(mActivity.getString(R.string.drawer_manage_labels));
    }

    protected void getItems() {
        labels = labelRepo.getAllLabels();
    }
}