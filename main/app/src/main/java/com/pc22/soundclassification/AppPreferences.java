package com.pc22.soundclassification;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import java.time.LocalTime;

public class AppPreferences {

    // ==================== Shared Preferences ====================
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String PREF_NAME = "app_preferences";


    private Context context;

    // ==================== On Boarding Screen ====================
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    // ==================== Dark Mode ====================
    private static final String IS_DARK_MODE = "IsDarkMode";
    // ==================== Alarm Setting ====================
    private static final String DEFAULT_ALARM_HOUR = "DefaultAlarmHour";
    private static final String DEFAULT_ALARM_MINUTE = "DefaultAlarmMinute";

    private static final String NEXT_DAY_HOUR = "NextDayHour";
    private static final String NEXT_DAY_MINUTE = "NextDayMinute";


    public AppPreferences(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public boolean getIsFirstTimeLaunch() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setIsFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }


    // ==================== Dark Mode ====================
    public boolean getIsDarkMode() {
        return sharedPreferences.getBoolean(IS_DARK_MODE, true);
    }

    public void setDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        editor.putBoolean(IS_DARK_MODE, true);
        editor.commit();
    }

    public void setDayMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        editor.putBoolean(IS_DARK_MODE, false);
        editor.commit();
    }

    public void changeThemeMode(){
        if(getIsDarkMode()){
            // Is dark mode
            setDayMode();
        }else{
            // Is light mode
            setDarkMode();
        }
    }
    // ==================== Alarm Setting ====================
    // Default Alarm
    public int[] getDefaultAlarm(){
        int[] hourAndTime = new int[2];
        hourAndTime[0] = sharedPreferences.getInt(DEFAULT_ALARM_HOUR, 8);
        hourAndTime[1] = sharedPreferences.getInt(DEFAULT_ALARM_MINUTE, 30);
        return hourAndTime;
    }
    public void setDefaultAlarm(int[] hourAndTime){
        editor.putInt(DEFAULT_ALARM_HOUR, hourAndTime[0]);
        editor.putInt(DEFAULT_ALARM_MINUTE, hourAndTime[1]);
        editor.commit();
    }
    // Next Day
    public int[] getNextDay(){
        int[] hourAndTime = new int[2];
        hourAndTime[0] = sharedPreferences.getInt(NEXT_DAY_HOUR, 13);
        hourAndTime[1] = sharedPreferences.getInt(NEXT_DAY_MINUTE, 00);
        return hourAndTime;
    }
    public void setNextDay(int[] hourAndTime){
        editor.putInt(NEXT_DAY_HOUR, hourAndTime[0]);
        editor.putInt(NEXT_DAY_MINUTE, hourAndTime[1]);
        editor.commit();
    }

}
