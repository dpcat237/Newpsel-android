package com.dpcat237.nps.behavior.factory.retrofit.Responces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Alexey on 19.06.2016.
 */
public class ArticleSyncResponce {

    @SerializedName("api_id")
    @Expose
    private String apiId;
    @SerializedName("feed_id")
    @Expose
    private String feedId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("date_add")
    @Expose
    private Integer dateAdd;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("ui_id")
    @Expose
    private Integer uiId;
    @SerializedName("is_stared")
    @Expose
    private Boolean isStared;
    @SerializedName("is_unread")
    @Expose
    private Boolean isUnread;

    /**
     *
     * @return
     * The apiId
     */
    public String getApiId() {
        return apiId;
    }

    /**
     *
     * @param apiId
     * The api_id
     */
    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    /**
     *
     * @return
     * The feedId
     */
    public String getFeedId() {
        return feedId;
    }

    /**
     *
     * @param feedId
     * The feed_id
     */
    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The link
     */
    public String getLink() {
        return link;
    }

    /**
     *
     * @param link
     * The link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     *
     * @return
     * The content
     */
    public String getContent() {
        return content;
    }

    /**
     *
     * @param content
     * The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     *
     * @return
     * The dateAdd
     */
    public Integer getDateAdd() {
        return dateAdd;
    }

    /**
     *
     * @param dateAdd
     * The date_add
     */
    public void setDateAdd(Integer dateAdd) {
        this.dateAdd = dateAdd;
    }

    /**
     *
     * @return
     * The language
     */
    public String getLanguage() {
        return language;
    }

    /**
     *
     * @param language
     * The language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     *
     * @return
     * The uiId
     */
    public Integer getUiId() {
        return uiId;
    }

    /**
     *
     * @param uiId
     * The ui_id
     */
    public void setUiId(Integer uiId) {
        this.uiId = uiId;
    }

    /**
     *
     * @return
     * The isStared
     */
    public Boolean getIsStared() {
        return isStared;
    }

    /**
     *
     * @param isStared
     * The is_stared
     */
    public void setIsStared(Boolean isStared) {
        this.isStared = isStared;
    }

    /**
     *
     * @return
     * The isUnread
     */
    public Boolean getIsUnread() {
        return isUnread;
    }

    /**
     *
     * @param isUnread
     * The is_unread
     */
    public void setIsUnread(Boolean isUnread) {
        this.isUnread = isUnread;
    }

}
