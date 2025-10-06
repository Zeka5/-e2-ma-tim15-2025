package com.example.ma_mobile.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ma_mobile.ShopListFragment;

public class ShopPagerAdapter extends FragmentStateAdapter {

    private ShopListFragment potionsFragment;
    private ShopListFragment clothingFragment;

    public ShopPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                potionsFragment = ShopListFragment.newInstance(ShopListFragment.TYPE_POTIONS);
                return potionsFragment;
            case 1:
                clothingFragment = ShopListFragment.newInstance(ShopListFragment.TYPE_CLOTHING);
                return clothingFragment;
            default:
                return ShopListFragment.newInstance(ShopListFragment.TYPE_POTIONS);
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Potions, Clothing
    }
}
