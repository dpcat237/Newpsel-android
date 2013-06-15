package com.dpcat237.nps;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.dpcat237.nps.preference.PrefsFragment;

public class SettingsActivity extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
	}
}