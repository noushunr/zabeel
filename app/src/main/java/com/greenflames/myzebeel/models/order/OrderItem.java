package com.greenflames.myzebeel.models.order;

/*
 *Created by Adithya T Raj on 07-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OrderItem implements Serializable {

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("entity_id")
    private String entity_id;

    @SerializedName("grand_total")
    private String grand_total;

    @SerializedName("increment_id")
    private String increment_id;

    @SerializedName("status")
    private String status;

    @SerializedName("billing_address")
    private BillingAddress billing_address;

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }

    public String getGrand_total() {
        return grand_total;
    }

    public void setGrand_total(String grand_total) {
        this.grand_total = grand_total;
    }

    public String getIncrement_id() {
        return increment_id;
    }

    public void setIncrement_id(String increment_id) {
        this.increment_id = increment_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BillingAddress getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(BillingAddress billing_address) {
        this.billing_address = billing_address;
    }
}
