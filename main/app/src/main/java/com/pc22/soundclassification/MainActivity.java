package com.pc22.soundclassification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.pc22.soundclassification.audio.AudioClassificationActivity;
import com.pc22.soundclassification.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    AppPreferences appPreferences;

    int mNavItemID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get Audio Permission
        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 2033);
        }

        // ==================== OnBoarding Screen ====================
        // load SharedPreferences
        appPreferences = new AppPreferences(getApplicationContext());
        // Determine whether it is the first time launching this app
        if(appPreferences.getIsFirstTimeLaunch()){
            // If true

            // Set dark mode (default)
            appPreferences.setDarkMode();

            // load onBoarding screen
            startActivity(new Intent(MainActivity.this, OnboardingActivity.class));
        }

        // ==================== Check theme ====================
        if(appPreferences.getIsDarkMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // ==================== Navbar and Fragment ====================
        // Restore fragment from saved state
        if(savedInstanceState != null){
            mNavItemID = savedInstanceState.getInt("selected_fragment", 0);
        }else{
            mNavItemID = R.id.alarm;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        switchFragment(mNavItemID);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
        switchFragment(item.getItemId());
        return true;
        });
    }

    public void switchFragment(int mNavItemID){
        Fragment fragment = null;
        switch (mNavItemID){
            case R.id.alarm:
                fragment = new AlarmFragment();
                break;
            case R.id.classification_result:
                fragment = new ClassificationFragment();
                break;
            case R.id.history:
                fragment = new HistoryFragment();
                break;
            case R.id.setting:
                fragment = new SettingFragment();
                break;
        }
        if(fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putInt("selected_fragemnt", mNavItemID);
    }
}