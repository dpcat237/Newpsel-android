package com.dpcat237.nps.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.dpcat237.nps.R;

public class SettingsActivity extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		// Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
	}
}