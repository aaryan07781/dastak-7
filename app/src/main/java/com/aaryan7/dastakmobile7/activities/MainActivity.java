package com.aaryan7.dastakmobile7.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.aaryan7.dastakmobile7.R;
import com.aaryan7.dastakmobile7.fragments.BillingFragment;
import com.aaryan7.dastakmobile7.fragments.ProductFragment;
import com.aaryan7.dastakmobile7.fragments.SalesFragment;
import com.aaryan7.dastakmobile7.utils.GreetingUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Main Activity for the app
 */
public class MainActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private TextView tvGreeting;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        tvGreeting = findViewById(R.id.tv_greeting);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        
        // Set up toolbar
        setSupportActionBar(toolbar);
        
        // Set up greeting
        updateGreeting();
        
        // Set up bottom navigation
        setupBottomNavigation();
        
        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new ProductFragment());
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Update greeting when activity resumes
        updateGreeting();
    }
    
    /**
     * Update greeting based on time of day
     */
    private void updateGreeting() {
        String greeting = GreetingUtils.getGreetingMessage();
        tvGreeting.setText(greeting);
    }
    
    /**
     * Set up bottom navigation
     */
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            
            int itemId = item.getItemId();
            if (itemId == R.id.nav_products) {
                fragment = new ProductFragment();
                toolbar.setTitle("Products");
            } else if (itemId == R.id.nav_billing) {
                fragment = new BillingFragment();
                toolbar.setTitle("Billing");
            } else if (itemId == R.id.nav_sales) {
                fragment = new SalesFragment();
                toolbar.setTitle("Sales Analysis");
            }
            
            return loadFragment(fragment);
        });
    }
    
    /**
     * Load fragment
     * @param fragment Fragment to load
     * @return true if successful, false otherwise
     */
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
