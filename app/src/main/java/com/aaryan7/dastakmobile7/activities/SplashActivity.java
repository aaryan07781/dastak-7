package com.aaryan7.dastakmobile7.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.aaryan7.dastakmobile7.R;
import com.aaryan7.dastakmobile7.utils.TrialManager;

/**
 * Splash Activity for app launch
 */
public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2000; // 2 seconds
    private TextView tvAppName, tvDeveloper;
    private TrialManager trialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        // Initialize views
        tvAppName = findViewById(R.id.tv_app_name);
        tvDeveloper = findViewById(R.id.tv_developer);
        
        // Initialize trial manager
        trialManager = new TrialManager(this);
        
        // Animate views
        animateViews();
        
        // Navigate to appropriate activity after delay
        new Handler().postDelayed(this::navigateNext, SPLASH_DURATION);
    }
    
    /**
     * Animate views with fade-in and scale
     */
    private void animateViews() {
        // Set initial alpha and scale
        tvAppName.setAlpha(0f);
        tvAppName.setScaleX(0.8f);
        tvAppName.setScaleY(0.8f);
        tvDeveloper.setAlpha(0f);
        
        // Animate app name
        tvAppName.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1000)
                .start();
        
        // Animate developer text with delay
        tvDeveloper.animate()
                .alpha(1f)
                .setStartDelay(500)
                .setDuration(500)
                .start();
    }
    
    /**
     * Navigate to next activity based on trial status
     */
    private void navigateNext() {
        Intent intent;
        
        if (trialManager.isTrialActive()) {
            // Trial is active, go to main activity
            intent = new Intent(this, MainActivity.class);
        } else {
            // Trial expired, go to trial activity
            intent = new Intent(this, TrialActivity.class);
        }
        
        startActivity(intent);
        finish();
    }
}
