package com.greenflames.myzebeel.models.signup;

/*
 *Created by Adithya T Raj on 28-04-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SignUpResponse implements Serializable {

    @SerializedName("message")
    private String message;

    @SerializedName("id")
    private String id;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
