package com.greenflames.myzebeel.models.shipping;

/*
 *Created by Adithya T Raj on 06-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ShippingInfoResponse implements Serializable {

    @SerializedName("message")
    private String message;

    @SerializedName("payment_methods")
    private ArrayList<PaymentMethod> payment_methods;

    @SerializedName("totals")
    private ShippingTotal totals;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<PaymentMethod> getPayment_methods() {
        return payment_methods;
    }

    public void setPayment_methods(ArrayList<PaymentMethod> payment_methods) {
        this.payment_methods = payment_methods;
    }

    public ShippingTotal getTotals() {
        return totals;
    }

    public void setTotals(ShippingTotal totals) {
        this.totals = totals;
    }
}
