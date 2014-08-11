package com.dpcat237.nps.model;

import com.dpcat237.nps.constant.EntityConstants;

public class Feed extends List {
    private String website;
    private String favicon;
    private Integer itemsCount;
    private Integer unreadCount = 0;
    private Integer status = EntityConstants.STATUS_NORMAL;
    private Integer date_up;


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

    public Integer getStatus() {
        return status;
    }

    public Integer getDateUpdated() {
        return date_up;
    }

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return title;
	}
}
