package com.dpcat237.nps.ui.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.dpcat237.nps.R;
import com.dpcat237.nps.behavior.task.SetLabelTask;
import com.dpcat237.nps.database.repository.LabelRepository;
import com.dpcat237.nps.model.Label;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class LabelsDialog extends DialogFragment{
    private Context mContext;
    private Integer itemApiId;
	private LabelRepository labelRepo;
    private ArrayAdapter<Label> adapter;

	public LabelsDialog(Context context, Integer itemApiId) {
		mContext = context;
        this.itemApiId = itemApiId;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		labelRepo = new LabelRepository(mContext);
		labelRepo.open();
		
		ArrayList<Label> values = labelRepo.getAllLabels();
		adapter = new ArrayAdapter<Label>(mContext, R.layout.fragment_simple_row, values);

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Labels");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int position) {
		    	Label label = adapter.getItem(position);
		    	
		    	SetLabelTask task = new SetLabelTask(mContext, itemApiId, label);
				task.execute();
		    }
		});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);

	    return dialog;

	}
}