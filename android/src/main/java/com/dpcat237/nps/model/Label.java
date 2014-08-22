package com.dpcat237.nps.model;

import com.dpcat237.nps.constant.EntityConstants;

public class Label {
    private Integer id;
    private Integer api_id;
    private String name;
    private Integer unreadCount = 0;
    private Integer status = EntityConstants.STATUS_NORMAL;
    private Integer date_up;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getApiId() {
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

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Integer getStatus() {
        return status;
    }

    public Integer getDateUpdated() {
        return date_up;
    }

    @Override
    public String toString() {
        return name;
    }
}