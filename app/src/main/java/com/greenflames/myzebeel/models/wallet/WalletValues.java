package com.greenflames.myzebeel.models.wallet;

/*
 *Created by Adithya T Raj on 09-06-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WalletValues implements Serializable {

    @SerializedName("title")
    private String title;

    @SerializedName("sort_order")
    private String sort_order;

    @SerializedName("price")
    private String price;

    @SerializedName("price_type")
    private String price_type;

    @SerializedName("sku")
    private String sku;

    @SerializedName("option_type_id")
    private String option_type_id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSort_order() {
        return sort_order;
    }

    public void setSort_order(String sort_order) {
        this.sort_order = sort_order;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice_type() {
        return price_type;
    }

    public void setPrice_type(String price_type) {
        this.price_type = price_type;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getOption_type_id() {
        return option_type_id;
    }

    public void setOption_type_id(String option_type_id) {
        this.option_type_id = option_type_id;
    }
}
