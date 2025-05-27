package com.aaryan7.dastakmobile7.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for trial and developer activation
 */
public class TrialManager {
    private static final String PREF_NAME = "trial_prefs";
    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_DEVELOPER_MODE = "developer_mode";
    private static final int TRIAL_DAYS = 5;
    
    private static final String DEVELOPER_ID = "Aaryan7";
    private static final String DEVELOPER_PASSWORD = "Aaryan2242019@@";
    
    private SharedPreferences preferences;
    
    public TrialManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        checkFirstLaunch();
    }
    
    /**
     * Check if this is the first launch and save date if it is
     */
    private void checkFirstLaunch() {
        long firstLaunch = preferences.getLong(KEY_FIRST_LAUNCH, 0);
        
        if (firstLaunch == 0) {
            // First launch, save current date
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(KEY_FIRST_LAUNCH, System.currentTimeMillis());
            editor.apply();
        }
    }
    
    /**
     * Check if trial is active
     * @return true if trial is active, false otherwise
     */
    public boolean isTrialActive() {
        // If developer mode is active, always return true
        if (isDeveloperMode()) {
            return true;
        }
        
        long firstLaunch = preferences.getLong(KEY_FIRST_LAUNCH, 0);
        
        if (firstLaunch == 0) {
            return true; // First launch, trial is active
        }
        
        // Calculate days since first launch
        long currentTime = System.currentTimeMillis();
        long diffInMillis = currentTime - firstLaunch;
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        
        return diffInDays < TRIAL_DAYS;
    }
    
    /**
     * Get remaining trial days
     * @return Number of remaining trial days
     */
    public int getRemainingTrialDays() {
        if (isDeveloperMode()) {
            return Integer.MAX_VALUE; // Unlimited for developer mode
        }
        
        long firstLaunch = preferences.getLong(KEY_FIRST_LAUNCH, 0);
        
        if (firstLaunch == 0) {
            return TRIAL_DAYS; // First launch, full trial period
        }
        
        // Calculate days since first launch
        long currentTime = System.currentTimeMillis();
        long diffInMillis = currentTime - firstLaunch;
        long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);
        
        int remainingDays = (int) (TRIAL_DAYS - diffInDays);
        return Math.max(0, remainingDays);
    }
    
    /**
     * Check if developer mode is active
     * @return true if developer mode is active, false otherwise
     */
    public boolean isDeveloperMode() {
        return preferences.getBoolean(KEY_DEVELOPER_MODE, false);
    }
    
    /**
     * Activate developer mode
     * @param id Developer ID
     * @param password Developer password
     * @return true if activation successful, false otherwise
     */
    public boolean activateDeveloperMode(String id, String password) {
        if (DEVELOPER_ID.equals(id) && DEVELOPER_PASSWORD.equals(password)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_DEVELOPER_MODE, true);
            editor.apply();
            return true;
        }
        return false;
    }
    
    /**
     * Reset trial period (for testing purposes)
     */
    public void resetTrial() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_FIRST_LAUNCH);
        editor.apply();
        checkFirstLaunch();
    }
}
