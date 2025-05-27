package com.aaryan7.dastakmobile7.utils;

import java.util.Calendar;

/**
 * Utility class for time-based greeting
 */
public class GreetingUtils {
    
    // Time periods for greetings
    public static final int MORNING = 0;
    public static final int AFTERNOON = 1;
    public static final int EVENING = 2;
    
    /**
     * Get current time period
     * @return Time period (MORNING, AFTERNOON, or EVENING)
     */
    public static int getCurrentTimePeriod() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        
        if (hourOfDay >= 4 && hourOfDay < 12) {
            return MORNING;
        } else if (hourOfDay >= 12 && hourOfDay < 17) {
            return AFTERNOON;
        } else {
            return EVENING;
        }
    }
    
    /**
     * Get greeting message based on time period
     * @return Greeting message
     */
    public static String getGreetingMessage() {
        int timePeriod = getCurrentTimePeriod();
        
        switch (timePeriod) {
            case MORNING:
                return "Good Morning";
            case AFTERNOON:
                return "Good Afternoon";
            case EVENING:
                return "Good Evening";
            default:
                return "Welcome";
        }
    }
}
