package com.example.ma_mobile.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserEquipment {
    @SerializedName("potions")
    private List<UserPotion> potions;

    @SerializedName("clothing")
    private List<UserClothing> clothing;

    @SerializedName("weapons")
    private List<UserWeapon> weapons;

    public UserEquipment() {
    }

    public List<UserPotion> getPotions() {
        return potions;
    }

    public void setPotions(List<UserPotion> potions) {
        this.potions = potions;
    }

    public List<UserClothing> getClothing() {
        return clothing;
    }

    public void setClothing(List<UserClothing> clothing) {
        this.clothing = clothing;
    }

    public List<UserWeapon> getWeapons() {
        return weapons;
    }

    public void setWeapons(List<UserWeapon> weapons) {
        this.weapons = weapons;
    }
}
