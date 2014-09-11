package com.dpcat237.nps.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.task.SignInTask;
import com.dpcat237.nps.helper.AccountHelper;
import com.dpcat237.nps.helper.ConnectionHelper;
import com.dpcat237.nps.helper.DisplayHelper;
import com.dpcat237.nps.helper.NotificationHelper;
import com.dpcat237.nps.helper.StringHelper;

public class SignInActivity extends Activity {
    private static final String TAG = "NPS:SignInActivity";
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
        DisplayHelper.hideKeyboard(mContext, mView);
        if (!StringHelper.isUserDataValid(mContext, mView)) {
            return;
        }

        if (!ConnectionHelper.hasConnection(mContext)) {
            NotificationHelper.showSimpleToast(mContext, mContext.getString(R.string.error_connection));

            return;
        }

        String email = ((EditText) mView.findViewById(R.id.txtEmail)).getText().toString();
        String password = ((EditText) mView.findViewById(R.id.txtPassword)).getText().toString();
        password = StringHelper.getPassword(password);

        SignInTask task = new SignInTask(mContext, email, password);
        task.execute();
	}

    public void goChangePassword(View view) {
        Intent intent = new Intent(mContext, RecoverPasswordActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onKeyUp (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            doLogin(mView);

            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(mContext, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}