package com.dpcat237.nps.model;

import com.dpcat237.nps.common.model.Generic;

public class LabelItem extends Generic {
	public long label_api_id;
	public long item_api_id;
	public Boolean is_unread = true;


	public void setLabelApiId(long label_api_id) {
		this.label_api_id = label_api_id;
	}
	
	public long getLabelApiId() {
		return label_api_id;
	}

	public long getItemApiId() {
		return item_api_id;
	}

	public void setItemApiId(long item_api_id) {
		this.item_api_id = item_api_id;
	}
	
	public Boolean isUnread() {
		return is_unread;
	}

	public void setIsUnread(Boolean is_unread) {
		this.is_unread = is_unread;
	}
}
