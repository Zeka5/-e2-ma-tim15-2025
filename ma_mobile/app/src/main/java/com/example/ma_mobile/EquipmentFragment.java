package com.example.ma_mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ma_mobile.adapter.EquipmentPagerAdapter;
import com.example.ma_mobile.repository.EquipmentRepository;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class EquipmentFragment extends Fragment {

    private static final String TAG = "EquipmentFragment";

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private Button btnOpenShop;
    private ProgressBar progressBar;

    private EquipmentRepository equipmentRepository;
    private EquipmentPagerAdapter pagerAdapter;

    public EquipmentFragment() {
        // Required empty public constructor
    }

    public static EquipmentFragment newInstance() {
        return new EquipmentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        equipmentRepository = new EquipmentRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_equipment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupViewPager();
        setupListeners();
    }

    private void initializeViews(View view) {
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        btnOpenShop = view.findViewById(R.id.btn_open_shop);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupViewPager() {
        pagerAdapter = new EquipmentPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Potions");
                    break;
                case 1:
                    tab.setText("Clothing");
                    break;
                case 2:
                    tab.setText("Weapons");
                    break;
            }
        }).attach();
    }

    private void setupListeners() {
        btnOpenShop.setOnClickListener(v -> navigateToShop());
    }

    private void navigateToShop() {
        if (getActivity() != null) {
            ShopFragment shopFragment = ShopFragment.newInstance();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, shopFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh equipment when returning from shop
        refreshEquipment();
    }

    private void refreshEquipment() {
        if (pagerAdapter != null) {
            pagerAdapter.refreshAll();
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
