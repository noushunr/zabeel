package com.greenflames.myzebeel.models.order;

/*
 *Created by Adithya T Raj on 08-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderDetailItem implements Serializable {

    @SerializedName("base_original_price")
    private String base_original_price;

    @SerializedName("qty_ordered")
    private String qty_ordered;

    @SerializedName("name")
    private String name;

    @SerializedName("item_id")
    private String item_id;

    @SerializedName("original_price")
    private String original_price;

    @SerializedName("price")
    private String price;

    @SerializedName("store_id")
    private String store_id;

    public String getBase_original_price() {
        return base_original_price;
    }

    public void setBase_original_price(String base_original_price) {
        this.base_original_price = base_original_price;
    }

    public String getQty_ordered() {
        return qty_ordered;
    }

    public void setQty_ordered(String qty_ordered) {
        this.qty_ordered = qty_ordered;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(String original_price) {
        this.original_price = original_price;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }
}
