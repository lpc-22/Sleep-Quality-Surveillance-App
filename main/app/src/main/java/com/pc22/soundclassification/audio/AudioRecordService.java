package com.pc22.soundclassification.audio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.AlarmClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.pc22.soundclassification.AppPreferences;
import com.pc22.soundclassification.MainActivity;
import com.pc22.soundclassification.R;
import com.pc22.soundclassification.Room.AppDatabase;
import com.pc22.soundclassification.Room.ClassificationRecord;
import com.pc22.soundclassification.Room.DayRecord;

import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AudioRecordService extends Service {
    private static final String TAG = "AudioRecordService";
    private static final int NOTIFICATION_ID = 1;
    int[] dayEvent;

    @Override
    public void onCreate() {
        super.onCreate();
        // Get App setting
        appPreferences = new AppPreferences(getApplicationContext());

        // ==================== ROOM DB: DayRecord ====================
        dayRecord = new DayRecord();
        dayEvent = new int[]{0, 0, 0, 0, 0};

        initDate();
        storeStartSleepTime();
        // init variable
        eventHappenList = new ArrayList<>();
    }

    // TODO:: Testing
    private void playAudio() {

    }


    public void initDate(){
        //  ==================== Get date ====================
        // get current time
        Calendar calendarToday = Calendar.getInstance();

        // Save the record for the current minute to ROOM_DB
        currentDateAndTime = calendarToday.getTime();

        // get next day
        Calendar calendarTomorrow = Calendar.getInstance();
        calendarTomorrow.add(Calendar.DATE, 1);
        nextDateAndTime = calendarTomorrow.getTime();

        // get today end time
        int[] nextDay = appPreferences.getNextDay();
        calendarToday.set(Calendar.HOUR_OF_DAY, nextDay[0]);
        calendarToday.set(Calendar.MINUTE, nextDay[1]);
        calendarToday.set(Calendar.SECOND, 0);
        todayEndTime = calendarToday.getTime();

        // Set the string for storing Date in DB
        SimpleDateFormat storeDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(currentDateAndTime.compareTo(todayEndTime)>=0){
            // Tomorrow
            // store record with Current date + 1
            storeDate = storeDateFormat.format(nextDateAndTime);
        }else{
            // Today
            // store record with today's date
            storeDate = storeDateFormat.format(currentDateAndTime);
            dayRecord.setStartSleepTime(currentDateAndTime);
        }
    }

    AppDatabase db;
    public void storeStartSleepTime(){
        // Add startTime to Day Record
        db = AppDatabase.getInstance(getApplicationContext());
//        List<ClassificationRecord> CR_list_all = db.classificationRecordDao().getAll();

        if(db.dayRecordDao().exists(storeDate)){
            // ===== if Exists =====
//            db.dayRecordDao().updateStoreDate(new Date(), storeDate);
        }else{
            // ===== Not Exists =====
            dayRecord = new DayRecord(storeDate, new Date());
            insertAsyncTask_dr = new InsertAsyncTask_DR();
            insertAsyncTask_dr.execute(dayRecord);
        }
    }

    public void storeEndSleepTime(){
        db = AppDatabase.getInstance(getApplicationContext());
        if(db.dayRecordDao().exists(storeDate)){
            // ===== if exists =====
            db.dayRecordDao().updateEndSleepDate(new Date(), storeDate);

            // Update dayEvent on dayRecord
            db.dayRecordDao().updateEventByStoreDate(dayEvent[0], dayEvent[1], dayEvent[2], dayEvent[3], dayEvent[4], storeDate);
        }else{
            // ===== not exist =====
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CalculateAwake calculateAwake = new CalculateAwake();
        calculateAwake.execute();
        stopStreaming();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // Set Notification
        Notification notification = new NotificationCompat.Builder(this, "ForegroundServiceChannel")
                .setContentTitle("Foreground Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        startStreaming();
        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "ForegroundServiceChannel",
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    // ==================== Setting ====================
    AppPreferences appPreferences;

    // ==================== Model ====================
    String modelPath = "lite-model_yamnet_classification_tflite_1.tflite";
    AudioClassifier classifier;
    AudioRecord audioRecord;
    TensorAudio tensor;

    // ==================== Time ====================
    SimpleDateFormat storeDateFormat = new SimpleDateFormat("yyyyMMdd");
    Date currentTime;
    Date currentDateAndTime;
    Date todayEndTime;
    Date nextDateAndTime;
    String storeDate;

    public void startStreaming(){

        // Get current time and set the beginning time for the first minute function
        Calendar calendar = Calendar.getInstance(); // Get time instance
        calendar.set(Calendar.MILLISECOND, 0);      // Set second to 0
        calendar.add(Calendar.MINUTE, 1);        // Add minutes by 1
        currentTime = calendar.getTime();           // get time

        // Get Model by path
        try {
            classifier = AudioClassifier.createFromFile(this, modelPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Creating the tensor variable, which will store the recording and build the format spec. for recorder
        tensor = classifier.createInputTensorAudio();

        // TimerTask
        startSecondTask();
        startMinuteTask();
    }

    public void stopStreaming(){
        secondTask.cancel();
        minuteTask.cancel();
        audioRecord.stop();
        audioRecord.release();
    }

    // ==================== TimerTask ====================
    Thread secondThread;
    TimerTask secondTask;
    List<Boolean[]> eventHappenList;    // store what event have happen in that second

    public void startSecondTask(){
        secondThread = new Thread(()->{
            try{
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
                audioRecord = classifier.createAudioRecord();    // Creates an AudioRecord instance to record audio stream
                audioRecord.startRecording();

                secondTask = new TimerTask() {
                    @Override
                    public void run() {
                        // Classifying audio data
                        tensor.load(audioRecord);
                        List<Classifications> allOutput = classifier.classify(tensor);

                        // Filtering out classifications with low probability
                        List<Category> filteredOutput = new ArrayList<>();

                        // Init the secondRecord : [0]Breath, [1]Snore, [2]Cough, [3]Move, [4]Noise, --[5]isAwake--
                        Boolean[] eventHappen = {false, false, false, false, false};
                        for (Classifications classifications : allOutput) {
                            for (Category category : classifications.getCategories()) {
                                // Set up a minimum threshold to accept the prediction
                                if (category.getScore() > 0.6f) {
                                    switch(category.getIndex()){
                                        case 36:    // Breathing
                                        case 37:    // Wheeze
                                        case 39:    // Gasp
                                        case 40:    // Pant
                                            // [0]Breath
                                            eventHappen[0] = true;
                                            break;
                                        case 38:    // Snoring
                                        case 41:    // Snore
                                            // [1]Snore
                                            eventHappen[1] = true;
                                            break;
                                        case 42:    // Cough
                                        case 44:    // Sneeze
                                            // [2]Cough
                                            eventHappen[2] = true;
                                            break;
                                        case 46:    // Run
                                        case 47:    // Shuffle
                                        case 48:    // Walk, footsteps
                                        case 470:   // Rub
                                            // [3] Move
                                            eventHappen[3] = true;
                                            break;
                                        default:    // Noise
                                            // [4] Noise
                                            eventHappen[4] = true;
                                            break;
                                        case 494:   // Silence
                                            break;
                                    }
                                    filteredOutput.add(category);
                                }
                            }
                        }
                        eventHappenList.add(eventHappen);

                        // Sorting the results
                        Collections.sort(filteredOutput, (o1, o2) -> (int) (o1.getScore() - o2.getScore()));


                        // TODO:: Reomve
                        StringBuilder outputStr = new StringBuilder();
                        for (Category category : filteredOutput) {
                            outputStr.append(category.getLabel()).append(": ").append(category.getScore()).append(", ");
                        }
                        // TODO:: Reomve
                        long millis = System.currentTimeMillis();   //get current time
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Log.d(TAG, sdf.format(new Date())+outputStr);
                    }
                };
                new Timer().scheduleAtFixedRate(secondTask, 1000, 1000);
            }catch (Exception e){
                Log.e(TAG, "Exception: " + e);
            }
        });
        secondThread.start();
    }

    Thread minuteThread;
    TimerTask minuteTask;

    public void startMinuteTask(){
        minuteThread = new Thread(()->{
            try{
                minuteTask = new TimerTask() {
                    @Override
                    public void run() {
                        // Retrieval the data
                        List<Boolean[]> cp_eventHappenList = new ArrayList<>(eventHappenList);
                        cp_eventHappenList.addAll(eventHappenList);

                        // Clear the data
                        eventHappenList.clear();

                        // Summarize the minutes
                        int[] minuteResult = {0, 0, 0, 0, 0};    // Breath, Snore, Cough, Move, Noise
                        for (Boolean[] row_eventHappen : cp_eventHappenList) {
                            for (int i = 0; i < row_eventHappen.length; i++) {
                                if(row_eventHappen[i]) minuteResult[i]++;
                            }
                        }

                        // Save the record for the current minute to ROOM_DB
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        ClassificationRecord r = new ClassificationRecord(calendar.getTime(), storeDate, minuteResult);
                        InsertAsyncTask_CR insertAsyncTask_cr = new InsertAsyncTask_CR();
                        insertAsyncTask_cr.execute(r);
                    }
                };
                new Timer().scheduleAtFixedRate(minuteTask, currentTime, 60000);
            }catch (Exception e){
                Log.e(TAG, "Exception: "+e);
            }
        });
        minuteThread.start();
    }

    // ==================== ROOM DB ====================
    // Add record to Database (DayRecord)
    DayRecord dayRecord;
    InsertAsyncTask_DR insertAsyncTask_dr;
    class InsertAsyncTask_DR extends AsyncTask<DayRecord, Void, Void> {
        @Override
        protected Void doInBackground(DayRecord... dayRecords) {
            AppDatabase.getInstance(getApplicationContext()).dayRecordDao().insertRecord(dayRecords[0]);
            return null;
        }
    }

    class InsertAsyncTask_CR extends AsyncTask<ClassificationRecord, Void, Void>{
        @Override
        protected Void doInBackground(ClassificationRecord... classificationRecords) {
            AppDatabase.getInstance(getApplicationContext()).classificationRecordDao().insertRecord(classificationRecords[0]);
            return null;
        }
    }

    public class CalculateAwake extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            // init
            db = AppDatabase.getInstance(getApplicationContext());
            // Get all record by date
            List<ClassificationRecord> classificationRecords = db.classificationRecordDao().getAllByDate(storeDate);

            for(ClassificationRecord classificationRecord : classificationRecords){
                // Get Time
                Date currentDate_cal = classificationRecord.date;

                // Sum other event
                dayEvent[0] += classificationRecord.breath;
                dayEvent[1] += classificationRecord.cough;
                dayEvent[2] += classificationRecord.move;
                dayEvent[3] += classificationRecord.noise;
                dayEvent[4] += classificationRecord.snore;

                // Awake
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate_cal);
                calendar.add(Calendar.MINUTE, -4);
                double[] weight = {0.15, 0.15, 0.15, 0.08, 0.21, 0.12, 0.13};
                double[] awakeScores = new double[7];
                for(int i=0;i<=6;i++){
                    ClassificationRecord classificationRecord_cal = db.classificationRecordDao().getRecordByStoreDateAndMinute(storeDate, calendar.getTime());
                    if(classificationRecord_cal!=null){
                        // Record found
                        int m = classificationRecord_cal.move;
                        awakeScores[i] = m * weight[i];
                    }else{
                        // Record not found
                        awakeScores[i] = 60 * weight[i];
                    }
                    calendar.add(Calendar.MINUTE, 1);
                }
                // Calculate score
                double sum = 0;
                for(double a : awakeScores){
                    sum += a;
                }
                sum *= 0.125;
                double roundedNumber = Math.round(sum * 100.0) / 100.0;

                classificationRecord.awake = roundedNumber;
                db.classificationRecordDao().updateAwake(roundedNumber, storeDate, currentDate_cal);
            }
            storeEndSleepTime();
            return null;
        }

        // get storedate
        // get all record by date
        //
    }
}
