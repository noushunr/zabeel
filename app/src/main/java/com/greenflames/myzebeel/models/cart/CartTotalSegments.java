package com.greenflames.myzebeel.models.cart;

/*
 *Created by Adithya T Raj on 04-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CartTotalSegments implements Serializable {

    @SerializedName("code")
    private String code;

    @SerializedName("title")
    private String title;

    @SerializedName("value")
    private String value;

    @SerializedName("label")
    private String label;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
