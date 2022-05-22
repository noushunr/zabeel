package com.greenflames.myzebeel.models.cart;

/*
 *Created by Adithya T Raj on 10-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CartProductExtensionAttributes implements Serializable {

    @SerializedName("CartConfigItemOption")
    private ArrayList<CartConfigItemOption> CartConfigItemOption;

    public ArrayList<CartConfigItemOption> getCartConfigItemOption() {
        return CartConfigItemOption;
    }

    public void setCartConfigItemOption(ArrayList<CartConfigItemOption> cartConfigItemOption) {
        CartConfigItemOption = cartConfigItemOption;
    }
}
