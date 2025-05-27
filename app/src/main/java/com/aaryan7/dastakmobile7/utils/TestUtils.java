package com.aaryan7.dastakmobile7.utils;

import android.content.Context;
import android.util.Log;

/**
 * Utility class for testing and debugging
 */
public class TestUtils {
    private static final String TAG = "TestUtils";
    
    /**
     * Test all app components
     * @param context Application context
     * @return Test results
     */
    public static String testAllComponents(Context context) {
        StringBuilder results = new StringBuilder();
        
        // Test database
        results.append("Database Test: ");
        try {
            // Test database creation
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            dbHelper.getWritableDatabase().close();
            results.append("PASSED\n");
        } catch (Exception e) {
            results.append("FAILED - ").append(e.getMessage()).append("\n");
            Log.e(TAG, "Database test failed", e);
        }
        
        // Test trial manager
        results.append("Trial Manager Test: ");
        try {
            TrialManager trialManager = new TrialManager(context);
            int remainingDays = trialManager.getRemainingTrialDays();
            results.append("PASSED (").append(remainingDays).append(" days remaining)\n");
        } catch (Exception e) {
            results.append("FAILED - ").append(e.getMessage()).append("\n");
            Log.e(TAG, "Trial manager test failed", e);
        }
        
        // Test greeting utils
        results.append("Greeting Utils Test: ");
        try {
            String greeting = GreetingUtils.getGreetingMessage();
            int timePeriod = GreetingUtils.getCurrentTimePeriod();
            results.append("PASSED (Current greeting: ").append(greeting).append(")\n");
        } catch (Exception e) {
            results.append("FAILED - ").append(e.getMessage()).append("\n");
            Log.e(TAG, "Greeting utils test failed", e);
        }
        
        // Test PDF generation
        results.append("PDF Generator Test: ");
        try {
            // Just test class loading, not actual generation
            Class.forName("com.aaryan7.dastakmobile7.utils.PDFGenerator");
            results.append("PASSED\n");
        } catch (Exception e) {
            results.append("FAILED - ").append(e.getMessage()).append("\n");
            Log.e(TAG, "PDF generator test failed", e);
        }
        
        // Test backup manager
        results.append("Backup Manager Test: ");
        try {
            BackupManager backupManager = new BackupManager(context);
            boolean signedIn = backupManager.isUserSignedIn();
            results.append("PASSED (Signed in: ").append(signedIn).append(")\n");
        } catch (Exception e) {
            results.append("FAILED - ").append(e.getMessage()).append("\n");
            Log.e(TAG, "Backup manager test failed", e);
        }
        
        return results.toString();
    }
    
    /**
     * Perform stress test on database
     * @param context Application context
     * @param iterations Number of iterations
     * @return Test results
     */
    public static String performStressTest(Context context, int iterations) {
        StringBuilder results = new StringBuilder();
        long startTime = System.currentTimeMillis();
        
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            
            for (int i = 0; i < iterations; i++) {
                // Open and close database repeatedly
                dbHelper.getWritableDatabase().close();
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            results.append("Stress Test: PASSED\n");
            results.append("Iterations: ").append(iterations).append("\n");
            results.append("Duration: ").append(duration).append("ms\n");
            results.append("Average time per operation: ").append(duration / iterations).append("ms\n");
        } catch (Exception e) {
            results.append("Stress Test: FAILED - ").append(e.getMessage()).append("\n");
            Log.e(TAG, "Stress test failed", e);
        }
        
        return results.toString();
    }
    
    /**
     * Check memory usage
     * @return Memory usage information
     */
    public static String checkMemoryUsage() {
        StringBuilder results = new StringBuilder();
        
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        long usedMemory = totalMemory - freeMemory;
        
        results.append("Memory Usage:\n");
        results.append("Max memory: ").append(maxMemory).append(" MB\n");
        results.append("Total memory: ").append(totalMemory).append(" MB\n");
        results.append("Used memory: ").append(usedMemory).append(" MB\n");
        results.append("Free memory: ").append(freeMemory).append(" MB\n");
        
        return results.toString();
    }
}
