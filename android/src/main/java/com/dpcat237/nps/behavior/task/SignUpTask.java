package com.dpcat237.nps.behavior.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class SignUpTask extends AsyncTask<Void, Integer, Void> {
    private static final String TAG = "NPS:SignUpTask";
    private Context mContext;
    private View mView;
    private String email;
    private String password;
    private String appKey;
    private String checkApi = "";
    private ProgressDialog dialog;
    private ApiFactoryManager apiFactoryManager;
        
    public SignUpTask(Context context, View view, String email, String password) {
        mContext = context;
        mView = view;
        this.email = email;
        this.password = password;
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
        } else if (checkApi.equals("305")) {
            EditText passwordButton = (EditText) mView.findViewById(R.id.txtPassword);
            passwordButton.setError(mContext.getString(R.string.error_305));
            NotificationHelper.showSimpleToast(mContext, mContext.getString(R.string.error_305));
        } else if (checkApi.equals("99") || checkApi.equals("310")) {
            NotificationHelper.showSimpleToast(mContext, mContext.getString(R.string.error_310));
        }
    }
}