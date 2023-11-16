package com.pc22.soundclassification.Room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface DayRecordDao {
    @Insert
    void insertRecord(DayRecord dayRecord);

    @Query("UPDATE dayrecord SET startSleepTime = :startSleepTime WHERE storeDate =:storeDate")
    void updateStartSleepDate(Date startSleepTime, String storeDate);

    @Query("UPDATE dayrecord SET endSleepTime = :endSleepTime WHERE storeDate =:storeDate")
    void updateEndSleepDate(Date endSleepTime, String storeDate);

    @Query("SELECT EXISTS(SELECT * FROM dayrecord WHERE storeDate = :date)")
    boolean exists(String date);

    @Query("SELECT * FROM dayrecord")
    List<DayRecord> getAll();

    @Query("DELETE FROM dayrecord")
    void deleteAll();

    @Query("SELECT * FROM dayrecord WHERE storeDate = :storeDate")
    DayRecord getRecordByStoreDate(String storeDate);

    @Query("UPDATE dayrecord SET dayBreath = :dayBreath, dayCough = :dayCough, dayMove = :dayMove, dayNoise = :dayNoise, daySnore = :daySnore WHERE storeDate =:storeDate")
    void updateEventByStoreDate(int dayBreath, int dayCough, int dayMove, int dayNoise, int daySnore, String storeDate);
}
