package com.greenflames.myzebeel.models.cart;

/*
 *Created by Adithya T Raj on 04-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CartProductOption implements Serializable {

    @SerializedName("extension_attributes")
    private CartProductExtensionAttributes extension_attributes;

    public CartProductExtensionAttributes getExtension_attributes() {
        return extension_attributes;
    }

    public void setExtension_attributes(CartProductExtensionAttributes extension_attributes) {
        this.extension_attributes = extension_attributes;
    }
}
