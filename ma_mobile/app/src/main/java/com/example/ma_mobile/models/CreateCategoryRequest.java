package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;

public class CreateCategoryRequest {
    @SerializedName("name")
    private String name;

    @SerializedName("color")
    private String color;

    public CreateCategoryRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
