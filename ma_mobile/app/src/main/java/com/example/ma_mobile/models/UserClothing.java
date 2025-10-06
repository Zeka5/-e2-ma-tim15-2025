package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;

public class UserClothing {
    @SerializedName("id")
    private Long id;

    @SerializedName("clothingTemplateId")
    private Long clothingTemplateId;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;

    @SerializedName("accumulatedBonus")
    private Integer accumulatedBonus;

    @SerializedName("battlesRemaining")
    private Integer battlesRemaining;

    @SerializedName("isActive")
    private Boolean isActive;

    @SerializedName("acquiredAt")
    private String acquiredAt;

    @SerializedName("description")
    private String description;

    @SerializedName("iconUrl")
    private String iconUrl;

    public UserClothing() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClothingTemplateId() {
        return clothingTemplateId;
    }

    public void setClothingTemplateId(Long clothingTemplateId) {
        this.clothingTemplateId = clothingTemplateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getAccumulatedBonus() {
        return accumulatedBonus;
    }

    public void setAccumulatedBonus(Integer accumulatedBonus) {
        this.accumulatedBonus = accumulatedBonus;
    }

    public Integer getBattlesRemaining() {
        return battlesRemaining;
    }

    public void setBattlesRemaining(Integer battlesRemaining) {
        this.battlesRemaining = battlesRemaining;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getAcquiredAt() {
        return acquiredAt;
    }

    public void setAcquiredAt(String acquiredAt) {
        this.acquiredAt = acquiredAt;
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
