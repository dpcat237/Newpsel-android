package com.dpcat237.nps.behavior.factory.retrofit.Responces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Alexey on 19.06.2016.
 */
public class ServerSharedItem {

    @SerializedName("label_api_id")
    @Expose
    private Integer labelApiId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("text")
    @Expose
    private String text;

    /**
     *
     * @return
     * The labelApiId
     */
    public Integer getLabelApiId() {
        return labelApiId;
    }

    /**
     *
     * @param labelApiId
     * The label_api_id
     */
    public void setLabelApiId(Integer labelApiId) {
        this.labelApiId = labelApiId;
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
     * The text
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @param text
     * The text
     */
    public void setText(String text) {
        this.text = text;
    }

}
