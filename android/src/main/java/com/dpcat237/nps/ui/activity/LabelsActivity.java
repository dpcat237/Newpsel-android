package com.dpcat237.nps.ui.activity;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.dpcat237.nps.R;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.helper.ConnectionHelper;
import com.dpcat237.nps.model.Label;

import java.util.ArrayList;

public class LabelsActivity extends ListActivity {
	private LabelRepository labelRepo;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_label_list);
		mContext = this;
		labelRepo = new LabelRepository(this);
		labelRepo.open();
		
		showList();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		labelRepo.open();
		showList();
	}

	@Override
	protected void onPause() {
		super.onPause();
		labelRepo.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.label_list, menu);
        MenuItem createLabelButton = menu.findItem(R.id.buttonCreateLabel);
        if (!ConnectionHelper.hasConnection(mContext)) {
            createLabelButton.setVisible(false);
        }

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
		    case R.id.buttonCreateLabel:
		    	Intent intent = new Intent(this, CreateLabelActivity.class);
				startActivity(intent);
		        return true;
	    }
		return false;
	}
	
	public void showList()
	{
		ArrayList<Label> values = labelRepo.getAllLabels();
		ArrayAdapter<Label> adapter = new ArrayAdapter<Label>(this, android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}
}