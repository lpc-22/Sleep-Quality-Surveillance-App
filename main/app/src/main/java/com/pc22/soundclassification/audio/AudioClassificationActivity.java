package com.pc22.soundclassification.audio;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.pc22.soundclassification.AppPreferences;
import com.pc22.soundclassification.R;
import com.pc22.soundclassification.Room.AppDatabase;
import com.pc22.soundclassification.Room.ClassificationRecord;
import com.pc22.soundclassification.Room.DayRecord;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;
import java.util.Date;

public class AudioClassificationActivity extends AppCompatActivity {
    // ==================== XML Element ====================
    Button btn_awake;

    // ==================== Global Variable ====================
    View view;

    // ==================== Shared Preference ====================
    AppPreferences appPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_helper);
        view = findViewById(android.R.id.content);
        appPreferences = new AppPreferences(getApplicationContext());

        // Get Audio Permission
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 2033);
        }

        // start update remaining timer & start alarm
        updateRemainingTime();

        // Declare Button
        btn_awake = findViewById(R.id.btnAwake);

        // Start record and classification
        startRecordingService();
    }

    public void startRecordingService() {
        // start AudioRecordService.java
        Context context = getApplicationContext();
        Intent intent = new Intent(context, AudioRecordService.class); // Build the intent for the service
        context.startForegroundService(intent);

    }


    public void endRecording(View view) {
        // Stop AudioRecord Service
        Intent intent = new Intent(getApplicationContext(), AudioRecordService.class);
        getApplicationContext().stopService(intent);

        // Stop or cancel alarm
        cancelAlarm();

        // Analysis the result
        calSleepEfficiency();

        // Return to Alarm
        finish();
    }

    public void startAlarm(){

    }

    public void cancelAlarm(){

    }

    Calendar alarm_calendar;

    public void updateRemainingTime(){
        // XML
        TextView countDownTime;
        countDownTime = view.findViewById(R.id.countDownTime);

        // Get Alarm time
        alarm_calendar = Calendar.getInstance();
        alarm_calendar.setTimeInMillis(getIntent().getLongExtra("long_alarmCalendar", 0L));

        // Start Alarm
        startAlarm();

        // Update Remaining time
        TimerTask secondTask = new TimerTask(){
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();

                // Calculate time difference between two Calendar objects
                long diffInMillis = alarm_calendar.getTimeInMillis() - calendar.getTimeInMillis();
                int diffInSeconds = (int) (diffInMillis / 1000);

                // Format time difference in hh:mm:ss format
                int hours = diffInSeconds / 3600;
                int minutes = (diffInSeconds % 3600) / 60;
                int seconds = diffInSeconds % 60;
                String formattedTimeDifference = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                countDownTime.setText(formattedTimeDifference);
            }
        };
        new Timer().scheduleAtFixedRate(secondTask, 0, 1000);
    }

    public void calSleepEfficiency(){


        // Duration


        // Efficiency
    }
    public void processData(){
        // set awake

    }
}