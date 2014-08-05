package com.dpcat237.nps.model;

public class LabelItem extends Generic {
	public long api_id;
	public long label_id;
	public long label_api_id;
	public long item_api_id;
	public Boolean is_unread;
	public Integer date_add;
	

	public long getApiId() {
		return api_id;
	}

	public void setApiId(long api_id) {
		this.api_id = api_id;
	}
	
	public long getLabelId() {
		return label_id;
	}

	public void setLabelApiId(long label_api_id) {
		this.label_api_id = label_api_id;
	}
	
	public long getLabelApiId() {
		return label_api_id;
	}

	public void setLabelId(long label_id) {
		this.label_id = label_id;
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
	
	public Integer getDateAdd() {
		return date_add;
	}

	public void setDateAdd(Integer date_add) {
		this.date_add = date_add;
	}
}
