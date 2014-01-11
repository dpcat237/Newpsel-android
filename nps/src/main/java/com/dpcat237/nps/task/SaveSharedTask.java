package com.dpcat237.nps.task;

import android.content.Context;
import android.os.AsyncTask;

import com.dpcat237.nps.model.Shared;
import com.dpcat237.nps.repository.SharedRepository;

public class SaveSharedTask extends AsyncTask<Void, Integer, Void>{
	private Context mContext;
	private SharedRepository sharedRepo;
	Long labelApiId;
	String title;
    String url;

	public SaveSharedTask(Context context, Long labelApiIdData, String titleData, String urlData) {
        mContext = context;
        labelApiId = labelApiIdData;
        title = titleData;
        url = urlData;
    }

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
        sharedRepo = new SharedRepository(mContext);
        sharedRepo.open();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
        saveShared();

		return null;
	}
  
	@Override
 	protected void onPostExecute(Void result) {
        sharedRepo.close();
	}

    protected void saveShared() {
        Shared shared = new Shared();
        shared.setLabelApiId(labelApiId);
        shared.setTitle(title);
        shared.setText(url);
        sharedRepo.addShared(shared);
    }
}
