package com.greenflames.myzebeel.models.address;

/*
 *Created by Adithya T Raj on 09-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StateModel implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("code")
    private String code;

    @SerializedName("name")
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
