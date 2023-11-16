package com.pc22.soundclassification.Room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

@Entity
public class DayRecord {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "storeDate")
    public String storeDate;

    @ColumnInfo(name = "startSleepTime")
    public Date startSleepTime;

    @ColumnInfo(name = "endSleepTime")
    public Date endSleepTime;

    @ColumnInfo(name = "sleepEfficiency")
    public float sleepEfficiency;

    @ColumnInfo(name = "dayBreath")
    public int dayBreath;

    @ColumnInfo(name = "dayCough")
    public int dayCough;

    @ColumnInfo(name = "dayMove")
    public int dayMove;

    @ColumnInfo(name = "dayNoise")
    public int dayNoise;

    @ColumnInfo(name = "daySnore")
    public int daySnore;

    public DayRecord(){}

    public DayRecord(String storeDate, Date startSleepTime){
        this.storeDate = storeDate;
        this.startSleepTime = startSleepTime;
    }

//    public DayRecord(Date startSleepTime){
//        this.startSleepTime = startSleepTime;
//    }

    public void setStartSleepTime(Date startSleepTime){
        this.startSleepTime = startSleepTime;
    }

    public void setEndSleepTime(Date endSleepTime){
        this.endSleepTime = endSleepTime;
    }
}
