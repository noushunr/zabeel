package com.greenflames.myzebeel.models.error;

/**
 * Created by Noushad N on 08-04-2022.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Parameters implements Serializable {

    @SerializedName("fieldName")
    @Expose
    private String fieldName;
    @SerializedName("fieldValue")
    @Expose
    private String fieldValue;
    @SerializedName("field2Name")
    @Expose
    private String field2Name;
    @SerializedName("field2Value")
    @Expose
    private String field2Value;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getField2Name() {
        return field2Name;
    }

    public void setField2Name(String field2Name) {
        this.field2Name = field2Name;
    }

    public String getField2Value() {
        return field2Value;
    }

    public void setField2Value(String field2Value) {
        this.field2Value = field2Value;
    }

}
