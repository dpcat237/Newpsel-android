package com.dpcat237.nps.behavior.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.factory.ApiFactoryManager;
import com.dpcat237.nps.constant.ApiConstants;
import com.dpcat237.nps.helper.LoginHelper;
import com.dpcat237.nps.helper.NotificationHelper;
import com.dpcat237.nps.helper.PreferencesHelper;
import com.dpcat237.nps.ui.activity.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpTask extends AsyncTask<Void, Integer, Void> {
    private static final String TAG = "NPS:SignUpTask";
    private Context mContext;
    private String email;
    private String password;
    private String appKey;
    private String checkApi = "";
    private ProgressDialog dialog;
    private ApiFactoryManager apiFactoryManager;
        
    public SignUpTask(Context context, String email, String password) {
        mContext = context;
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
            jsonData.put(ApiConstants.DEVICE_ID, appKey);
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

        if (checkApi == null) {
            LoginHelper.doLogin(mContext);
            mContext.startActivity(new Intent(mContext, MainActivity.class));
            ((Activity) mContext).finish();
        } else if (checkApi.equals("305")) {
            NotificationHelper.showSimpleToast(mContext, mContext.getString(R.string.error_user_exists));
        } else if (checkApi.equals("99") || checkApi.equals("310")) {
            NotificationHelper.showSimpleToast(mContext, mContext.getString(R.string.error_310));
        }
    }
}