package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserGameStats {
    @SerializedName("id")
    private Long id;

    @SerializedName("level")
    private Integer level;

    @SerializedName("title")
    private String title;

    @SerializedName("powerPoints")
    private Integer powerPoints;

    @SerializedName("experiencePoints")
    private Integer experiencePoints;

    @SerializedName("coins")
    private Integer coins;

    @SerializedName("qrCode")
    private String qrCode;

    @SerializedName("badgeCount")
    private Integer badgeCount;

    @SerializedName("badges")
    private List<Badge> badges;

    public UserGameStats() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPowerPoints() {
        return powerPoints;
    }

    public void setPowerPoints(Integer powerPoints) {
        this.powerPoints = powerPoints;
    }

    public Integer getExperiencePoints() {
        return experiencePoints;
    }

    public void setExperiencePoints(Integer experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = coins;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Integer getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(Integer badgeCount) {
        this.badgeCount = badgeCount;
    }

    public List<Badge> getBadges() {
        return badges;
    }

    public void setBadges(List<Badge> badges) {
        this.badges = badges;
    }
}
