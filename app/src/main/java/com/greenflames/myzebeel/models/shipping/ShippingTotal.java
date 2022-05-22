package com.greenflames.myzebeel.models.shipping;

/*
 *Created by Adithya T Raj on 06-05-2021
 */

import com.greenflames.myzebeel.models.cart.CartTotalSegments;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ShippingTotal implements Serializable {

    @SerializedName("total_segments")
    private ArrayList<CartTotalSegments> total_segments;

    public ArrayList<CartTotalSegments> getTotal_segments() {
        return total_segments;
    }

    public void setTotal_segments(ArrayList<CartTotalSegments> total_segments) {
        this.total_segments = total_segments;
    }
}
