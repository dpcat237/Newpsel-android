package com.dpcat237.nps.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dpcat237.nps.R;

public class FeedsCategoriesActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_simple_list);
        ListView listView = (ListView) findViewById(R.id.list);

        String[] values = getResources().getStringArray(R.array.drawer_main_activity);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                values);
        listView.setAdapter(adapter);
	}
}