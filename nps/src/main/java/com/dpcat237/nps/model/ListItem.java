package com.dpcat237.nps.model;

public class ListItem extends Generic {
    protected Integer api_id;
    protected String title;
    protected String text;
    protected String content;
    protected String language;

    public Integer getApiId() {
        return api_id;
    }

    public void setApiId(Integer api_id) {
        this.api_id = api_id;
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
}
