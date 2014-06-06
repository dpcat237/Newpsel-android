package com.dpcat237.nps.model;

public class Song extends Generic {
    private Integer list_id;
    private Integer item_id;
    private String list_title;
    private String title;
    public String content;
    private String file;
    private Boolean is_grabbed = false;
    private String type;
    private Integer duration;


    public Integer getListId() {
        return list_id;
    }

    public void setListId(Integer listId) {
        this.list_id = listId;
    }

    public Integer getItemId() {
        return item_id;
    }

    public void setItemId(Integer itemId) {
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
