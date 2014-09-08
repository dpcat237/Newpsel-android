package com.dpcat237.nps.common.model;

public abstract class List extends Generic {
    protected Integer api_id;
    protected String title;
    protected Integer lastUpdate;

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

    public Integer getLastUpdate() {
        return lastUpdate;
    }
}
