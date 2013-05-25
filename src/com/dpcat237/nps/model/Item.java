package com.dpcat237.nps.model;

public class Item {
	public long id;
	public long api_id;
	public long feed_id;
	public String title;
	public String link;
	public String content;
	public Boolean is_stared;
	public Boolean is_unread;
	public Integer date_add;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public long getApiId() {
		return api_id;
	}

	public void setApiId(long api_id) {
		this.api_id = api_id;
	}
	
	public long getFeedId() {
		return feed_id;
	}

	public void setFeedId(long feed_id) {
		this.feed_id = feed_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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
	
	public Integer getDateAdd() {
		return date_add;
	}

	public void setDateAdd(Integer date_add) {
		this.date_add = date_add;
	}
	
	@Override
	public String toString() {
		return title;
	}
}
