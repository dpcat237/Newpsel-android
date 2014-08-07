package com.dpcat237.nps.ui.activity;

import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.dpcat237.nps.R;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.model.Label;

import java.util.ArrayList;

public class SettingsActivity extends PreferenceActivity {
    private static final String TAG = "NPS:SettingsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
	}

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            MultiSelectListPreference labelsList = (MultiSelectListPreference) findPreference("pref_later_items_labels");
            setLaterItemsLabels(labelsList);
        }

        private void setLaterItemsLabels(MultiSelectListPreference labelsList) {
            LabelRepository labelRepo = new LabelRepository(getActivity());
            labelRepo.open();
            ArrayList<Label> labels = labelRepo.getAllLabels();
            labelRepo.close();

            CharSequence[] entryValues = new CharSequence[labels.size()];
            CharSequence[] entries = new CharSequence[labels.size()];
            Integer count = 0;
            Integer ApiId;
            for (Label label : labels) {
                ApiId = label.getApiId();
                entryValues[count] = ApiId.toString();
                entries[count] = label.getName();
                count++;
            }

            labelsList.setEntryValues(entryValues);
            labelsList.setEntries(entries);
        }
    }
}