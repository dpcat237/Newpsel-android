package com.dpcat237.nps.behavior.factory.retrofit.Responces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Alexey on 19.06.2016.
 */
public class Tag {

    @SerializedName("api_id")
    @Expose
    private String apiId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("date_up")
    @Expose
    private Integer dateUp;
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
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
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
