package com.greenflames.myzebeel.models.cart;

/*
 *Created by Adithya T Raj on 04-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CartListResponse implements Serializable {

    @SerializedName("message")
    private String message;

    @SerializedName("items")
    private ArrayList<CartItem> items;

    @SerializedName("total_segments")
    private ArrayList<CartTotalSegments> total_segments;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<CartItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<CartItem> items) {
        this.items = items;
    }

    public ArrayList<CartTotalSegments> getTotal_segments() {
        return total_segments;
    }

    public void setTotal_segments(ArrayList<CartTotalSegments> total_segments) {
        this.total_segments = total_segments;
    }
}
