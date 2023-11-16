package com.pc22.soundclassification;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OnboardingActivity extends AppCompatActivity {

    AppPreferences appPreferences;
    Button btn_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_onboarding);
        appPreferences = new AppPreferences(getApplicationContext());

        // Declare
        btn_start = findViewById(R.id.btn_start);

        // Start Button listener
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appPreferences.setIsFirstTimeLaunch(false);
                finish();
            }
        });
    }
}