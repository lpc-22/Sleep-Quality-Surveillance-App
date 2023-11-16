package com.pc22.soundclassification;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pc22.soundclassification.Room.AppDatabase;
import com.pc22.soundclassification.Room.ClassificationRecord;
import com.pc22.soundclassification.Room.DayRecord;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClassificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClassificationFragment extends Fragment {

    Context context;
    public CrListAdapter crListAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    SimpleDateFormat storeDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Calendar checkingDate_calendar;
    Date checkingDate_date;

    public ClassificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClassificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClassificationFragment newInstance(String param1, String param2) {
        ClassificationFragment fragment = new ClassificationFragment();
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

        context = getContext();


        checkingDate_calendar = Calendar.getInstance();
        checkingDate_date = checkingDate_calendar.getTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_classification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView checkingDate = view.findViewById(R.id.checkingDate);
        Button btn_left = view.findViewById(R.id.btn_left);
        Button btn_right = view.findViewById(R.id.btn_right);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        checkingDate.setText(sdf.format(new Date()));

        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkingDate_calendar.add(Calendar.DATE, -1);
                checkingDate_date = checkingDate_calendar.getTime();
                checkingDate.setText(storeDateFormat.format(checkingDate_date));
                loadClassificationRecord();
            }
        });

        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkingDate_calendar.add(Calendar.DATE, 1);
                checkingDate_date = checkingDate_calendar.getTime();
                checkingDate.setText(storeDateFormat.format(checkingDate_date));
                loadClassificationRecord();
            }
        });

        initRecycleView();
        loadClassificationRecord();
    }

    public void loadClassificationRecord() {
        AppDatabase db = AppDatabase.getInstance(getContext());

        List<ClassificationRecord> CR_list_all = db.classificationRecordDao().getAll();
        for(ClassificationRecord element : CR_list_all){
            Log.d("record", "["+element.storeDate+"]: "+element.date+", awake=["+element.awake+"], breath=["+element.breath+"], move=["+element.move+"], snore=["+element.snore+"], cough=["+element.cough+"], noise=["+element.noise+"]");
        }

        // TODO: remove
        List<DayRecord> dayRecord = db.dayRecordDao().getAll();
        for(DayRecord a : dayRecord){
            Log.d("Testing", "start:"+a.startSleepTime+", end: "+a.endSleepTime);
        }

        List<ClassificationRecord> CR_list = db.classificationRecordDao().getAllByDate(storeDateFormat.format(checkingDate_date));
        crListAdapter.setClassificationRecords(CR_list);
    }

    public void initRecycleView(){
        RecyclerView recyclerView = getView().findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        crListAdapter = new CrListAdapter(context);
        recyclerView.setAdapter(crListAdapter);
    }
}