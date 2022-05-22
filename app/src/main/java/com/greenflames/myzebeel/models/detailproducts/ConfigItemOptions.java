package com.greenflames.myzebeel.models.detailproducts;

/*
 *Created by Adithya T Raj on 04-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ConfigItemOptions implements Serializable {

    @SerializedName("option_value")
    private String option_value;

    @SerializedName("option_id")
    private String option_id;

    @SerializedName("label")
    private String label;

    public ConfigItemOptions(String option_value, String option_id, String label) {
        this.option_value = option_value;
        this.option_id = option_id;
        this.label = label;
    }

    public String getOption_value() {
        return option_value;
    }

    public void setOption_value(String option_value) {
        this.option_value = option_value;
    }

    public String getOption_id() {
        return option_id;
    }

    public void setOption_id(String option_id) {
        this.option_id = option_id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
