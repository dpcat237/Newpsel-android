package com.dpcat237.nps.model;

public class ListItem extends Generic {
    protected Integer api_id;
    protected Integer item_id;
    protected Integer list_id;
    protected String title;
    protected String text;
    protected String content;
    protected String language;
    protected Boolean is_unread;
    protected Integer date_add;

    public Integer getApiId() {
        return api_id;
    }

    public void setApiId(Integer api_id) {
        this.api_id = api_id;
    }

    public Integer getItemApiId() {
        return item_id;
    }

    public void setItemApiId(Integer item_id) {
        this.item_id = item_id;
    }

    public Integer getListApiId() {
        return list_id;
    }

    public void setListApiId(Integer list_id) {
        this.list_id = list_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}
