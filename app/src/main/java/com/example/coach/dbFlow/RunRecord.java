package com.example.coach.dbFlow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = LocationDatabase.class)
public class RunRecord extends BaseModel {
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private int groupId;
    @Column
    private double distance;
    @Column
    private String time;
    @Column
    private String duration;

    public int getGroupId() {
        return groupId;
    }

    public RunRecord setGroupId(int groupId) {
        this.groupId = groupId;
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getDistance() {
        return distance;
    }

    public RunRecord setDistance(double distance) {
        this.distance = distance;
        return this;
    }

    public String getTime() {
        return time;
    }

    public RunRecord setTime(String time) {
        this.time = time;
        return this;
    }

    public String getDuration() {
        return duration;
    }
    public RunRecord setDuration(String duration) {
        this.duration = duration;
        return this;
    }
}
