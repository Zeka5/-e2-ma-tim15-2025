package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;

public class GuildBossMissionProgress {
    @SerializedName("id")
    private Long id;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("username")
    private String username;

    @SerializedName("shopPurchases")
    private Integer shopPurchases;

    @SerializedName("bossHits")
    private Integer bossHits;

    @SerializedName("easyTasksCompleted")
    private Integer easyTasksCompleted;

    @SerializedName("hardTasksCompleted")
    private Integer hardTasksCompleted;

    @SerializedName("noUncompletedTasks")
    private Boolean noUncompletedTasks;

    @SerializedName("daysWithMessages")
    private Integer daysWithMessages;

    @SerializedName("totalDamageDealt")
    private Integer totalDamageDealt;

    @SerializedName("totalTasksCompleted")
    private Integer totalTasksCompleted;

    @SerializedName("badge")
    private String badge;

    @SerializedName("badgeTitle")
    private String badgeTitle;

    public GuildBossMissionProgress() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getShopPurchases() {
        return shopPurchases;
    }

    public void setShopPurchases(Integer shopPurchases) {
        this.shopPurchases = shopPurchases;
    }

    public Integer getBossHits() {
        return bossHits;
    }

    public void setBossHits(Integer bossHits) {
        this.bossHits = bossHits;
    }

    public Integer getEasyTasksCompleted() {
        return easyTasksCompleted;
    }

    public void setEasyTasksCompleted(Integer easyTasksCompleted) {
        this.easyTasksCompleted = easyTasksCompleted;
    }

    public Integer getHardTasksCompleted() {
        return hardTasksCompleted;
    }

    public void setHardTasksCompleted(Integer hardTasksCompleted) {
        this.hardTasksCompleted = hardTasksCompleted;
    }

    public Boolean getNoUncompletedTasks() {
        return noUncompletedTasks;
    }

    public void setNoUncompletedTasks(Boolean noUncompletedTasks) {
        this.noUncompletedTasks = noUncompletedTasks;
    }

    public Integer getDaysWithMessages() {
        return daysWithMessages;
    }

    public void setDaysWithMessages(Integer daysWithMessages) {
        this.daysWithMessages = daysWithMessages;
    }

    public Integer getTotalDamageDealt() {
        return totalDamageDealt;
    }

    public void setTotalDamageDealt(Integer totalDamageDealt) {
        this.totalDamageDealt = totalDamageDealt;
    }

    public Integer getTotalTasksCompleted() {
        return totalTasksCompleted;
    }

    public void setTotalTasksCompleted(Integer totalTasksCompleted) {
        this.totalTasksCompleted = totalTasksCompleted;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getBadgeTitle() {
        return badgeTitle;
    }

    public void setBadgeTitle(String badgeTitle) {
        this.badgeTitle = badgeTitle;
    }
}
