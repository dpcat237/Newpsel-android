package com.dpcat237.nps.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.task.SignInTask;
import com.dpcat237.nps.helper.AccountHelper;
import com.dpcat237.nps.helper.ConnectionHelper;
import com.dpcat237.nps.helper.DisplayHelper;

public class SignInActivity extends Activity {
    private Context mContext;
    private View mView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mContext = this;
		mView = this.findViewById(android.R.id.content).getRootView();
		
		setContentView(R.layout.activity_sign_in);

        EditText textEmail = (EditText) mView.findViewById(R.id.txtEmail);
        textEmail.setText(AccountHelper.getEmail(mContext));
	}
	
	public void doLogin(View view) {
		if (ConnectionHelper.hasConnection(mContext)) {
            DisplayHelper.hideKeyboard(mContext, mView);
			SignInTask task = new SignInTask(mContext, mView);
			task.execute();
		} else {
			Toast.makeText(mContext, R.string.error_connection, Toast.LENGTH_SHORT).show();
		}
	}

    public void goChangePassword(View view) {
        Intent intent = new Intent(mContext, RecoverPasswordActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(mContext, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}