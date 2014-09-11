package com.dpcat237.nps.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.task.SignUpTask;
import com.dpcat237.nps.helper.AccountHelper;
import com.dpcat237.nps.helper.ConnectionHelper;
import com.dpcat237.nps.helper.DisplayHelper;
import com.dpcat237.nps.helper.NotificationHelper;
import com.dpcat237.nps.helper.StringHelper;

public class SignUpActivity extends Activity {
    private static final String TAG = "NPS:SignUpActivity";
    private Context mContext;
    private View mView;
    private EditText textEmail;
    private EditText txtPassword;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mContext = this;
		mView = this.findViewById(android.R.id.content).getRootView();
		
		setContentView(R.layout.activity_sign_up);

        textEmail = (EditText) mView.findViewById(R.id.txtEmail);
        textEmail.setText(AccountHelper.getEmail(mContext));
        txtPassword = (EditText) mView.findViewById(R.id.txtPassword);
        CheckBox showPassword = (CheckBox) mView.findViewById(R.id.showPassword);
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    txtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    txtPassword.setInputType(129);
                }
            }
        });
	}
	
	public void doSignUp(View view) {
        DisplayHelper.hideKeyboard(mContext, mView);
        if (!StringHelper.isUserDataValid(mContext, mView)) {
            return;
        }

        if (!ConnectionHelper.hasConnection(mContext)) {
            NotificationHelper.showSimpleToast(mContext, mContext.getString(R.string.error_connection));

            return;
        }

        String email = textEmail.getText().toString();
        String password = txtPassword.getText().toString();
        password = StringHelper.getPassword(password);

        SignUpTask task = new SignUpTask(mContext, email, password);
        task.execute();
	}

    @Override
    public boolean onKeyUp (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            doSignUp(mView);

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