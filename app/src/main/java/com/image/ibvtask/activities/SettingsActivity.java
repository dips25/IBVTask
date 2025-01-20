package com.image.ibvtask.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.image.ibvtask.R;

public class SettingsActivity extends AppCompatActivity {

    Switch nightSwitch;
    SharedPreferences s;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        nightSwitch = (Switch) findViewById(R.id.night_switch);
        getSupportActionBar().setTitle(getString(R.string.settings));
        s = getSharedPreferences("other_credentials", Context.MODE_PRIVATE);

       //boolean isNightMode = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES;


        if (s.getBoolean("isDark",false)) {

            nightSwitch.setChecked(true);

        } else {

            nightSwitch.setChecked(false);
        }

        nightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    s.edit().putBoolean("isDark",true).commit();

                } else {

                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    s.edit().putBoolean("isDark",false).commit();
                }

            }
        });
    }
}
