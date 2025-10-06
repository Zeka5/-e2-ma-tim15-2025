package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;

public class PotionTemplate {
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("powerBonus")
    private Integer powerBonus;

    @SerializedName("isPermanent")
    private Boolean isPermanent;

    @SerializedName("calculatedPrice")
    private Integer calculatedPrice;

    @SerializedName("description")
    private String description;

    @SerializedName("iconUrl")
    private String iconUrl;

    public PotionTemplate() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPowerBonus() {
        return powerBonus;
    }

    public void setPowerBonus(Integer powerBonus) {
        this.powerBonus = powerBonus;
    }

    public Boolean getIsPermanent() {
        return isPermanent;
    }

    public void setIsPermanent(Boolean isPermanent) {
        this.isPermanent = isPermanent;
    }

    public Integer getCalculatedPrice() {
        return calculatedPrice;
    }

    public void setCalculatedPrice(Integer calculatedPrice) {
        this.calculatedPrice = calculatedPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
