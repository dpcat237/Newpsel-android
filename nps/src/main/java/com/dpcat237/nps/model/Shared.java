package com.dpcat237.nps.model;

public class Shared extends Generic {
	public String title;
	public String text;
    public long label_api_id;


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

    public void setLabelApiId(long label_api_id) {
        this.label_api_id = label_api_id;
    }

    public long getLabelApiId() {
        return label_api_id;
    }

	@Override
	public String toString() {
		return title;
	}
}
