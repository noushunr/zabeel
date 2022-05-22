package com.greenflames.myzebeel.models.wishlist;

/*
 *Created by Adithya T Raj on 05-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WishListItem implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("product")
    private WishListProduct product;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public WishListProduct getProduct() {
        return product;
    }

    public void setProduct(WishListProduct product) {
        this.product = product;
    }
}
