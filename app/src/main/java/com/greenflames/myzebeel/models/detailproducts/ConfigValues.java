package com.greenflames.myzebeel.models.detailproducts;

/*
 *Created by Adithya T Raj on 03-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ConfigValues implements Serializable {

    @SerializedName("value_index")
    private String value_index;

    @SerializedName("extension_attributes")
    private ConfigValuesExtensionAttributes extension_attributes;

    public String getValue_index() {
        return value_index;
    }

    public void setValue_index(String value_index) {
        this.value_index = value_index;
    }

    public ConfigValuesExtensionAttributes getExtension_attributes() {
        return extension_attributes;
    }

    public void setExtension_attributes(ConfigValuesExtensionAttributes extension_attributes) {
        this.extension_attributes = extension_attributes;
    }
}
