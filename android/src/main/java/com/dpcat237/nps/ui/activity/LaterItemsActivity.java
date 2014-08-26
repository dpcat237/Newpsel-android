package com.dpcat237.nps.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.dpcat237.nps.R;
import com.dpcat237.nps.constant.ItemConstants;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.database.repository.LaterItemRepository;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.common.model.Label;
import com.dpcat237.nps.common.model.LaterItem;
import com.dpcat237.nps.ui.adapter.LaterItemsAdapter;

public class LaterItemsActivity extends Activity {
    private static final String TAG = "NPS:LaterItemsActivity";
    private Context mContext;
	private LaterItemRepository laterItemRepo;
    private LabelRepository labelRepo;
	private Integer labelId;
    private LaterItemsAdapter mAdapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item_list);
		mContext = this;
        setTitle(mContext.getString(R.string.drawer_later_item));

        openDB();
        labelId = PreferencesHelper.getMainListId(mContext);
	    Label label = labelRepo.getLabel(labelId);

	    TextView txtFeedTitle= (TextView) this.findViewById(R.id.feedTitle);
	    txtFeedTitle.setText(label.getName());

        ListView listView = (ListView) findViewById(R.id.itemsList);
	    mAdapter = new LaterItemsAdapter(mContext);
        mAdapter.addToDataset(laterItemRepo.getForList(labelId));
	    listView.setAdapter(mAdapter);
	    registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mAdapter.getCount() > 0) {
					LaterItem item = mAdapter.getItem(position);
					showItem(item.getApiId());

					if (item.isUnread()) {
						markReadItem(item.getApiId());
					}
				}
			}
		});
	}

    private void openDB() {
        laterItemRepo = new LaterItemRepository(mContext);
        labelRepo = new LabelRepository(mContext);
        laterItemRepo.open();
        labelRepo.open();
    }

    private void closeDB() {
        laterItemRepo.close();
        labelRepo.close();
    }

	@Override
	public void onResume() {
	    super.onResume();

        openDB();
        mAdapter.updateList(laterItemRepo.getForList(labelId));
	}

	@Override
	protected void onPause() {
		super.onPause();
        closeDB();
	}

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mAdapter.finish();
        super.onDestroy();
    }

	public void markReadItem(Integer apiId) {
		laterItemRepo.readItem(apiId, false);
	}

	public void showItem(Integer apiId) {
		Intent intent = new Intent(this, LaterItemActivity.class);
		intent.putExtra(ItemConstants.ITEM_API_ID, apiId);
        PreferencesHelper.setCurrentItemApiId(mContext, 0);
		startActivity(intent);
	}
}