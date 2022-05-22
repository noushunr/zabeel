package com.greenflames.myzebeel.models.wishlist;

/*
 *Created by Adithya T Raj on 05-05-2021
 */

import com.greenflames.myzebeel.models.detailproducts.CustomAttributes;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class WishListProduct implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("sku")
    private String sku;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private String price;

    @SerializedName("status")
    private String status;

    @SerializedName("custom_attributes")
    private ArrayList<CustomAttributes> custom_attributes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<CustomAttributes> getCustom_attributes() {
        return custom_attributes;
    }

    public void setCustom_attributes(ArrayList<CustomAttributes> custom_attributes) {
        this.custom_attributes = custom_attributes;
    }
}
