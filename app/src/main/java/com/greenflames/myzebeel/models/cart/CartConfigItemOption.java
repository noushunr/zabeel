package com.greenflames.myzebeel.models.cart;

/*
 *Created by Adithya T Raj on 04-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CartConfigItemOption implements Serializable {

    @SerializedName("option_id")
    private String option_id;

    @SerializedName("option_value")
    private String option_value;

    public String getOption_id() {
        return option_id;
    }

    public void setOption_id(String option_id) {
        this.option_id = option_id;
    }

    public String getOption_value() {
        return option_value;
    }

    public void setOption_value(String option_value) {
        this.option_value = option_value;
    }
}
