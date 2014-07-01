package com.dpcat237.nps.model;

public class Feed extends List {
    private String website;
    private String favicon;
    private Integer itemsCount;
    private Integer unreadCount;


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

	public Integer getItemsCount() {
		return itemsCount;
	}

	public void setItemsCount(Integer itemsCount) {
		this.itemsCount = itemsCount;
	}

    public Integer getUnreadCount() {
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
