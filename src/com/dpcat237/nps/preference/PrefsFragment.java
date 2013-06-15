package com.dpcat237.nps.preference;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.dpcat237.nps.R;

public class PrefsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		// Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
	}
}