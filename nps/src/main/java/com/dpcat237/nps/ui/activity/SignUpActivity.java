package com.dpcat237.nps.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.task.SignUpTask;
import com.dpcat237.nps.helper.ConnectionHelper;

public class SignUpActivity extends Activity {
	View mView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = this.findViewById(android.R.id.content).getRootView();
		
		setContentView(R.layout.activity_sign_up);
	}
	
	public void doSignUp(View view) {
		if (checkInputs(view)) {
			if (ConnectionHelper.hasConnection(this)) {
				SignUpTask task = new SignUpTask(this, mView);
				task.execute();
			} else {
				Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public Boolean checkInputs(View view) {
		Boolean check = true;
		EditText userName = (EditText) mView.findViewById(R.id.txtUsername);
		EditText email = (EditText) mView.findViewById(R.id.txtEmail);
		EditText password = (EditText) mView.findViewById(R.id.txtPassword);
		
		if( userName.getText().toString().trim().equals("")) {    
		   userName.setError(getString(R.string.error_311));
		   check = false;
		}
		
		if( email.getText().toString().trim().equals("")) {    
			email.setError(getString(R.string.error_312));
			check = false;
		}
		
		if( password.getText().toString().trim().equals("")) {
			password.setError(getString(R.string.error_313));
		   check = false;
		}
		
		return check;
	}
}