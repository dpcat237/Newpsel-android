package com.dpcat237.nps.behavior.factory.retrofit.Responces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Alexey on 19.06.2016.
 */
public class NewpselFeed {
    @SerializedName("api_id")
    @Expose
    private String apiId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("favicon")
    @Expose
    private String favicon;
    @SerializedName("date_up")
    @Expose
    private Integer dateUp;
    @SerializedName("deleted")
    @Expose
    private Boolean deleted;
    @SerializedName("status")
    @Expose
    private Integer status;

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
     * The website
     */
    public String getWebsite() {
        return website;
    }

    /**
     *
     * @param website
     * The website
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     *
     * @return
     * The favicon
     */
    public String getFavicon() {
        return favicon;
    }

    /**
     *
     * @param favicon
     * The favicon
     */
    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    /**
     *
     * @return
     * The dateUp
     */
    public Integer getDateUp() {
        return dateUp;
    }

    /**
     *
     * @param dateUp
     * The date_up
     */
    public void setDateUp(Integer dateUp) {
        this.dateUp = dateUp;
    }

    /**
     *
     * @return
     * The deleted
     */
    public Boolean getDeleted() {
        return deleted;
    }

    /**
     *
     * @param deleted
     * The deleted
     */
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    /**
     *
     * @return
     * The status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

}
