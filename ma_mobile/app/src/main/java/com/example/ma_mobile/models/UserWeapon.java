package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;

public class UserWeapon {
    @SerializedName("id")
    private Long id;

    @SerializedName("weaponTemplateId")
    private Long weaponTemplateId;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;

    @SerializedName("currentBonusPercentage")
    private Double currentBonusPercentage;

    @SerializedName("upgradeLevel")
    private Integer upgradeLevel;

    @SerializedName("duplicateCount")
    private Integer duplicateCount;

    @SerializedName("acquiredAt")
    private String acquiredAt;

    @SerializedName("description")
    private String description;

    @SerializedName("iconUrl")
    private String iconUrl;

    public UserWeapon() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWeaponTemplateId() {
        return weaponTemplateId;
    }

    public void setWeaponTemplateId(Long weaponTemplateId) {
        this.weaponTemplateId = weaponTemplateId;
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

    public Double getCurrentBonusPercentage() {
        return currentBonusPercentage;
    }

    public void setCurrentBonusPercentage(Double currentBonusPercentage) {
        this.currentBonusPercentage = currentBonusPercentage;
    }

    public Integer getUpgradeLevel() {
        return upgradeLevel;
    }

    public void setUpgradeLevel(Integer upgradeLevel) {
        this.upgradeLevel = upgradeLevel;
    }

    public Integer getDuplicateCount() {
        return duplicateCount;
    }

    public void setDuplicateCount(Integer duplicateCount) {
        this.duplicateCount = duplicateCount;
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
