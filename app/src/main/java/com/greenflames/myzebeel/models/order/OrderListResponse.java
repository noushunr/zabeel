package com.greenflames.myzebeel.models.order;

/*
 *Created by Adithya T Raj on 07-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderListResponse implements Serializable {

    @SerializedName("message")
    private String message;

    @SerializedName("items")
    private ArrayList<OrderItem> items;

    @SerializedName("total_count")
    private String total_count;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<OrderItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<OrderItem> items) {
        this.items = items;
    }

    public String getTotal_count() {
        return total_count;
    }

    public void setTotal_count(String total_count) {
        this.total_count = total_count;
    }
}
