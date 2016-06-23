package com.dpcat237.nps.behavior.factory.retrofit.Responces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Alexey on 19.06.2016.
 */
public class SavedArticle {
    @SerializedName("api_id")
    @Expose
    private String apiId;
    @SerializedName("item_id")
    @Expose
    private String itemId;
    @SerializedName("feed_id")
    @Expose
    private String feedId;
    @SerializedName("later_id")
    @Expose
    private String laterId;
    @SerializedName("is_unread")
    @Expose
    private Boolean isUnread;
    @SerializedName("date_add")
    @Expose
    private Integer dateAdd;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("item_language")
    @Expose
    private Object itemLanguage;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("content")
    @Expose
    private String content;

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
     * The itemId
     */
    public String getItemId() {
        return itemId;
    }

    /**
     *
     * @param itemId
     * The item_id
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
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
     * The laterId
     */
    public String getLaterId() {
        return laterId;
    }

    /**
     *
     * @param laterId
     * The later_id
     */
    public void setLaterId(String laterId) {
        this.laterId = laterId;
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
     * The itemLanguage
     */
    public Object getItemLanguage() {
        return itemLanguage;
    }

    /**
     *
     * @param itemLanguage
     * The item_language
     */
    public void setItemLanguage(Object itemLanguage) {
        this.itemLanguage = itemLanguage;
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

}
