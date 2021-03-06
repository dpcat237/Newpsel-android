package com.dpcat237.nps.common.model;

public class Item extends ListItem {
    private Integer feed_id;
    private Boolean is_stared;

    public Integer getFeedId() {
        return feed_id;
    }

    public void setFeedId(Integer feed_id) {
        this.feed_id = feed_id;
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
