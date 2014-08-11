package com.dpcat237.nps.model;

import com.dpcat237.nps.constant.EntityConstants;

public class Label {
    public Integer id;
    public Integer api_id;
    public String name;
    public Integer unreadCount = 0;
    public Integer status = EntityConstants.STATUS_NORMAL;
    public Integer date_up;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }

    public Integer getDateUpdated() {
        return date_up;
    }

    public void setDateUpdated(Integer dateUp) {
        this.date_up = dateUp;
    }

    @Override
    public String toString() {
        return name;
    }
}
