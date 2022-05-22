package com.greenflames.myzebeel.models.products;

/*
 *Created by Adithya T Raj on 29-04-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductModel implements Serializable {

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

    @SerializedName("type_id")
    private String type_id;

    @SerializedName("media_gallery_entries")
    private ArrayList<MediaGalleryEntries> media_gallery_entries;

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

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public ArrayList<MediaGalleryEntries> getMedia_gallery_entries() {
        return media_gallery_entries;
    }

    public void setMedia_gallery_entries(ArrayList<MediaGalleryEntries> media_gallery_entries) {
        this.media_gallery_entries = media_gallery_entries;
    }
}
