package com.greenflames.myzebeel.models.order;

/*
 *Created by Adithya T Raj on 08-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderPayment implements Serializable {

    @SerializedName("amount_ordered")
    private String amount_ordered;

    @SerializedName("shipping_amount")
    private String shipping_amount;

    @SerializedName("method")
    private String method;

    @SerializedName("base_amount_ordered")
    private String base_amount_ordered;

    @SerializedName("additional_information")
    private ArrayList<String> additional_information;

    public String getAmount_ordered() {
        return amount_ordered;
    }

    public void setAmount_ordered(String amount_ordered) {
        this.amount_ordered = amount_ordered;
    }

    public String getShipping_amount() {
        return shipping_amount;
    }

    public void setShipping_amount(String shipping_amount) {
        this.shipping_amount = shipping_amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public ArrayList<String> getAdditional_information() {
        return additional_information;
    }

    public void setAdditional_information(ArrayList<String> additional_information) {
        this.additional_information = additional_information;
    }

    public String getBase_amount_ordered() {
        return base_amount_ordered;
    }

    public void setBase_amount_ordered(String base_amount_ordered) {
        this.base_amount_ordered = base_amount_ordered;
    }
}
