package com.greenflames.myzebeel.models.address;

/*
 *Created by Adithya T Raj on 03-05-2021
 */

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class AddressModel implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("customer_id")
    private String customer_id;

    @SerializedName("region")
    private AddressRegion region;

    @SerializedName("region_id")
    private String region_id;

    @SerializedName("country_id")
    private String country_id;

    @SerializedName("street")
    private ArrayList<String> street;

    @SerializedName("telephone")
    private String telephone;

    @Nullable
    @SerializedName("company")
    private String company;

    @SerializedName("postcode")
    private String postcode;

    @SerializedName("city")
    private String city;

    @SerializedName("firstname")
    private String firstname;

    @SerializedName("lastname")
    private String lastname;

    @SerializedName("default_shipping")
    private Boolean default_shipping;

    @SerializedName("default_billing")
    private Boolean default_billing;

    @Nullable
    @SerializedName("custom_attributes")
    private ArrayList<AddressCustomAttributes> custom_attributes;

    public AddressModel(String id, String customer_id, AddressRegion region, String region_id, String country_id, ArrayList<String> street, String telephone, @Nullable String company, String postcode, String city, String firstname, String lastname, Boolean default_shipping, Boolean default_billing, @Nullable ArrayList<AddressCustomAttributes> custom_attributes) {
        this.id = id;
        this.customer_id = customer_id;
        this.region = region;
        this.region_id = region_id;
        this.country_id = country_id;
        this.street = street;
        this.telephone = telephone;
        this.company = company;
        this.postcode = postcode;
        this.city = city;
        this.firstname = firstname;
        this.lastname = lastname;
        this.default_shipping = default_shipping;
        this.default_billing = default_billing;
        this.custom_attributes = custom_attributes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public AddressRegion getRegion() {
        return region;
    }

    public void setRegion(AddressRegion region) {
        this.region = region;
    }

    public String getRegion_id() {
        return region_id;
    }

    public void setRegion_id(String region_id) {
        this.region_id = region_id;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
    }

    public ArrayList<String> getStreet() {
        return street;
    }

    public void setStreet(ArrayList<String> street) {
        this.street = street;
    }

    @Nullable
    public String getCompany() {
        return company;
    }

    public void setCompany(@Nullable String company) {
        this.company = company;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public Boolean getDefault_shipping() {
        return default_shipping;
    }

    public void setDefault_shipping(Boolean default_shipping) {
        this.default_shipping = default_shipping;
    }

    public Boolean getDefault_billing() {
        return default_billing;
    }

    public void setDefault_billing(Boolean default_billing) {
        this.default_billing = default_billing;
    }

    @Nullable
    public ArrayList<AddressCustomAttributes> getCustom_attributes() {
        return custom_attributes;
    }

    public void setCustom_attributes(@Nullable ArrayList<AddressCustomAttributes> custom_attributes) {
        this.custom_attributes = custom_attributes;
    }
}
