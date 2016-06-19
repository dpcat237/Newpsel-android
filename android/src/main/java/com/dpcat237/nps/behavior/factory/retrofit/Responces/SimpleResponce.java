package com.dpcat237.nps.behavior.factory.retrofit.Responces;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alexey on 19.06.2016.
 */
public class SimpleResponce {

    @SerializedName("error")
    boolean error;

    public boolean isError() {
        return error;
    }
}
