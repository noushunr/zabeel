package com.greenflames.myzebeel.models.detailproducts;

/*
 *Created by Adithya T Raj on 03-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ConfigProductOptions implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("attribute_id")
    private String attribute_id;

    @SerializedName("label")
    private String label;

    @SerializedName("position")
    private String position;

    @SerializedName("values")
    private ArrayList<ConfigValues> values;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttribute_id() {
        return attribute_id;
    }

    public void setAttribute_id(String attribute_id) {
        this.attribute_id = attribute_id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public ArrayList<ConfigValues> getValues() {
        return values;
    }

    public void setValues(ArrayList<ConfigValues> values) {
        this.values = values;
    }
}
