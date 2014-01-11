package com.dpcat237.nps;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.dpcat237.nps.model.Item;
import com.dpcat237.nps.model.Label;
import com.dpcat237.nps.repository.LabelRepository;
import com.dpcat237.nps.task.SetLabelTask;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class LabelsDialog extends DialogFragment{
	Context mContext;
	Item item;
	private LabelRepository labelRepo;
	ArrayAdapter<Label> adapter;

	public LabelsDialog(Context context, Item selectedItem) {
		mContext = context;
		item = selectedItem;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		labelRepo = new LabelRepository(mContext);
		labelRepo.open();
		
		ArrayList<Label> values = labelRepo.getAllLabels();
		adapter = new ArrayAdapter<Label>(mContext, android.R.layout.simple_list_item_1, values);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Labels");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int position) {
		    	Label label = adapter.getItem(position);
		    	
		    	SetLabelTask task = new SetLabelTask(mContext, item, label);
				task.execute();
		    }
		});
		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);

	    return dialog;

	}
}