package com.dpcat237.nps.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.task.ChangePasswordTask;
import com.dpcat237.nps.helper.AccountHelper;
import com.dpcat237.nps.helper.ConnectionHelper;
import com.dpcat237.nps.helper.DisplayHelper;
import com.dpcat237.nps.helper.NotificationHelper;
import com.dpcat237.nps.helper.StringHelper;

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

    /**
     * Valid data and send requirement password change
     *
     * @param view View
     */
	public void requireChangePassword(View view) {
        DisplayHelper.hideKeyboard(mContext, mView);
        if (!StringHelper.isEmailValid(mContext, mView)) {
            return;
        }

        if (!ConnectionHelper.hasConnection(mContext)) {
            NotificationHelper.showSimpleToast(mContext, mContext.getString(R.string.error_connection));

            return;
        }

        DisplayHelper.hideKeyboard(mContext, mView);
        ChangePasswordTask task = new ChangePasswordTask(this, mView);
        task.execute();
	}

    /**
     * Submit when enter key is pressed
     *
     * @param keyCode int
     * @param event   KeyEvent
     *
     * @return boolean
     */
    @Override
    public boolean onKeyUp (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            requireChangePassword(mView);

            return true;
        }

        return super.onKeyUp(keyCode, event);
    }
}