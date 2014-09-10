package com.dpcat237.nps.behavior.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.factory.ApiFactoryManager;
import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.helper.LoginHelper;
import com.dpcat237.nps.helper.NotificationHelper;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.ui.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SignInTask extends AsyncTask<Void, Integer, Void>{
    private static final String TAG = "NPS:SignInTask";
	private Context mContext;
	private View mView;
    private String email;
    private String password;
    private String appKey;
    private Boolean checkLogin = false;
    private ProgressDialog dialog;
    private ApiFactoryManager apiFactoryManager;
	private String errorMessage;

	public SignInTask(Context context, View view) {
        mContext = context;
        mView = view;
        getData();
    }
	
	private void getData() {
		EditText emailText = (EditText) mView.findViewById(R.id.txtEmail);
		EditText passwordText = (EditText) mView.findViewById(R.id.txtPassword);
		email = emailText.getText().toString();
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
		appKey = PreferencesHelper.generateKey(mContext);
	}
    
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = ProgressDialog.show(mContext, "Loading", "Please wait...", true);
        apiFactoryManager = new ApiFactoryManager();
	}
     
	@Override
	protected Void doInBackground(Void... params) {
        checkLogin = false;
        JSONObject jsonData = new JSONObject();
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            jsonData.put("appKey", appKey);
            jsonData.put("email", email);
            jsonData.put("password", password);
            result = apiFactoryManager.makeRequest(ApiConstants.URL_SIGN_IN, jsonData);
        } catch (JSONException e) {
            result.put("error", true);
        }

        Boolean error = (Boolean) result.get("error");
        errorMessage = (String) result.get("errorMessage");
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

            return;
		}

        Integer code = Integer.parseInt(errorMessage);
        if (code == ApiConstants.ERROR_LOGIN_DATA) {
            NotificationHelper.showSimpleToast(mContext, mContext.getString(R.string.error_login_data));
        } else {
            NotificationHelper.showSimpleToast(mContext, mContext.getString(R.string.try_later));
        }



	}
}
