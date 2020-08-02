package com.example.coach.dbFlow;


import com.raizlabs.android.dbflow.annotation.Database;

//dbFlow的表必须要有主键，如果没有会报错
@Database(name = LocationDatabase.NAME, version = LocationDatabase.VERSION)
public class LocationDatabase {
    public static final String NAME = "LocationDatabase";
    public static final int VERSION=1;
}

