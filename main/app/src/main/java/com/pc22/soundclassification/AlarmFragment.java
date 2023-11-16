package com.pc22.soundclassification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.pc22.soundclassification.audio.AudioClassificationActivity;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlarmFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Context context;

    // XML
    NumberPicker numPickerHou;
    NumberPicker numPickerMin;
    NumberPicker numPickerAMPM;
    TextView remainingTime;
    Button startAlarm;

    // App Preferences
    AppPreferences appPreferences;

    // Alarm Time
    Calendar alarmCalendar;
    int hour;
    int minute;
    int AMPM; //0=AM, 1=PM

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AlarmFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlarmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlarmFragment newInstance(String param1, String param2) {
        AlarmFragment fragment = new AlarmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appPreferences = new AppPreferences(getContext());

        // XML Element
        remainingTime = view.findViewById(R.id.remainingTime);
        startAlarm = view.findViewById(R.id.startAlarm);

        startAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AudioClassificationActivity.class);
                intent.putExtra("long_alarmCalendar", alarmCalendar.getTimeInMillis());
                startActivity(intent);
            }
        });

        // ==================== setting numberPicker ====================
        numPickerHou = view.findViewById(R.id.numPickerHour);
        numPickerHou.setMaxValue(12);

        numPickerMin = view.findViewById(R.id.numPickerMinute);
        numPickerMin.setMaxValue(59);
        numPickerMin.setMinValue(0);

        numPickerAMPM = view.findViewById(R.id.numPickerAMPM);
        numPickerAMPM.setMinValue(0);
        numPickerAMPM.setMaxValue(1);
        numPickerAMPM.setDisplayedValues( new String[] { "AM", "PM" } );

        int[] DA_hourAndMinute = new int[2];
        DA_hourAndMinute = appPreferences.getDefaultAlarm();
        if(DA_hourAndMinute[0]>=12){
            // PM
            numPickerHou.setValue(DA_hourAndMinute[0]-12);
            numPickerAMPM.setValue(1);
        }else{
            // AM
            numPickerHou.setValue(DA_hourAndMinute[0]);
            numPickerAMPM.setValue(0);
        }
        numPickerMin.setValue(DA_hourAndMinute[1]);

        NumberPicker.OnValueChangeListener alarmChanged = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                updateValue();
            }
        };

        // Set Listener
        numPickerHou.setOnValueChangedListener(alarmChanged);
        numPickerMin.setOnValueChangedListener(alarmChanged);
        numPickerAMPM.setOnValueChangedListener(alarmChanged);

        // Call Listener once for init
        hour = numPickerHou.getValue();
        minute = numPickerMin.getValue();
        AMPM = numPickerAMPM.getValue();

        updateValue();
    }

    public void updateRemainingTime(){
        int txt_hours, tex_minutes;

        // Get Current time
        Calendar currentCalendar = Calendar.getInstance();
        // Get Setting time (Next day)
        int[] ED_hourAndMinute = appPreferences.getNextDay();
        Calendar endDayCalendar = Calendar.getInstance();
        endDayCalendar.set(Calendar.HOUR_OF_DAY, ED_hourAndMinute[0]);
        endDayCalendar.set(Calendar.MINUTE, ED_hourAndMinute[1]);

        // Set up alarm time
        alarmCalendar = Calendar.getInstance();
        if(currentCalendar.compareTo(endDayCalendar)<0){
            // Yes -> Alarm set to today
            alarmCalendar.set(Calendar.HOUR, hour);
            alarmCalendar.set(Calendar.MINUTE, minute);
            alarmCalendar.set(Calendar.AM_PM, (AMPM==0) ? Calendar.AM : Calendar.PM);
        }else{
            // No -> Alarm set to tomorrow
            alarmCalendar.add(Calendar.DATE, 1);
            alarmCalendar.set(Calendar.HOUR, hour);
            alarmCalendar.set(Calendar.MINUTE, minute);
            alarmCalendar.set(Calendar.AM_PM, (AMPM==0) ? Calendar.AM : Calendar.PM);
        }

        // Calculate remaining time
        long diffInMillis = alarmCalendar.getTimeInMillis() - currentCalendar.getTimeInMillis();
        long diffInMinutes = (diffInMillis / 60000)%60;
        long diffInHours = (diffInMillis / 3600000);

        // Update the TextView
        remainingTime.setText(diffInHours+"hour, "+diffInMinutes+"minute");
    }

    public void updateValue(){
        // Get Value
        hour = numPickerHou.getValue();
        minute = numPickerMin.getValue();
        AMPM = numPickerAMPM.getValue();

        updateRemainingTime();
    }

    public void setAlarm(){

//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(this, AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 0);
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 8);
//        calendar.set(Calendar.MINUTE, 30);
//
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}