package com.dpcat237.nps.ui.factory.mainActivityFragmentManager;


import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.dpcat237.nps.R;
import com.dpcat237.nps.constant.MainActivityConstants;
import com.dpcat237.nps.constant.SyncConstants;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.common.model.Label;
import com.dpcat237.nps.ui.activity.LaterItemsActivity;
import com.dpcat237.nps.ui.adapter.LabelsAdapter;

import java.util.ArrayList;

public class MainFragmentLabelsManager extends MainFragmentManager {
    private static final String TAG = "NPS:MainFragmentLabelsManager";
    protected LabelRepository labelRepo;
    private LabelsAdapter mAdapter;
    protected ArrayList<Label> labels;


    public void finish() {
        super.finish();
        labelRepo.close();
    }

    protected void openDB() {
        labelRepo = new LabelRepository(mActivity);
        labelRepo.open();
    }

    protected void initializeAdapter() {
        mAdapter = new LabelsAdapter(mActivity);

        //activate sync of later items
        PreferencesHelper.setSyncRequired(mActivity, SyncConstants.SYNC_LATER_ITEMS, true);
    }

    protected void setItems() {
        getItems();
        mAdapter.addToDataset(labels);
    }

    protected void setAdapter() {
        listView.setAdapter(mAdapter);
    }

    protected void setOnClickListener() {
        if (labels.isEmpty()) {
            return;
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter.getCount() < 1) {
                    return;
                }

                Label label = mAdapter.getItem(position);
                showNextPage(label);
            }
        });
    }

    protected Integer countItems() {
        return mAdapter.getCount();
    }

    protected void setCreatorType() {
        managerType = MainActivityConstants.DRAWER_MAIN_DICTATE_ITEMS;
    }

    protected void setTitle() {
        mActivity.setTitle(mActivity.getString(R.string.drawer_later_item));
    }

    protected void getItems() {
        labels = labelRepo.getForListUnread();
    }

    protected void showNextPage(Label label) {
        PreferencesHelper.setMainListId(mActivity, label.getApiId());
        Intent intent = new Intent(mActivity, LaterItemsActivity.class);
        mActivity.startActivity(intent);
    }
}