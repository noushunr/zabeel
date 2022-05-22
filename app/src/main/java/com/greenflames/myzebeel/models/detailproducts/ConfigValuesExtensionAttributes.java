package com.greenflames.myzebeel.models.detailproducts;

/*
 *Created by Adithya T Raj on 03-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ConfigValuesExtensionAttributes implements Serializable {

    @SerializedName("label")
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
