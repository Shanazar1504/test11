package com.example.doktor;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefConfig {
    private static final String MY_PREFERENCE_NAME = "com.example.doktor";
    private static final String PREF_STATUS_KEY = "pref_status_key";
    private static final String PREF_TIME_KEY = "pref_time_key";
    private static final String PREF_PhoneNumber_KEY = "pref_number_key";




    // NOTIFICATION
    public static void saveNotify(Context context, boolean status) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_STATUS_KEY, status);
        editor.apply();
    }
    public static boolean loadNotify(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(PREF_STATUS_KEY, false);
    }

    // time
    public static void saveTime(Context context, boolean status) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_TIME_KEY, status);
        editor.apply();
    }
    public static boolean loadTime(Context context) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(PREF_TIME_KEY, false);
    }


    // for ENGINE IP address
    public static void saveNumber(Context context, String ipEngine) {
        SharedPreferences pref = context.getSharedPreferences(MY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_PhoneNumber_KEY, ipEngine);
        editor.apply();
    }
}