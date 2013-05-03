package com.dpcat237.nps.model;

public class Feed {
	public long id;
	public long api_id;
	public String title;
	public String website;
	public String favicon;

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

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return title +" "+ api_id;
	}
}
