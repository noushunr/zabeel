package com.greenflames.myzebeel.models.order;

/*
 *Created by Adithya T Raj on 08-05-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StatusHistory implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("entity_id")
    private String entity_id;

    @SerializedName("entity_name")
    private String entity_name;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }

    public String getEntity_name() {
        return entity_name;
    }

    public void setEntity_name(String entity_name) {
        this.entity_name = entity_name;
    }
}
