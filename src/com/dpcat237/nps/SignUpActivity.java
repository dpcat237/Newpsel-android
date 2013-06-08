package com.dpcat237.nps;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dpcat237.nps.helper.GenericHelper;
import com.dpcat237.nps.task.SignUpTask;

public class SignUpActivity extends Activity {
	View mView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = this.findViewById(android.R.id.content).getRootView();
		
		setContentView(R.layout.sign_up);
	}
	
	public void doSignUp(View view) {
		if (GenericHelper.hasConnection(this)) {
			SignUpTask task = new SignUpTask(this, mView);
			task.execute();
		} else {
			Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
		}
	}
}