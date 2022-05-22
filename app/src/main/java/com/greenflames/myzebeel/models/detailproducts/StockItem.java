package com.greenflames.myzebeel.models.detailproducts;

/*
 *Created by Adithya T Raj on 03-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StockItem implements Serializable {

    @SerializedName("item_id")
    private String item_id;

    @SerializedName("product_id")
    private String product_id;

    @SerializedName("stock_id")
    private String stock_id;

    @SerializedName("qty")
    private String qty;

    @SerializedName("is_in_stock")
    private Boolean is_in_stock;

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getStock_id() {
        return stock_id;
    }

    public void setStock_id(String stock_id) {
        this.stock_id = stock_id;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public Boolean getIs_in_stock() {
        return is_in_stock;
    }

    public void setIs_in_stock(Boolean is_in_stock) {
        this.is_in_stock = is_in_stock;
    }
}
