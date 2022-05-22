package com.greenflames.myzebeel.models.products;

/*
 *Created by Adithya T Raj on 29-04-2021
 */

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MediaGalleryEntries implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("media_type")
    private String media_type;

    @SerializedName("file")
    private String file;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
