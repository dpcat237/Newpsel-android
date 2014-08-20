package com.dpcat237.nps.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.task.SignInTask;
import com.dpcat237.nps.helper.AccountHelper;
import com.dpcat237.nps.helper.ConnectionHelper;

public class SignInActivity extends Activity {
	View mView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mView = this.findViewById(android.R.id.content).getRootView();
		
		setContentView(R.layout.activity_sign_in);

        EditText textEmail = (EditText) mView.findViewById(R.id.txtEmail);
        textEmail.setText(AccountHelper.getEmail(this));
	}
	
	public void doLogin(View view) {
		if (ConnectionHelper.hasConnection(this)) {
			SignInTask task = new SignInTask(this, mView);
			task.execute();
		} else {
			Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
		}
	}

    public void goChangePassword(View view) {
        Intent intent = new Intent(this, RecoverPasswordActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}