package com.pc22.soundclassification;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.pc22.soundclassification.Room.AppDatabase;

import java.util.Calendar;

public class SettingFragment extends Fragment {

    AppPreferences appPreferences;
    AppDatabase db;

    // XML element
    Switch darkMode_switch;
    TextView clickText_defaultAlarm;
    TextView clickText_nextDay;
    TextView clickText_deleteAllData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appPreferences = new AppPreferences(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // XML Element
        darkMode_switch = view.findViewById(R.id.darkMode_switch);
        clickText_defaultAlarm = view.findViewById(R.id.clickText_defaultAlarm);
        clickText_nextDay = view.findViewById(R.id.clickText_nextDay);
        clickText_deleteAllData = view.findViewById(R.id.deleteAllData);

        // ==================== Dark mode switch ====================
        darkMode_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appPreferences.changeThemeMode();
                updateDarkModeButton();
            }
        });
        updateDarkModeButton();

        // ==================== Default Alarm ====================
        int[] DA_hourAndMinute = appPreferences.getDefaultAlarm();
        Calendar DA_calendar = Calendar.getInstance();
        DA_calendar.set(0, 0, 0, DA_hourAndMinute[0], DA_hourAndMinute[1]);
        String DA_string = DateFormat.format("HH:mm", DA_calendar).toString();

        clickText_defaultAlarm.setText(DA_string);
        clickText_defaultAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Init
                TimePickerDialog DA_timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Update shared preference
                        appPreferences.setDefaultAlarm(new int[]{hourOfDay , minute});
                        // Update current view
                        DA_calendar.set(0, 0, 0, hourOfDay, minute);
                        clickText_defaultAlarm.setText(DateFormat.format("HH:mm", DA_calendar));
                    }
                }, 24, 0, true
                );
                // Displayed previous selected time
                DA_timePickerDialog.updateTime(DA_hourAndMinute[0], DA_hourAndMinute[1]);
                // Show dialog
                DA_timePickerDialog.show();
            }
        });

        // ==================== Next Day ====================
        int[] ND_hourAndMinute = appPreferences.getNextDay();
        Calendar ND_calendar = Calendar.getInstance();
        ND_calendar.set(0, 0, 0, ND_hourAndMinute[0], ND_hourAndMinute[1]);
        String ND_string = DateFormat.format("HH:mm", ND_calendar).toString();

        clickText_nextDay.setText(ND_string);
        clickText_nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Init
                TimePickerDialog ND_timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Update shared preference
                        appPreferences.setNextDay(new int[]{hourOfDay , minute});
                        // Update current view
                        ND_calendar.set(0, 0, 0, hourOfDay, minute);
                        clickText_nextDay.setText(DateFormat.format("HH:mm", ND_calendar));
                    }
                }, 24, 0, true
                );
                // Displayed previous selected time
                ND_timePickerDialog.updateTime(ND_hourAndMinute[0], ND_hourAndMinute[1]);
                // Show dialog
                ND_timePickerDialog.show();
            }
        });
        // ========== Delete All ==========
        clickText_deleteAllData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext()).setTitle("Delete All Data").setMessage("You are going to delete all data.").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AppDatabase db = AppDatabase.getInstance(getContext());
                        db.dayRecordDao().deleteAll();
                        db.classificationRecordDao().deleteAll();
                    }
                }).setNegativeButton(android.R.string.no, null).show();
            }
        });

    }
    public void updateDarkModeButton(){
        if(appPreferences.getIsDarkMode()){
            // dark mode
            darkMode_switch.setChecked(true);
        }else{
            // day mode
            darkMode_switch.setChecked(false);
        }
    }
}