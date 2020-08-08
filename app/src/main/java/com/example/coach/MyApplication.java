package com.example.coach;

import android.app.Application;
import android.content.Context;

import androidx.recyclerview.widget.ItemTouchHelper;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

public class MyApplication extends Application {

private static Context context;
    @Override
    public void onCreate() {

        super.onCreate();
        //初始化DBFLOW
        FlowManager.init(new FlowConfig.Builder(this).build());
        context=getApplicationContext();
    }

    public static Context getAppContext() {
        return context;
    }
}
