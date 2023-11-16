package com.pc22.soundclassification.Room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.util.Date;
import java.util.List;

@Dao
public interface ClassificationRecordDao {
    @Insert
    void insertRecord(ClassificationRecord classificationRecord);

    @Query("SELECT * FROM classificationrecord")
    List<ClassificationRecord> getAll();

    @Query("SELECT * FROM classificationrecord WHERE CR_storeDate = :storeDate")
    List<ClassificationRecord> getAllByDate(String storeDate);

    @Query("DELETE FROM classificationrecord")
    void deleteAll();

    @Query("SELECT * FROM classificationrecord WHERE CR_storeDate = :storeDate AND CR_date = :date")
    ClassificationRecord getRecordByStoreDateAndMinute(String storeDate, Date date);

    @Query("UPDATE classificationrecord SET CR_awake = :awake WHERE CR_storeDate = :storeDate AND CR_date = :date")
    void updateAwake(double awake, String storeDate, Date date);

    @Query("SELECT COUNT(*) FROM classificationrecord WHERE CR_storeDate = :storeDate")
    int getMinuteCountByStoreDate(String storeDate);

    @Query("SELECT COUNT(*) FROM classificationrecord WHERE CR_storeDate = :storeDate AND CR_awake <= :threshold")
    int getAwakeCountByStoreDate(String storeDate, double threshold);
}
