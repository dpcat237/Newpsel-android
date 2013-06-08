package com.dpcat237.nps.task;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dpcat237.nps.MainActivity;
import com.dpcat237.nps.R;
import com.dpcat237.nps.helper.ApiHelper;
import com.dpcat237.nps.helper.GenericHelper;

public class SignUpTask extends AsyncTask<Void, Integer, Void>{
	ApiHelper api;
	int progress_status;
	private Context mContext;
	private View mView;
	String username;
	String email;
	String password;
	String appKey;
	String checkApi = "";
	ProgressDialog dialog;
	
	public SignUpTask(Context context, View view) {
		api = new ApiHelper();
        mContext = context;
        mView = view;
        getData();
    }
	
	private void getData() {
		EditText usernameText = (EditText) mView.findViewById(R.id.txtUsername);
		EditText emailText = (EditText) mView.findViewById(R.id.txtEmail);
		EditText passwordText = (EditText) mView.findViewById(R.id.txtPassword);
		username = usernameText.getText().toString();
		email = emailText.getText().toString();
		String pwd = passwordText.getText().toString();
		try {
			password = GenericHelper.sha1SignUpPassword(pwd);
		} catch (NoSuchAlgorithmException e) {
			Log.e("LoginTask - getData","Error", e);
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			Log.e("LoginTask - getData","Error", e);
			e.printStackTrace();
		}
		appKey = GenericHelper.generateKey(mContext);
	}
    
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = ProgressDialog.show(mContext, "Loading", "Please wait...", true);
	}
     
	@Override
	protected Void doInBackground(Void... params) {
		checkApi = ApiHelper.doSignUp(username, email, password, appKey);

		return null;
	}
 
	@Override
	protected void onProgressUpdate(Integer... values) {
	  super.onProgressUpdate(values);
	}
  
	@Override
 	protected void onPostExecute(Void result) {
		dialog.cancel();
		
		if (checkApi.equals("100")) {
			GenericHelper.doLogin(mContext);
			mContext.startActivity(new Intent(mContext, MainActivity.class));
			((Activity) mContext).finish();	
		} else if (checkApi.equals("304")) {
			Toast.makeText(mContext, R.string.error_304, Toast.LENGTH_SHORT).show();
		} else if (checkApi.equals("305")) {
			Toast.makeText(mContext, R.string.error_305, Toast.LENGTH_SHORT).show();
		} else if (checkApi.equals("99") || checkApi.equals("310")) {
			Toast.makeText(mContext, R.string.error_310, Toast.LENGTH_SHORT).show();
		}
	}
}
