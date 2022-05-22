package com.greenflames.myzebeel.models.wishlist;

/*
 *Created by Adithya T Raj on 05-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class WishListResponse implements Serializable {

    @SerializedName("message")
    private String message;

    @SerializedName("items_count")
    private String items_count;

    @SerializedName("items")
    private ArrayList<WishListItem> items;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getItems_count() {
        return items_count;
    }

    public void setItems_count(String items_count) {
        this.items_count = items_count;
    }

    public ArrayList<WishListItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<WishListItem> items) {
        this.items = items;
    }
}
