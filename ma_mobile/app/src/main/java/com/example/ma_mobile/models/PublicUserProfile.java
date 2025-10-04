package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class PublicUserProfile implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("username")
    private String username;

    @SerializedName("avatarId")
    private Integer avatarId;

    @SerializedName("level")
    private Integer level;

    @SerializedName("title")
    private String title;

    @SerializedName("experiencePoints")
    private Integer experiencePoints;

    @SerializedName("qrCode")
    private String qrCode;

    @SerializedName("badgeCount")
    private Integer badgeCount;

    @SerializedName("badges")
    private List<Badge> badges;

    public PublicUserProfile() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Integer avatarId) {
        this.avatarId = avatarId;
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

    public Integer getExperiencePoints() {
        return experiencePoints;
    }

    public void setExperiencePoints(Integer experiencePoints) {
        this.experiencePoints = experiencePoints;
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

    public int getAvatarDrawableId() {
        switch (avatarId != null ? avatarId : 1) {
            case 1: return com.example.ma_mobile.R.drawable.avatar_1;
            case 2: return com.example.ma_mobile.R.drawable.avatar_2;
            case 3: return com.example.ma_mobile.R.drawable.avatar_3;
            case 4: return com.example.ma_mobile.R.drawable.avatar_4;
            case 5: return com.example.ma_mobile.R.drawable.avatar_5;
            default: return com.example.ma_mobile.R.drawable.avatar_1;
        }
    }
}
