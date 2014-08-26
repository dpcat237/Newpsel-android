package com.dpcat237.nps.common.model;

public class LaterItem extends ListItem {
    protected Integer feed_id;
    protected Integer later_id;


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

    @Override
    public String toString() {
        return title;
    }
}
