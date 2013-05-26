package com.dpcat237.nps.model;

public class Feed {
	public Integer id;
	public Integer api_id;
	public String title;
	public String website;
	public String favicon;
	public Integer lastUpdate;
	public Integer unreadCount;

	public long getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public long getApiId() {
		return api_id;
	}

	public void setApiId(Integer api_id) {
		this.api_id = api_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}
	
	public String getFavicon() {
		return favicon;
	}

	public void setFavicon(String favicon) {
		this.favicon = favicon;
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Integer lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public long getUnreadCount() {
		return unreadCount;
	}

	public void setUnreadCount(Integer unreadCount) {
		this.unreadCount = unreadCount;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return title;
	}
}
