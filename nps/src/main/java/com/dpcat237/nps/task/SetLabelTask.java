package com.dpcat237.nps.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.model.Label;
import com.dpcat237.nps.model.LabelItem;
import com.dpcat237.nps.repository.LabelRepository;

public class SetLabelTask extends AsyncTask<Void, Integer, Void>{
	private Context mContext;
	private LabelRepository labelRepo;
	Item item;
	Label label;
	Boolean set = false;
	
	public SetLabelTask(Context context, Item selectedItem, Label selectedLabel) {
        mContext = context;
        item = selectedItem;
        label = selectedLabel;
    }
     
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		labelRepo = new LabelRepository(mContext);
		labelRepo.open();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		if (labelRepo.checkLabelSet(label.getId(), item.getApiId())) {
			labelRepo.removeLaterItem(label.getId(), item.getApiId());
			set = true;
		} else {
			LabelItem labelItem = new LabelItem();
			labelItem.setLabelId(label.getId());
			labelItem.setLabelApiId(label.getApiId());
			labelItem.setItemApiId(item.getApiId());
			labelItem.setIsUnread(true);
			
			labelRepo.setLabel(labelItem);
		}
		
		return null;
	}
  
	@Override
 	protected void onPostExecute(Void result) {
		labelRepo.close();
		
		if (set) {
			Toast.makeText(mContext, mContext.getString(R.string.txt_label)+" "+label + " " + mContext.getString(R.string.txt_removed) + ".", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(mContext, mContext.getString(R.string.txt_successful_set_label)+" "+label + ".", Toast.LENGTH_SHORT).show();
		}
	}
}
