package com.greenflames.myzebeel.models.products;

/*
 *Created by Adithya T Raj on 29-04-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductResponse implements Serializable {

    @SerializedName("message")
    private String message;

    @SerializedName("total_count")
    private String total_count;

    @SerializedName("items")
    private ArrayList<ProductModel> items;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTotal_count() {
        return total_count;
    }

    public void setTotal_count(String total_count) {
        this.total_count = total_count;
    }

    public ArrayList<ProductModel> getItems() {
        return items;
    }

    public void setItems(ArrayList<ProductModel> items) {
        this.items = items;
    }
}
