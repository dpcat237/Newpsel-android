package com.dpcat237.nps.behavior.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.factory.ApiFactoryManager;
import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.helper.LoginHelper;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.ui.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SignUpTask extends AsyncTask<Void, Integer, Void>{
    private Context mContext;
    private View mView;
    private EditText usernameText;
    private EditText emailText;
    private EditText passwordText;
    private String username;
    private String email;
    private String password;
    private String appKey;
    private String checkApi = "";
    private ProgressDialog dialog;
    private ApiFactoryManager apiFactoryManager;
        
    public SignUpTask(Context context, View view) {
        mContext = context;
        mView = view;
        getData();
    }
        
    private void getData() {
        usernameText = (EditText) mView.findViewById(R.id.txtUsername);
        emailText = (EditText) mView.findViewById(R.id.txtEmail);
        passwordText = (EditText) mView.findViewById(R.id.txtPassword);
        username = usernameText.getText().toString();
        email = emailText.getText().toString();
        String pwd = passwordText.getText().toString();
        try {
                password = LoginHelper.sha1SignUpPassword(pwd);
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
        JSONObject jsonData = new JSONObject();
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            jsonData.put("appKey", appKey);
            jsonData.put("username", username);
            jsonData.put("email", email);
            jsonData.put("password", password);
            result = apiFactoryManager.makeRequest(ApiConstants.URL_SIGN_UP, jsonData);
        } catch (JSONException e) {
            result.put("error", true);
        }
        checkApi = (String) result.get("result");

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
            LoginHelper.doLogin(mContext);
            mContext.startActivity(new Intent(mContext, MainActivity.class));
            ((Activity) mContext).finish();
        } else if (checkApi.equals("304")) {
                usernameText.setError(mContext.getString(R.string.error_304));
                Toast.makeText(mContext, R.string.error_304, Toast.LENGTH_SHORT).show();
        } else if (checkApi.equals("305")) {
                emailText.setError(mContext.getString(R.string.error_305));
                Toast.makeText(mContext, R.string.error_305, Toast.LENGTH_SHORT).show();
        } else if (checkApi.equals("99") || checkApi.equals("310")) {
                Toast.makeText(mContext, R.string.error_310, Toast.LENGTH_SHORT).show();
        }
    }
}