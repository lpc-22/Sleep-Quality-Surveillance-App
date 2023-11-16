package com.pc22.soundclassification;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.pc22.soundclassification.Room.AppDatabase;
import com.pc22.soundclassification.Room.ClassificationRecord;
import com.pc22.soundclassification.Room.DayRecord;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // Global
    SimpleDateFormat storeDate_format;

    // XML element
    BarChart SE_chart;

    TextView clickText_Previous;
    TextView currentDate;
    TextView clickText_Next;
    TextView startAndEndTime;
    TextView sleepEfficiency_duration;
    TextView SE_percentage;
    TextView numberBreath;
    TextView numberCough;
    TextView numberMove;
    TextView numberNoise;
    TextView numberSnore;

    // ROOM Database
    AppDatabase db;
    // Date
    Date date_currentView;

    // SE_chart
    List<BarEntry> SE_barEntryList_Sleep;
    List<BarEntry> SE_barEntryList_Awake;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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

        db = AppDatabase.getInstance(getContext());
        // Date
        date_currentView = new Date();
        storeDate_format = new SimpleDateFormat("yyyy-MM-dd");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // SE_chart
        SE_chart = view.findViewById(R.id.SE_chart);
        updateSleepStateGraph();

        // XML
        startAndEndTime = view.findViewById(R.id.startAndEndTime);
        clickText_Previous = view.findViewById(R.id.clickText_Previous);
        currentDate = view.findViewById(R.id.currentDate);
        clickText_Next = view.findViewById(R.id.clickText_Next);
        sleepEfficiency_duration = view.findViewById(R.id.sleepEfficiency_duration);
        SE_percentage = view.findViewById(R.id.SE_percentage);
        numberBreath = view.findViewById(R.id.numberBreath);
        numberCough = view.findViewById(R.id.numberCough);
        numberMove = view.findViewById(R.id.numberMove);
        numberNoise = view.findViewById(R.id.numberNoise);
        numberSnore = view.findViewById(R.id.numberSnore);

        // Update View
        updateCurrentDate();
        updateSleepEfficiency();
        updateSleepStateGraph();
        updateEventNumber();

        // on Click Listener
        clickText_Previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date_currentView);
                calendar.add(Calendar.DATE, -1);
                date_currentView = calendar.getTime();

                // Update view
                updateCurrentDate();
                updateSleepEfficiency();
                updateSleepStateGraph();
                updateEventNumber();
            }
        });
        clickText_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date_currentView);
                calendar.add(Calendar.DATE, 1);
                date_currentView = calendar.getTime();

                // Update view
                updateCurrentDate();
                updateSleepEfficiency();
                updateSleepStateGraph();
                updateEventNumber();
            }
        });
    }

    private void updateCurrentDate(){
        currentDate.setText(storeDate_format.format(date_currentView));
    }

    private void setValues() {
        // Get All Data
        List<ClassificationRecord> classificationRecords = db.classificationRecordDao().getAllByDate(storeDate_format.format(date_currentView));
        // loop init
        SimpleDateFormat sdf_SS= new SimpleDateFormat("HHmm");
        // loop all record
        if(classificationRecords!=null){
            for(ClassificationRecord classificationRecord : classificationRecords){
                if(classificationRecord != null){
                    // Time
                    float time_SS = new Float(sdf_SS.format(classificationRecord.date));
                    // Awake
                    float awake = (float)classificationRecord.awake;
                    if(awake<=1){
                        // sleep state (cyan)
                        SE_barEntryList_Sleep.add(new BarEntry(time_SS, awake));
                    }else{
                        // awake state (red)
                        SE_barEntryList_Awake.add(new BarEntry(time_SS, awake));
                    }
                    // Time
                    sdf_SS.format(classificationRecord.date);
                }
            }
        }else{

        }

    }

    private void setUpChart(){
        BarDataSet barDataSet_Sleep = new BarDataSet(SE_barEntryList_Sleep, "Sleep");
        BarDataSet barDataSet_Awake = new BarDataSet(SE_barEntryList_Awake, "Awake");
//        barDataSet_Sleep.setDrawValues(false);
        barDataSet_Awake.setDrawValues(false);
        barDataSet_Sleep.setValueTextColor(Color.CYAN);
        barDataSet_Awake.setValueTextColor(Color.RED);
        barDataSet_Sleep.setColor(Color.CYAN);
        barDataSet_Awake.setColor(Color.RED);

        //initialize x Axis Labels (labels for 25 vertical grid lines)
        final ArrayList<String> xAxisLabel = new ArrayList<>();


//        barDataSet_Sleep.setValueTextSize(12f);
        BarData barData = new BarData(barDataSet_Sleep, barDataSet_Awake);
        SE_chart.getXAxis().setTextColor(Color.WHITE);
        SE_chart.getAxisLeft().setTextColor(Color.WHITE);
        SE_chart.getAxisRight().setTextColor(Color.WHITE);
        SE_chart.setData(barData);
        SE_chart.animateY(500);
        SE_chart.invalidate();
    }

    private void updateSleepEfficiency(){
        DayRecord currentDayRecord =  db.dayRecordDao().getRecordByStoreDate(storeDate_format.format(date_currentView));

        if(currentDayRecord != null){
            // Start time and End time
            Date startSleepTime = currentDayRecord.startSleepTime;
            Date endSleepTime = currentDayRecord.endSleepTime;
            SimpleDateFormat sleepEfficiency_format= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String str_startSleepTime = sleepEfficiency_format.format(startSleepTime);
            String str_endSleepTime = sleepEfficiency_format.format(endSleepTime);
            startAndEndTime.setText(str_startSleepTime+"\n"+str_endSleepTime);
            Log.d("History", "["+ currentDayRecord.storeDate+"]: "+str_startSleepTime+" - "+str_endSleepTime+currentDayRecord.dayBreath+currentDayRecord.dayCough+currentDayRecord.dayMove+currentDayRecord.dayNoise+currentDayRecord.daySnore);

            // Duration (Number of minute)
            int onBedMinutes = db.classificationRecordDao().getMinuteCountByStoreDate(currentDayRecord.storeDate);
            int SE_hour = onBedMinutes/60;
            int SE_minute = onBedMinutes%60;
            String SE_duration = SE_hour+" hours, "+SE_minute+" minutes";
            sleepEfficiency_duration.setText(SE_duration);

            // Efficiency (e.g. 30%)
            int a = db.classificationRecordDao().getAwakeCountByStoreDate(currentDayRecord.storeDate, 1.0);
            double b = (double)a/(double)onBedMinutes;
            String formattedValue = String.format("%.0f%%", b*100);
            SE_percentage.setText(formattedValue);
        }else{
            startAndEndTime.setText("-");
            sleepEfficiency_duration.setText("-");
            SE_percentage.setText("%");
        }
    }
    public void updateSleepStateGraph(){
        SE_barEntryList_Sleep = new ArrayList<>();
        SE_barEntryList_Awake = new ArrayList<>();
        setValues();
        setUpChart();
    }

    public void updateEventNumber(){
        DayRecord dayRecord = db.dayRecordDao().getRecordByStoreDate(storeDate_format.format(date_currentView));
        if(dayRecord!=null){
            numberBreath.setText(""+dayRecord.dayBreath);
            numberCough.setText(""+dayRecord.dayCough);
            numberMove.setText(""+dayRecord.dayMove);
            numberNoise.setText(""+dayRecord.dayNoise);
            numberSnore.setText(""+dayRecord.daySnore);
        }else{
            numberBreath.setText("-");
            numberCough.setText("-");
            numberMove.setText("-");
            numberNoise.setText("-");
            numberSnore.setText("-");
        }

    }
}