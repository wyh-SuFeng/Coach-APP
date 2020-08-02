package com.example.coach.dbFlow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = LocationDatabase.class)
public class LocationRecord extends BaseModel {
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private int groupId;
    @Column
    private double longitude;
    @Column
    private double latitude;
    public int getGroupId() {
        return groupId;
    }
    public LocationRecord setGroupId(int groupId) {
        this.groupId = groupId;
        return this;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public LocationRecord setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public LocationRecord setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }
}
