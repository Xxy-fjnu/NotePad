package com.example.android.notepad;

import android.support.v7.app.AppCompatDelegate;

public class NightUtils {

    /**
     * App杀死，没有值，false
     */
    public static boolean isNight = false;

    public static void setNightMode() {
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

}
