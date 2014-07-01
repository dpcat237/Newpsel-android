package com.dpcat237.nps.model;

public class Item extends ListItem {
    private Integer feed_id;
    private long ui_id;
    private String link;
    private Boolean is_stared;
    private Boolean is_unread;
    private long date_add;


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
	
	public Boolean isUnread() {
		return is_unread;
	}

	public void setIsUnread(Boolean is_unread) {
		this.is_unread = is_unread;
	}
	
	public long getDateAdd() {
		return date_add;
	}

	public void setDateAdd(long date_add) {
		this.date_add = date_add;
	}
	
	@Override
	public String toString() {
		return title;
	}
}
