package com.dpcat237.nps.model;

public class Song extends Generic {
    protected Integer list_id;
    protected Integer item_id;
    protected String list_title;
    protected String title;
    protected String content;
    protected String language;
    protected String file;
    protected Boolean is_grabbed = false;
    protected Boolean is_played = false;
    protected String type;
    protected Integer duration;


    public Integer getListId() {
        return list_id;
    }

    public void setListId(Integer listId) {
        this.list_id = listId;
    }

    public Integer getItemApiId() {
        return item_id;
    }

    public void setItemApiId(Integer itemId) {
        this.item_id = itemId;
    }

    public String getListTitle() {
        return list_title;
    }

    public void setListTitle(String listTitle) {
        this.list_title = listTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFilename() {
        return file;
    }

    public void setFilename(String file) {
        this.file = file;
    }

    public Boolean isGrabbed() {
        return is_grabbed;
    }

    public void setGrabbed(Boolean is_grabbed) {
        this.is_grabbed = is_grabbed;
    }

    public Boolean isPlayed() {
        return is_played;
    }

    public void setPlayed(Boolean is_played) {
        this.is_played = is_played;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
