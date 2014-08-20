package com.dpcat237.nps.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.task.ChangePasswordTask;
import com.dpcat237.nps.behavior.task.SignInTask;
import com.dpcat237.nps.helper.AccountHelper;
import com.dpcat237.nps.helper.ConnectionHelper;
import com.dpcat237.nps.helper.NotificationHelper;

public class RecoverPasswordActivity extends Activity {
    private Context mContext;
	private View mView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mContext = this;
		mView = this.findViewById(android.R.id.content).getRootView();
		
		setContentView(R.layout.activity_recover_password);

        EditText textEmail = (EditText) mView.findViewById(R.id.txtEmail);
        textEmail.setText(AccountHelper.getEmail(this));
	}
	
	public void requireChangePassword(View view) {
		if (ConnectionHelper.hasConnection(this)) {
            ChangePasswordTask task = new ChangePasswordTask(this, mView);
			task.execute();
		} else {
            NotificationHelper.showSimpleToast(mContext, mContext.getString(R.string.error_connection));
		}
	}
}