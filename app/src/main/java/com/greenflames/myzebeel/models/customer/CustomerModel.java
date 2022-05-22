package com.greenflames.myzebeel.models.customer;

/*
 *Created by Adithya T Raj on 06-05-2021
 */

import com.greenflames.myzebeel.models.address.AddressModel;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CustomerModel implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("firstname")
    private String firstname;

    @SerializedName("lastname")
    private String lastname;

    @SerializedName("addresses")
    private ArrayList<AddressModel> addresses;

    @SerializedName("website_id")
    private String website_id;

    @SerializedName("store_id")
    private String storeId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public ArrayList<AddressModel> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<AddressModel> addresses) {
        this.addresses = addresses;
    }

    public String getWebsite_id() {
        return website_id;
    }

    public void setWebsite_id(String website_id) {
        this.website_id = website_id;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
