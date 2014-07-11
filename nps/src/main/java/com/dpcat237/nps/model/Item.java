package com.dpcat237.nps.model;

public class Item extends ListItem {
    private Integer feed_id;
    private long ui_id;
    private String link;
    private Boolean is_stared;


    public Integer getFeedId() {
        return feed_id;
    }

    public void setFeedId(Integer feed_id) {
        this.feed_id = feed_id;
    }

    public long getUiId() {
        return ui_id;
    }

    public void setUiId(long api_id) {
        this.ui_id = api_id;
    }
	
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public Boolean isStared() {
		return is_stared;
	}

	public void setIsStared(Boolean is_stared) {
		this.is_stared = is_stared;
	}
	
	@Override
	public String toString() {
		return title;
	}
}
