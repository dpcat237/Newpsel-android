package com.dpcat237.nps.ui.factory.mainActivityFragmentManager;

import android.view.View;
import android.widget.AdapterView;

import com.dpcat237.nps.common.model.ListItem;
import com.dpcat237.nps.database.repository.DictateItemRepository;
import com.dpcat237.nps.ui.adapter.SavedItemsAdapter;

import java.util.ArrayList;

public abstract class MainFragmentItems extends MainFragmentManager {
    private static final String TAG = "NPS:MainFragmentItems";
    protected DictateItemRepository dictateRepo;
    private SavedItemsAdapter mAdapter;
    protected ArrayList<ListItem> items;


    public void finish() {
        super.finish();
        dictateRepo.close();
    }

    protected void openDB() {
        dictateRepo = new DictateItemRepository(mActivity);
        dictateRepo.open();
    }

    protected void initializeAdapter() {
        mAdapter = new SavedItemsAdapter(mActivity);
    }

    protected void setItems() {
        getItems();
        mAdapter.addToDataset(items);
    }

    protected void setAdapter() {
        listView.setAdapter(mAdapter);
    }

    protected void setOnClickListener() {
        if (items.isEmpty()) {
            return;
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter.getCount() < 1) {
                    return;
                }

                ListItem item = mAdapter.getItem(position);
                showNextPage(item);
            }
        });
    }

    protected Integer countItems() {
        return mAdapter.getCount();
    }

    //abstract methods
    abstract protected void setTitle();
    abstract protected void getItems();
    abstract protected void showNextPage(ListItem itemId);
}