package com.example.ma_mobile;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigation;

    private String userEmail;

    private Fragment currentFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        extractUserDataFromIntent();
        initializeViews();
        setupToolbar();
        setupBottomNavigation();

        // Load home fragment by default
        if (savedInstanceState == null) {
            loadHomeFragment();
        }
    }

    private void extractUserDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            userEmail = intent.getStringExtra("USER_EMAIL");
        }
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);

        updateToolbarTitle("Home");
    }
    private void updateToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                loadHomeFragment();
                updateToolbarTitle("Home");
                return true;
            } else if (itemId == R.id.nav_tasks) {
                loadTasksFragment();
                updateToolbarTitle("Tasks");
                return true;
//            } else if (itemId == R.id.nav_categories) {
//                loadCategoriesFragment();
//                updateToolbarTitle("Categories");
            } else if (itemId == R.id.nav_community) {
                loadCommunityFragment();
                updateToolbarTitle("Community");
                return true;
//            } else if (itemId == R.id.nav_notifications) {
//                loadNotificationsFragment();
//                updateToolbarTitle("Notifications");
//                return true;
//            } else if (itemId == R.id.nav_account) {
//                loadAccountFragment();
//                updateToolbarTitle("Account");
//                return true;
            }

            return false;
        });
    }

    private void loadHomeFragment() {
        HomeFragment fragment = HomeFragment.newInstance(userEmail);
        loadFragment(fragment, "HomeFragment");
    }

    private void loadTasksFragment() {
        TasksFragment fragment = new TasksFragment();
        loadFragment(fragment, "TasksFragment");
    }

    private void loadCategoriesFragment() {
        CategoriesFragment fragment = new CategoriesFragment();
        loadFragment(fragment, "CategoriesFragment");
    }

    private void loadNotificationsFragment() {
        NotificationsFragment fragment = new NotificationsFragment();
        loadFragment(fragment, "NotificationsFragment");
    }

    private void loadAccountFragment() {
        AccountFragment fragment = AccountFragment.newInstance(userEmail);
        loadFragment(fragment, "AccountFragment");
    }

    private void loadCommunityFragment() {
        CommunityFragment fragment = CommunityFragment.newInstance();
        loadFragment(fragment, "CommunityFragment");
    }

    private void loadFragment(Fragment fragment, String tag) {
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, fragment, tag);
        transaction.commit();

        currentFragment = fragment;
    }

    private void setupBackPressHandling() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (bottomNavigation.getSelectedItemId() != R.id.nav_home) {
                    bottomNavigation.setSelectedItemId(R.id.nav_home);
                } else {
                    // ako nema home, izadji iz app
                    finish();
                }
            }
        });
    }
}