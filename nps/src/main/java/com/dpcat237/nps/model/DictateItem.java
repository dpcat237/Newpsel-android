package com.dpcat237.nps.model;

public class DictateItem extends ListItem {
    private Integer feed_id;
    private Integer later_id;
    private String link;
    private Boolean has_tts_error = false;


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

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

    public Boolean hasTtsError() {
        return has_tts_error;
    }

    public void setTtsError(Boolean has_tts_error) {
        this.has_tts_error = has_tts_error;
    }
	
	@Override
	public String toString() {
		return title;
	}
}
