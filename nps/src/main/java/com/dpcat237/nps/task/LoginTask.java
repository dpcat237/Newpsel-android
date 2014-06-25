package com.dpcat237.nps.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dpcat237.nps.activity.MainActivity;
import com.dpcat237.nps.R;
import com.dpcat237.nps.helper.ApiHelper;
import com.dpcat237.nps.helper.GenericHelper;
import com.dpcat237.nps.helper.LoginHelper;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class LoginTask extends AsyncTask<Void, Integer, Void>{
	ApiHelper api;
	int progress_status;
	private Context mContext;
	private View mView;
	String username;
	String password;
	String appKey;
	Boolean checkLogin = false;
	ProgressDialog dialog;
	
	public LoginTask(Context context, View view) {
		api = new ApiHelper();
        mContext = context;
        mView = view;
        getData();
    }
	
	private void getData() {
		EditText usernameText = (EditText) mView.findViewById(R.id.txtUsername);
		EditText passwordText = (EditText) mView.findViewById(R.id.txtPassword);
		username = usernameText.getText().toString();
		String pwd = passwordText.getText().toString();
		try {
			password = LoginHelper.sha1LoginPassword(pwd);
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
		Map<String, Object> result = null;
		Boolean error = false;
		result = ApiHelper.doLogin(username, password, appKey);
		error = (Boolean) result.get("error");
		
		if (!error) {
			checkLogin = true;
		}

		return null;
	}
 
	@Override
	protected void onProgressUpdate(Integer... values) {
	  super.onProgressUpdate(values);
	}
  
	@Override
 	protected void onPostExecute(Void result) {
		dialog.cancel();
		
		if (checkLogin) {
			LoginHelper.doLogin(mContext);
			mContext.startActivity(new Intent(mContext, MainActivity.class));



			((Activity) mContext).finish();
		} else {
			Toast.makeText(mContext, R.string.try_later, Toast.LENGTH_SHORT).show();
		}
	}
}
