package com.dpcat237.nps.ui.dialog;

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
import com.dpcat237.nps.behavior.service.PlayerService;
import com.dpcat237.nps.behavior.service.valueObject.PlayerServiceStatus;
import com.dpcat237.nps.behavior.task.SetLabelTask;
import com.dpcat237.nps.database.repository.ItemRepository;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.helper.DisplayHelper;
import com.dpcat237.nps.common.model.Label;

import java.util.ArrayList;

public class PlayerLabelsDialog extends Activity {
    private static final String TAG = "NPS:PlayerLabelsDialog";
	private Context mContext;
    private Integer itemApiId;
    private ArrayAdapter<Label> mAdapter;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
        PlayerServiceStatus playerStatus = PlayerServiceStatus.getInstance();
        if (!playerStatus.hasActiveSong()) {
            finish();
        }

        PlayerService.playpause(mContext);
        if (DisplayHelper.isTablet(mContext)) {
            setContentView(R.layout.dialog_shared_labels_tablet);
        } else {
            setContentView(R.layout.dialog_shared_labels_mobile);
        }

        ItemRepository itemRepo = new ItemRepository(this);
        itemRepo.open();
        LabelRepository labelRepo = new LabelRepository(this);
        labelRepo.open();

        itemApiId = playerStatus.getCurrentSong().getItemApiId();
        ListView listView = (ListView) findViewById(R.id.labelsList);
		ArrayList<Label> values = labelRepo.getAllLabels();
        mAdapter = new ArrayAdapter<Label>(this, R.layout.dialog_labels_list_row, values);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Label label = mAdapter.getItem(position);
                setLabel(itemApiId, label);
			}
		});

		Intent result = new Intent("com.example.RESULT_ACTION");
		setResult(Activity.RESULT_OK, result);
	}


    public void setLabel(Integer itemApiId, Label label) {
        SetLabelTask task = new SetLabelTask(mContext, itemApiId, label);
        task.execute();
        finish();
    }

    @Override
    protected void onStop() {
        PlayerService.playpause(mContext);
        super.onStop();
    }
}