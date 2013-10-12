package com.dpcat237.nps.intent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.dpcat237.nps.model.Shared;
import com.dpcat237.nps.repository.SharedRepository;

public class ShareReceiver extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		SharedRepository sharedRepo = new SharedRepository(this);
		sharedRepo.open();
		
		Shared shared = new Shared();
		shared.setTitle(extras.getString(Intent.EXTRA_SUBJECT));
		shared.setText(extras.getString(Intent.EXTRA_TEXT));
		
		if (sharedRepo.addShared(shared)) {
			Toast.makeText(this, "Page successfully saved.", Toast.LENGTH_LONG).show();			
		} else {
			Toast.makeText(this, "Page already exists.", Toast.LENGTH_LONG).show();			
		}
		
		Intent result = new Intent("com.example.RESULT_ACTION");
		setResult(Activity.RESULT_OK, result);
		finish();
	}
}