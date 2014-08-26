package com.dpcat237.nps.behavior.task;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dpcat237.nps.R;
import com.dpcat237.nps.common.model.Label;
import com.dpcat237.nps.helper.NotificationHelper;
import com.dpcat237.nps.model.LabelItem;
import com.dpcat237.nps.database.repository.LabelRepository;

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
		if (labelRepo.checkLabelSet(label.getApiId(), itemApiId)) {
			labelRepo.removeLabelItem(label.getApiId(), itemApiId);
		} else {
			LabelItem labelItem = new LabelItem();
			labelItem.setLabelApiId(label.getApiId());
			labelItem.setItemApiId(itemApiId);

			labelRepo.setLabel(labelItem);
            set = true;
		}
		
		return null;
	}
  
	@Override
 	protected void onPostExecute(Void result) {
		labelRepo.close();
		
		if (set) {
            NotificationHelper.showSimpleToast(mContext, mContext.getString(R.string.txt_set_label, label.getName()));
		} else {
            NotificationHelper.showSimpleToast(mContext, mContext.getString(R.string.txt_label_removed, label.getName()));
		}
	}
}
