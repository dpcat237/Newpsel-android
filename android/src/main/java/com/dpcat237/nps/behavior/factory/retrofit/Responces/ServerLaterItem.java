package com.dpcat237.nps.behavior.factory.retrofit.Responces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Alexey on 19.06.2016.
 */
public class ServerLaterItem {
    @SerializedName("item_id")
    @Expose
    private Integer itemId;
    @SerializedName("label_id")
    @Expose
    private Integer labelId;

    /**
     *
     * @return
     * The itemId
     */
    public Integer getItemId() {
        return itemId;
    }

    /**
     *
     * @param itemId
     * The item_id
     */
    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    /**
     *
     * @return
     * The labelId
     */
    public Integer getLabelId() {
        return labelId;
    }

    /**
     *
     * @param labelId
     * The label_id
     */
    public void setLabelId(Integer labelId) {
        this.labelId = labelId;
    }
}
