package com.dpcat237.nps.model;

public class Label {
	public Integer id;
	public Integer api_id;
	public String name;
	public Integer lastUpdate;
	public Integer unreadCount;
	public Boolean is_changed;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	
	public Boolean isChanged() {
		return is_changed;
	}

	public void setIsChanged(Boolean is_changed) {
		this.is_changed = is_changed;
	}

	@Override
	public String toString() {
		return name;
	}
}
