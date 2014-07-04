package com.dpcat237.nps.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.dpcat237.nps.R;
import com.dpcat237.nps.model.Label;
import com.dpcat237.nps.database.repository.LabelRepository;

import java.util.ArrayList;

public class SettingsActivity extends PreferenceActivity {
    private static final String TAG = "NPS:SettingsActivity";
    public Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mContext = getApplicationContext();

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
	}

    public class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            ListPreference labelsList = (ListPreference) findPreference("dictation_label_id");
            setListenLabel(labelsList);
        }

        private void setListenLabel(ListPreference labelsList) {
            LabelRepository labelRepo = new LabelRepository(mContext);
            labelRepo.open();
            ArrayList<Label> labels = labelRepo.getAllLabels();
            labelRepo.close();

            CharSequence[] entryValues = new CharSequence[labels.size()];
            CharSequence[] entries = new CharSequence[labels.size()];
            Integer count = 0;
            Integer getId;
            for (Label label : labels) {
                getId = label.getId();
                entryValues[count] = getId.toString();
                entries[count] = label.getName();
                count++;
            }

            labelsList.setEntryValues(entryValues);
            labelsList.setEntries(entries);
        }
    }
}