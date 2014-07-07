package com.dpcat237.nps.behavior.receiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.helper.ConnectionHelper;
import com.dpcat237.nps.model.Label;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.behavior.task.SaveSharedTask;
import com.dpcat237.nps.behavior.task.SendSharedTask;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SharedReceiver extends Activity {
	Context mContext;
    ListView listView;
    private Bundle extras;
    ArrayAdapter<Label> mAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		Intent intent = getIntent();
		extras = intent.getExtras();
		setContentView(R.layout.dialog_shared_labels);
        LabelRepository labelRepo = new LabelRepository(this);
		labelRepo.open();

		listView = (ListView) findViewById(R.id.labelsList);
		ArrayList<Label> values = labelRepo.getAllLabels();
        mAdapter = new ArrayAdapter<Label>(this, R.layout.dialog_labels_list_row, values);
		listView.setAdapter(mAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Label label = mAdapter.getItem(position);
                savedShared(label);
			}
		});

		Intent result = new Intent("com.example.RESULT_ACTION");
		setResult(Activity.RESULT_OK, result);
        labelRepo.close();
	}

    public void savedShared(Label label) {
        Toast.makeText(mContext, mContext.getString(R.string.ts_successfully_saved)+label.getName()+" "+mContext.getString(R.string.label_s)+".", Toast.LENGTH_LONG).show();

        String subject = extras.getString(Intent.EXTRA_SUBJECT);
        String link = pullLink(extras.getString(Intent.EXTRA_TEXT));
        SaveSharedTask taskSave = new SaveSharedTask(mContext, label.getApiId(), subject, link);
        taskSave.execute();

        if (ConnectionHelper.hasConnection(this)) {
            SendSharedTask taskSend = new SendSharedTask(this);
            taskSend.execute();
        }

        ((Activity) mContext).finish();
    }

    //Pull all links from the body for easy retrieval
    private String pullLink(String text) {
        ArrayList links = new ArrayList();
        String link = "";

        String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);
        while(m.find()) {
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")"))
            {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            //links.add(urlStr);
            link = urlStr;
            break;
        }

        return link;
    }
}