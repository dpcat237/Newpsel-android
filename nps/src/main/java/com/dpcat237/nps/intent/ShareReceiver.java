package com.dpcat237.nps.intent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.helper.GenericHelper;
import com.dpcat237.nps.model.Label;
import com.dpcat237.nps.repository.LabelRepository;
import com.dpcat237.nps.repository.SharedRepository;
import com.dpcat237.nps.task.SaveSharedTask;
import com.dpcat237.nps.task.SendSharedTask;

import java.util.ArrayList;

public class ShareReceiver extends Activity {

	Context mContext;
    private LabelRepository labelRepo;
    ListView listView;
    private Bundle extras;
    ArrayAdapter<Label> mAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		Intent intent = getIntent();
		extras = intent.getExtras();
		SharedRepository sharedRepo = new SharedRepository(this);
		sharedRepo.open();
		setContentView(R.layout.shared_labels);
		
		labelRepo = new LabelRepository(this);
		labelRepo.open();

		listView = (ListView) findViewById(R.id.labelsList);
		ArrayList<Label> values = labelRepo.getAllLabels();
        mAdapter = new ArrayAdapter<Label>(this, android.R.layout.simple_list_item_1, values);
		listView.setAdapter(mAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Label label = mAdapter.getItem(position);
                savedShared(label);
			}
		});

		Intent result = new Intent("com.example.RESULT_ACTION");
		setResult(Activity.RESULT_OK, result);
	}

    public void savedShared(Label label) {
        Toast.makeText(mContext, "Page successfully saved to "+label.getName()+" label.", Toast.LENGTH_LONG).show();
        SaveSharedTask taskSave = new SaveSharedTask(mContext, label.getApiId(), extras.getString(Intent.EXTRA_SUBJECT), extras.getString(Intent.EXTRA_TEXT));
        taskSave.execute();

        if (GenericHelper.hasConnection(this)) {
            SendSharedTask taskSend = new SendSharedTask(this);
            taskSend.execute();
        }

        ((Activity) mContext).finish();
    }


}