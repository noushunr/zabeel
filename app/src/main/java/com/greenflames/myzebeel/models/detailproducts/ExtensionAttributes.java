package com.greenflames.myzebeel.models.detailproducts;

/*
 *Created by Adithya T Raj on 03-05-2021
 */

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ExtensionAttributes implements Serializable {

    @SerializedName("stock_item")
    private StockItem stock_item;

    @Nullable
    @SerializedName("configurable_product_options")
    private ArrayList<ConfigProductOptions> configurable_product_options;

    public StockItem getStock_item() {
        return stock_item;
    }

    public void setStock_item(StockItem stock_item) {
        this.stock_item = stock_item;
    }

    @Nullable
    public ArrayList<ConfigProductOptions> getConfigurable_product_options() {
        return configurable_product_options;
    }

    public void setConfigurable_product_options(@Nullable ArrayList<ConfigProductOptions> configurable_product_options) {
        this.configurable_product_options = configurable_product_options;
    }
}
