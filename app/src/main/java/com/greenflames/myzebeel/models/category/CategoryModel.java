package com.greenflames.myzebeel.models.category;

/*
 *Created by Adithya T Raj on 29-04-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CategoryModel implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("position")
    private String position;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
