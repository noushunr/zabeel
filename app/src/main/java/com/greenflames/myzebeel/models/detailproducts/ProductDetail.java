package com.greenflames.myzebeel.models.detailproducts;

/*
 *Created by Adithya T Raj on 03-05-2021
 */

import com.greenflames.myzebeel.models.products.MediaGalleryEntries;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductDetail implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("sku")
    private String sku;

    @SerializedName("name")
    private String name;

    @SerializedName("attribute_set_id")
    private String attribute_set_id;

    @SerializedName("price")
    private String price;

    @SerializedName("status")
    private String status;

    @SerializedName("visibility")
    private String visibility;

    @SerializedName("type_id")
    private String type_id;

    @SerializedName("extension_attributes")
    private ExtensionAttributes extension_attributes;

    @SerializedName("media_gallery_entries")
    private ArrayList<MediaGalleryEntries> media_gallery_entries;

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

    public String getAttribute_set_id() {
        return attribute_set_id;
    }

    public void setAttribute_set_id(String attribute_set_id) {
        this.attribute_set_id = attribute_set_id;
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

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public ExtensionAttributes getExtension_attributes() {
        return extension_attributes;
    }

    public void setExtension_attributes(ExtensionAttributes extension_attributes) {
        this.extension_attributes = extension_attributes;
    }

    public ArrayList<MediaGalleryEntries> getMedia_gallery_entries() {
        return media_gallery_entries;
    }

    public void setMedia_gallery_entries(ArrayList<MediaGalleryEntries> media_gallery_entries) {
        this.media_gallery_entries = media_gallery_entries;
    }

    public ArrayList<CustomAttributes> getCustom_attributes() {
        return custom_attributes;
    }

    public void setCustom_attributes(ArrayList<CustomAttributes> custom_attributes) {
        this.custom_attributes = custom_attributes;
    }
}
