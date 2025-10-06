package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;

public class UserPotion {
    @SerializedName("id")
    private Long id;

    @SerializedName("potionTemplateId")
    private Long potionTemplateId;

    @SerializedName("name")
    private String name;

    @SerializedName("powerBonus")
    private Integer powerBonus;

    @SerializedName("isPermanent")
    private Boolean isPermanent;

    @SerializedName("quantity")
    private Integer quantity;

    @SerializedName("isActivated")
    private Boolean isActivated;

    @SerializedName("acquiredAt")
    private String acquiredAt;

    @SerializedName("description")
    private String description;

    @SerializedName("iconUrl")
    private String iconUrl;

    public UserPotion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPotionTemplateId() {
        return potionTemplateId;
    }

    public void setPotionTemplateId(Long potionTemplateId) {
        this.potionTemplateId = potionTemplateId;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(Boolean isActivated) {
        this.isActivated = isActivated;
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
