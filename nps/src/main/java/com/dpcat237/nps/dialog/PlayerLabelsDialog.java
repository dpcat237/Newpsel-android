package com.dpcat237.nps.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dpcat237.nps.R;
import com.dpcat237.nps.constant.ItemConstants;
import com.dpcat237.nps.constant.PlayerConstants;
import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.model.Label;
import com.dpcat237.nps.repository.ItemRepository;
import com.dpcat237.nps.repository.LabelRepository;
import com.dpcat237.nps.service.PlayerService;
import com.dpcat237.nps.task.SetLabelTask;

import java.util.ArrayList;

public class PlayerLabelsDialog extends Activity {

	private Context mContext;
    private Item item;
    private ArrayAdapter<Label> mAdapter;
    private static final String TAG = "NPS:PlayerLabelsDialog";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
        PlayerService.pause(mContext, PlayerConstants.PAUSE_NOTIFICATION);
        setContentView(R.layout.dialog_shared_labels);
		Intent intent = getIntent();
        ItemRepository itemRepo = new ItemRepository(this);
        itemRepo.open();
        LabelRepository labelRepo = new LabelRepository(this);
        labelRepo.open();

        Integer itemId = intent.getIntExtra(ItemConstants.ITEM_ID, 0);
        item = itemRepo.getItem(itemId);

        ListView listView = (ListView) findViewById(R.id.labelsList);
		ArrayList<Label> values = labelRepo.getAllLabels();
        mAdapter = new ArrayAdapter<Label>(this, R.layout.dialog_labels_list, values);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Label label = mAdapter.getItem(position);
                setLabel(item, label);
			}
		});

		Intent result = new Intent("com.example.RESULT_ACTION");
		setResult(Activity.RESULT_OK, result);
	}

    public void setLabel(Item item, Label label) {
        SetLabelTask task = new SetLabelTask(mContext, item, label);
        PlayerService.play(mContext);
        task.execute();
        finish();
    }
}