package com.greenflames.myzebeel.models.address;

/*
 *Created by Adithya T Raj on 09-05-2021
 */

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CountryModel implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("three_letter_abbreviation")
    private String three_letter_abbreviation;

    @SerializedName("full_name_english")
    private String full_name_english;

    @Nullable
    @SerializedName("available_regions")
    private ArrayList<StateModel> available_regions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThree_letter_abbreviation() {
        return three_letter_abbreviation;
    }

    public void setThree_letter_abbreviation(String three_letter_abbreviation) {
        this.three_letter_abbreviation = three_letter_abbreviation;
    }

    public String getFull_name_english() {
        return full_name_english;
    }

    public void setFull_name_english(String full_name_english) {
        this.full_name_english = full_name_english;
    }

    @Nullable
    public ArrayList<StateModel> getAvailable_regions() {
        return available_regions;
    }

    public void setAvailable_regions(@Nullable ArrayList<StateModel> available_regions) {
        this.available_regions = available_regions;
    }
}
