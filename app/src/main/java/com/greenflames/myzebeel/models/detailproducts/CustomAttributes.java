package com.greenflames.myzebeel.models.detailproducts;

/*
 *Created by Adithya T Raj on 03-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CustomAttributes implements Serializable {

    @SerializedName("attribute_code")
    private String attribute_code;

    @SerializedName("value")
    private Object value;

    public String getAttribute_code() {
        return attribute_code;
    }

    public void setAttribute_code(String attribute_code) {
        this.attribute_code = attribute_code;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
