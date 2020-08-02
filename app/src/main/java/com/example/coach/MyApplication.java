package com.example.coach;

import android.app.Application;
import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowManager;

public class MyApplication extends Application {

Context context=new MainActivity();
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化DBFLOW
        FlowManager.init(this);
    }
}
