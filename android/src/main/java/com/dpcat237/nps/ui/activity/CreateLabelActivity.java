package com.dpcat237.nps.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.dpcat237.nps.R;
import com.dpcat237.nps.constant.PreferenceConstants;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.helper.ConnectionHelper;
import com.dpcat237.nps.helper.DisplayHelper;
import com.dpcat237.nps.helper.NotificationHelper;
import com.dpcat237.nps.helper.PreferencesHelper;

public class CreateLabelActivity extends Activity {
    private Context mContext;
    private View mView;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mContext = this;
		mView = this.findViewById(android.R.id.content).getRootView();
		
		setContentView(R.layout.activity_label_create);
	}
	
	public void createLabel(View view) {
        if (!ConnectionHelper.hasConnection(mContext)) {
            NotificationHelper.showSimpleToast(mContext, mContext.getString(R.string.error_connection));

            return;
        }

        DisplayHelper.hideKeyboard(mContext, mView);
		LabelRepository labelRepo = new LabelRepository(this);
		labelRepo.open();
		
		EditText nameText = (EditText) mView.findViewById(R.id.txtName);
		String name = nameText.getText().toString();
		labelRepo.createLabel(name);

        //notify to sync labels to server
        PreferencesHelper.setBooleanPreference(mContext, PreferenceConstants.LABELS_SYNC_REQUIRED, true);
        PreferencesHelper.setBooleanPreference(mContext, PreferenceConstants.WEAR_LABELS_SENT, false);

		finish();
	}

    @Override
    public boolean onKeyUp (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            createLabel(mView);

            return true;
        }

        return super.onKeyUp(keyCode, event);
    }
}