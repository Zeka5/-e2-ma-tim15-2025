package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;

public class AttackResponse {
    @SerializedName("hit")
    private Boolean hit;

    @SerializedName("damageDealt")
    private Integer damageDealt;

    @SerializedName("bossCurrentHp")
    private Integer bossCurrentHp;

    @SerializedName("attacksRemaining")
    private Integer attacksRemaining;

    @SerializedName("battleComplete")
    private Boolean battleComplete;

    @SerializedName("battleResult")
    private String battleResult;

    @SerializedName("rewards")
    private BattleRewards rewards;

    public AttackResponse() {
    }

    public Boolean getHit() {
        return hit;
    }

    public void setHit(Boolean hit) {
        this.hit = hit;
    }

    public Integer getDamageDealt() {
        return damageDealt;
    }

    public void setDamageDealt(Integer damageDealt) {
        this.damageDealt = damageDealt;
    }

    public Integer getBossCurrentHp() {
        return bossCurrentHp;
    }

    public void setBossCurrentHp(Integer bossCurrentHp) {
        this.bossCurrentHp = bossCurrentHp;
    }

    public Integer getAttacksRemaining() {
        return attacksRemaining;
    }

    public void setAttacksRemaining(Integer attacksRemaining) {
        this.attacksRemaining = attacksRemaining;
    }

    public Boolean getBattleComplete() {
        return battleComplete;
    }

    public void setBattleComplete(Boolean battleComplete) {
        this.battleComplete = battleComplete;
    }

    public String getBattleResult() {
        return battleResult;
    }

    public void setBattleResult(String battleResult) {
        this.battleResult = battleResult;
    }

    public BattleRewards getRewards() {
        return rewards;
    }

    public void setRewards(BattleRewards rewards) {
        this.rewards = rewards;
    }
}
