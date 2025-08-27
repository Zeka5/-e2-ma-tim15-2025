package com.example.ma_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 5000;

    private Handler splashHandler;
    private Runnable splashRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initializeSplashTimer();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startSplashTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSplashTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanupSplashTimer();
    }

    private void initializeSplashTimer() {
        splashHandler = new Handler(Looper.getMainLooper());
        splashRunnable = ()-> navigateToLogin();
    }

    private void startSplashTimer() {
        if (splashHandler != null && splashRunnable != null) {
            splashHandler.postDelayed(splashRunnable, SPLASH_DELAY_MS);
        }
    }

    private void stopSplashTimer() {
        if (splashHandler != null && splashRunnable != null) {
            splashHandler.removeCallbacks(splashRunnable);
        }
    }

    private void cleanupSplashTimer() {
        stopSplashTimer();
        splashHandler = null;
        splashRunnable = null;
    }

    private void navigateToLogin() {
        Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}