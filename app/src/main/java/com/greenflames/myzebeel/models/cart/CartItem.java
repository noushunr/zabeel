package com.greenflames.myzebeel.models.cart;

/*
 *Created by Adithya T Raj on 04-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CartItem implements Serializable {

    @SerializedName("message")
    private String message;

    @SerializedName("item_id")
    private String item_id;

    @SerializedName("sku")
    private String sku;

    @SerializedName("qty")
    private String qty;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private String price;

    @SerializedName("product_type")
    private String product_type;

    @SerializedName("quote_id")
    private String quote_id;

    @SerializedName("extension_attributes")
    private CartExtensionAttributes extension_attributes;

    @SerializedName("product_option")
    private CartProductOption product_option;

    @SerializedName("options")
    private String options;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
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

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public String getQuote_id() {
        return quote_id;
    }

    public void setQuote_id(String quote_id) {
        this.quote_id = quote_id;
    }

    public CartExtensionAttributes getExtension_attributes() {
        return extension_attributes;
    }

    public void setExtension_attributes(CartExtensionAttributes extension_attributes) {
        this.extension_attributes = extension_attributes;
    }

    public CartProductOption getProduct_option() {
        return product_option;
    }

    public void setProduct_option(CartProductOption product_option) {
        this.product_option = product_option;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}
