package com.greenflames.myzebeel.models.order;

/*
 *Created by Adithya T Raj on 08-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderDetail implements Serializable {

    @SerializedName("message")
    private String message;

    @SerializedName("items")
    private ArrayList<OrderDetailItem> items;

    @SerializedName("status_histories")
    private ArrayList<StatusHistory> status_histories;

    @SerializedName("billing_address")
    private BillingAddress billing_address;

    @SerializedName("payment")
    private OrderPayment payment;

    @SerializedName("base_currency_code")
    private String base_currency_code;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("increment_id")
    private String increment_id;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<OrderDetailItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<OrderDetailItem> items) {
        this.items = items;
    }

    public ArrayList<StatusHistory> getStatus_histories() {
        return status_histories;
    }

    public void setStatus_histories(ArrayList<StatusHistory> status_histories) {
        this.status_histories = status_histories;
    }

    public BillingAddress getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(BillingAddress billing_address) {
        this.billing_address = billing_address;
    }

    public OrderPayment getPayment() {
        return payment;
    }

    public void setPayment(OrderPayment payment) {
        this.payment = payment;
    }

    public String getBase_currency_code() {
        return base_currency_code;
    }

    public void setBase_currency_code(String base_currency_code) {
        this.base_currency_code = base_currency_code;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getIncrement_id() {
        return increment_id;
    }

    public void setIncrement_id(String increment_id) {
        this.increment_id = increment_id;
    }
}
