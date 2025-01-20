package com.image.ibvtask;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class IBVTask extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences s = getSharedPreferences("other_credentials", Context.MODE_PRIVATE);
        if (s.getBoolean("isDark",false)) {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }
}
