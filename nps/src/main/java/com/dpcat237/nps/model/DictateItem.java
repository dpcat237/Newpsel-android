package com.dpcat237.nps.model;

public class DictateItem extends ListItem {
    private Integer item_id;
    private Integer feed_id;
    private Integer later_id;
    private Boolean is_unread;
    private Integer date_add;
    private String link;


    public Integer getItemApiId() {
        return item_id;
    }

    public void setItemApiId(Integer item_id) {
        this.item_id = item_id;
    }

    public Integer getFeedApiId() {
        return feed_id;
    }

    public void setFeedApiId(Integer feed_id) {
        this.feed_id = feed_id;
    }

    public Integer getLabelApiId() {
        return later_id;
    }

    public void setLabelApiId(Integer later_id) {
        this.later_id = later_id;
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
	
	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	@Override
	public String toString() {
		return title;
	}
}
