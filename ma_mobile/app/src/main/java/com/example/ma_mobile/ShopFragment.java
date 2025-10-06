package com.example.ma_mobile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ma_mobile.adapter.ShopPagerAdapter;
import com.example.ma_mobile.models.User;
import com.example.ma_mobile.repository.UserRepository;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ShopFragment extends Fragment {

    private static final String TAG = "ShopFragment";

    private ImageButton btnBack;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TextView tvUserCoins;
    private ProgressBar progressBar;

    private ShopPagerAdapter pagerAdapter;
    private UserRepository userRepository;

    public ShopFragment() {
        // Required empty public constructor
    }

    public static ShopFragment newInstance() {
        return new ShopFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRepository = new UserRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupViewPager();
        setupListeners();
        loadUserCoins();
    }

    private void initializeViews(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        tabLayout = view.findViewById(R.id.tab_layout_shop);
        viewPager = view.findViewById(R.id.view_pager_shop);
        tvUserCoins = view.findViewById(R.id.tv_user_coins);
        progressBar = view.findViewById(R.id.progress_bar_shop);
    }

    private void setupViewPager() {
        pagerAdapter = new ShopPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Potions");
                    break;
                case 1:
                    tab.setText("Clothing");
                    break;
            }
        }).attach();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void loadUserCoins() {
        userRepository.getCurrentUserProfile(new UserRepository.UserProfileCallback() {
            @Override
            public void onSuccess(User user) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (user.getGameStats() != null && user.getGameStats().getCoins() != null) {
                            tvUserCoins.setText(user.getGameStats().getCoins() + " coins");
                        } else {
                            tvUserCoins.setText("0 coins");
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Log.e(TAG, "Error loading user coins: " + error);
                        tvUserCoins.setText("0 coins");
                    });
                }
            }
        });
    }

    public void refreshCoins() {
        loadUserCoins();
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
