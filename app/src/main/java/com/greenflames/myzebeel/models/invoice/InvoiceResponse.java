package com.greenflames.myzebeel.models.invoice;

/*
 *Created by Adithya T Raj on 08-05-2021
 */

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class InvoiceResponse implements Serializable {

    @SerializedName("message")
    private String message;

    @SerializedName("grand_total")
    private String grand_total;

    @SerializedName("subtotal")
    private String subtotal;

    @Nullable
    @SerializedName("discount_amount")
    private String discount_amount;

    @SerializedName("shipping_amount")
    private String shipping_amount;

    @SerializedName("base_currency_code")
    private String base_currency_code;

    @SerializedName("tax_amount")
    private String tax_amount;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGrand_total() {
        return grand_total;
    }

    public void setGrand_total(String grand_total) {
        this.grand_total = grand_total;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    @Nullable
    public String getDiscount_amount() {
        return discount_amount;
    }

    public void setDiscount_amount(@Nullable String discount_amount) {
        this.discount_amount = discount_amount;
    }

    public String getShipping_amount() {
        return shipping_amount;
    }

    public void setShipping_amount(String shipping_amount) {
        this.shipping_amount = shipping_amount;
    }

    public String getBase_currency_code() {
        return base_currency_code;
    }

    public void setBase_currency_code(String base_currency_code) {
        this.base_currency_code = base_currency_code;
    }

    public String getTax_amount() {
        return tax_amount;
    }

    public void setTax_amount(String tax_amount) {
        this.tax_amount = tax_amount;
    }
}
