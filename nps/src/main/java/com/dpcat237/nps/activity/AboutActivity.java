package com.dpcat237.nps.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.dpcat237.nps.R;

public class AboutActivity extends Activity {
	Context mContext;
	View mView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		mView = this.findViewById(android.R.id.content).getRootView();
		
		setContentView(R.layout.activity_about);
		
		PackageManager manager = mContext.getPackageManager();
		PackageInfo info;
		try {
			info = manager.getPackageInfo(mContext.getPackageName(), 0);
			String version = info.versionName;
			TextView resultTxt = (TextView) mView.findViewById(R.id.textVersion);
		    resultTxt.setText(version);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}