package com.dpcat237.nps.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.helper.GenericHelper;
import com.dpcat237.nps.task.LoginTask;

public class SignInActivity extends Activity {
	View mView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = this.findViewById(android.R.id.content).getRootView();
		
		setContentView(R.layout.activity_sign_in);
	}
	
	public void doLogin(View view) {
		if (GenericHelper.hasConnection(this)) {
			LoginTask task = new LoginTask(this, mView);
			task.execute();
		} else {
			Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
		}
	}
}