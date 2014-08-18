package com.dpcat237.nps.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.task.SignUpTask;
import com.dpcat237.nps.helper.ConnectionHelper;
import com.dpcat237.nps.helper.LoginHelper;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class SignUpActivity extends Activity {
	View mView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = this.findViewById(android.R.id.content).getRootView();
		
		setContentView(R.layout.activity_sign_up);
	}
	
	public void doSignUp(View view) {
		if (checkInputs()) {
			if (ConnectionHelper.hasConnection(this)) {
                String email = view.findViewById(R.id.txtEmail).toString();
                String password = view.findViewById(R.id.txtPassword).toString();
                try {
                    password = LoginHelper.sha1SignUpPassword(password);
                } catch (NoSuchAlgorithmException e) {
                    Log.e("LoginTask - getData", "Error", e);
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    Log.e("LoginTask - getData","Error", e);
                    e.printStackTrace();
                }

				SignUpTask task = new SignUpTask(this, mView, email, password);
				task.execute();
			} else {
				Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public Boolean checkInputs() {
		Boolean check = true;
		EditText email = (EditText) mView.findViewById(R.id.txtEmail);
		EditText password = (EditText) mView.findViewById(R.id.txtPassword);
		
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