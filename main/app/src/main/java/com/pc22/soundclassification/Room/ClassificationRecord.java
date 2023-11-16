package com.pc22.soundclassification.Room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

@Entity
public class ClassificationRecord {

    @PrimaryKey(autoGenerate = true)
    public int uid;

    // TODO: Replace this with link(to date)
    @ColumnInfo(name = "CR_date")
    public Date date;    //EVENT HAPPEN DATE AND TIME

    @ColumnInfo(name = "CR_storeDate")
    public String storeDate;    //Which date does the event belongs

    @ColumnInfo(name = "CR_breath")
    public int breath;

    @ColumnInfo(name = "CR_snore")
    public int snore;

    @ColumnInfo(name = "CR_cough")
    public int cough;

    @ColumnInfo(name = "CR_move")
    public int move;

    @ColumnInfo(name = "CR_noise")
    public int noise;

    @ColumnInfo(name = "CR_awake")
    public double awake;

    public ClassificationRecord(){}

    public ClassificationRecord(Date date, String storeDate, int[] result) {
        this.date = date;
        this.storeDate = storeDate;
        this.breath = result[0];
        this.snore = result[1];
        this.cough = result[2];
        this.move = result[3];
        this.noise = result[4];
    }
}