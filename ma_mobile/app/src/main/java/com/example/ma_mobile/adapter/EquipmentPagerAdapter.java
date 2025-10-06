package com.example.ma_mobile.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ma_mobile.EquipmentListFragment;

public class EquipmentPagerAdapter extends FragmentStateAdapter {

    private EquipmentListFragment potionsFragment;
    private EquipmentListFragment clothingFragment;
    private EquipmentListFragment weaponsFragment;

    public EquipmentPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                potionsFragment = EquipmentListFragment.newInstance(EquipmentListFragment.TYPE_POTIONS);
                return potionsFragment;
            case 1:
                clothingFragment = EquipmentListFragment.newInstance(EquipmentListFragment.TYPE_CLOTHING);
                return clothingFragment;
            case 2:
                weaponsFragment = EquipmentListFragment.newInstance(EquipmentListFragment.TYPE_WEAPONS);
                return weaponsFragment;
            default:
                return EquipmentListFragment.newInstance(EquipmentListFragment.TYPE_POTIONS);
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Potions, Clothing, Weapons
    }

    public void refreshAll() {
        if (potionsFragment != null) {
            potionsFragment.loadEquipment();
        }
        if (clothingFragment != null) {
            clothingFragment.loadEquipment();
        }
        if (weaponsFragment != null) {
            weaponsFragment.loadEquipment();
        }
    }
}
