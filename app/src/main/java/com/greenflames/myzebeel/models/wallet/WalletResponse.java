package com.greenflames.myzebeel.models.wallet;

/*
 *Created by Adithya T Raj on 09-06-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class WalletResponse implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("sku")
    private String sku;

    @SerializedName("name")
    private String name;

    @SerializedName("type_id")
    private String type_id;

    @SerializedName("status")
    private String status;

    @SerializedName("options")
    private ArrayList<WalletOptions> options;

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

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<WalletOptions> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<WalletOptions> options) {
        this.options = options;
    }
}
