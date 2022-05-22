package com.greenflames.myzebeel.models.error;

/*
 *Created by Adithya T Raj on 26-04-2021
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ErrorMessageResponse implements Serializable {

    @SerializedName("message")
    private String message;
    @SerializedName("parameters")
    @Expose
    private Parameters parameters;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }
}
