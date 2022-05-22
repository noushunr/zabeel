package com.greenflames.myzebeel.models.wallet;

/*
 *Created by Adithya T Raj on 09-06-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class WalletOptions implements Serializable {

    @SerializedName("product_sku")
    private String product_sku;

    @SerializedName("option_id")
    private String option_id;

    @SerializedName("title")
    private String title;

    @SerializedName("price")
    private String price;

    @SerializedName("price_type")
    private String price_type;

    @SerializedName("values")
    private ArrayList<WalletValues> values;

    public String getProduct_sku() {
        return product_sku;
    }

    public void setProduct_sku(String product_sku) {
        this.product_sku = product_sku;
    }

    public String getOption_id() {
        return option_id;
    }

    public void setOption_id(String option_id) {
        this.option_id = option_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public ArrayList<WalletValues> getValues() {
        return values;
    }

    public void setValues(ArrayList<WalletValues> values) {
        this.values = values;
    }
}
