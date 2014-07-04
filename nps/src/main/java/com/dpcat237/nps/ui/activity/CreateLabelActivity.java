package com.dpcat237.nps.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.dpcat237.nps.R;
import com.dpcat237.nps.database.repository.LabelRepository;

public class CreateLabelActivity extends Activity {
	View mView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = this.findViewById(android.R.id.content).getRootView();
		
		setContentView(R.layout.activity_label_create);
	}
	
	public void createLabel(View view) {
		LabelRepository labelRepo = new LabelRepository(this);
		labelRepo.open();
		
		EditText nameText = (EditText) mView.findViewById(R.id.txtName);
		String name = nameText.getText().toString();
		labelRepo.createLabel(name);
		finish();
	}
}