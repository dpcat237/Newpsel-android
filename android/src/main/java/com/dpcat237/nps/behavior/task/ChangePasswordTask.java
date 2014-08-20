package com.dpcat237.nps.behavior.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.factory.ApiFactoryManager;
import com.dpcat237.nps.constant.ApiConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordTask extends AsyncTask<Void, Integer, Void>{
	private Context mContext;
	private View mView;
    private String email;
    private ProgressDialog dialog;
    private ApiFactoryManager apiFactoryManager;

	public ChangePasswordTask(Context context, View view) {
        mContext = context;
        mView = view;
        getData();
    }
	
	private void getData() {
		EditText emailText = (EditText) mView.findViewById(R.id.txtEmail);
		email = emailText.getText().toString();
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
            jsonData.put("email", email);
            result = apiFactoryManager.makeRequest(ApiConstants.URL_RECOVERY_PASSWORD, jsonData);
        } catch (JSONException e) {
            result.put("error", true);
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

        //hide input and button
        EditText emailText = (EditText) mView.findViewById(R.id.txtEmail);
        emailText.setVisibility(View.GONE);
        Button submit = (Button) mView.findViewById(R.id.buttonSubmit);
        submit.setVisibility(View.GONE);

        //show notification
        TextView message = (TextView) mView.findViewById(R.id.sentEmail);
        message.setText(mContext.getText(R.string.sent_change_password));
        message.setVisibility(View.VISIBLE);
	}
}
