package com.dpcat237.nps.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.model.Label;
import com.dpcat237.nps.model.LabelItem;
import com.dpcat237.nps.repository.LabelRepository;

public class SetLabelTask extends AsyncTask<Void, Integer, Void> {
	private Context mContext;
	private LabelRepository labelRepo;
    private Integer itemApiId;
    private Label label;
    private Boolean set = false;
	
	public SetLabelTask(Context context, Integer itemApiId, Label selectedLabel) {
        mContext = context;
        this.itemApiId = itemApiId;
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
		if (labelRepo.checkLabelSet(label.getId(), itemApiId)) {
			labelRepo.removeLaterItem(label.getId(), itemApiId);
			set = true;
		} else {
			LabelItem labelItem = new LabelItem();
			labelItem.setLabelId(label.getId());
			labelItem.setLabelApiId(label.getApiId());
			labelItem.setItemApiId(itemApiId);
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
