package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;

public class BossBattle {
    @SerializedName("id")
    private Long id;

    @SerializedName("boss")
    private Boss boss;

    @SerializedName("currentHp")
    private Integer currentHp;

    @SerializedName("attacksUsed")
    private Integer attacksUsed;

    @SerializedName("status")
    private String status;

    @SerializedName("coinsEarned")
    private Integer coinsEarned;

    @SerializedName("equipmentEarnedType")
    private String equipmentEarnedType;

    @SerializedName("equipmentEarnedName")
    private String equipmentEarnedName;

    @SerializedName("userPpAtBattle")
    private Integer userPpAtBattle;

    @SerializedName("successRateAtBattle")
    private Double successRateAtBattle;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("completedAt")
    private String completedAt;

    public BossBattle() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boss getBoss() {
        return boss;
    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }

    public Integer getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(Integer currentHp) {
        this.currentHp = currentHp;
    }

    public Integer getAttacksUsed() {
        return attacksUsed;
    }

    public void setAttacksUsed(Integer attacksUsed) {
        this.attacksUsed = attacksUsed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCoinsEarned() {
        return coinsEarned;
    }

    public void setCoinsEarned(Integer coinsEarned) {
        this.coinsEarned = coinsEarned;
    }

    public String getEquipmentEarnedType() {
        return equipmentEarnedType;
    }

    public void setEquipmentEarnedType(String equipmentEarnedType) {
        this.equipmentEarnedType = equipmentEarnedType;
    }

    public String getEquipmentEarnedName() {
        return equipmentEarnedName;
    }

    public void setEquipmentEarnedName(String equipmentEarnedName) {
        this.equipmentEarnedName = equipmentEarnedName;
    }

    public Integer getUserPpAtBattle() {
        return userPpAtBattle;
    }

    public void setUserPpAtBattle(Integer userPpAtBattle) {
        this.userPpAtBattle = userPpAtBattle;
    }

    public Double getSuccessRateAtBattle() {
        return successRateAtBattle;
    }

    public void setSuccessRateAtBattle(Double successRateAtBattle) {
        this.successRateAtBattle = successRateAtBattle;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }
}
