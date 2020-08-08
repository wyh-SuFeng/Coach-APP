package com.example.coach.dbFlow;

import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.example.coach.MainActivity;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.SqlUtils;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class DBUtils {
    //路径存储
    public static void storeLocation(List<LocationRecord> locationList) {
        // save rows
        FlowManager.getDatabase(LocationDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<LocationRecord>() {
                            @Override
                            public void processModel(LocationRecord locationRecord, DatabaseWrapper wrapper) {

                                new LocationRecord()
                                        .setGroupId(locationRecord.getGroupId())
                                        .setLatitude(locationRecord.getLatitude())
                                        .setLongitude(locationRecord.getLongitude())
                                        .save();

                            }
                        }).addAll(locationList).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        Log.e(TAG, "onError: jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        Log.e(TAG, "success: rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
                    }
                }).build().execute();

    }

    //一步查询//路径绘画
    public static void drawLinesFromDatabase() {
        //方式2:QueryResultListCallback：集合查询
        SQLite.select().from(LocationRecord.class)//.where(BigSeaInfo0_Table.name.is(""))
                .async()//异步查询
                .queryListResultCallback(new QueryTransaction.
                        QueryResultListCallback<LocationRecord>() {
                    @Override
                    public void onListQueryResult(QueryTransaction transaction, @NonNull List<LocationRecord> tResult) {
                        // printData(tResult);//更新UI
                        List<LatLng> list = new ArrayList<>();
                        for (LocationRecord locationRecord : tResult) {
                            list.add(new LatLng(locationRecord.getLatitude(), locationRecord.getLongitude()));
                        }
                        PolylineOptions options = new PolylineOptions();
                        options.width(10).geodesic(true).color(Color.RED);
                        options.addAll(list);
                        MainActivity.aMap.addPolyline(options);
                    }
                }).error(new Transaction.Error() {
            @Override
            public void onError(@NonNull Transaction transaction,
                                @NonNull Throwable error) {
                Log.i("查询定位结果", "" + error.getMessage());
            }
        }).success(new Transaction.Success() {
            @Override
            public void onSuccess(@NonNull Transaction transaction) {
                Log.i("查询定位结果", "定位成功");
            }
        }).execute();
    }

    public static void deleteTable(Class<?> table) {
        Delete.table(table);
    }

    public static void deleteLocationRecordByGroupId(int groupId) {
        SQLite.delete().from(LocationRecord.class).where(LocationRecord_Table.groupId .eq(groupId)).execute();

        //方式2:QueryResultListCallback：集合查询
//        SQLite.select().from(LocationRecord.class).where(LocationRecord_Table.groupId.is(groupId))
//                .async()//异步查询
//                .queryListResultCallback(new QueryTransaction.
//                        QueryResultListCallback<LocationRecord>() {
//                    @Override
//                    public void onListQueryResult(QueryTransaction transaction, @NonNull List<LocationRecord> tResult) {
//                        // printData(tResult);//更新UI
//                        for (LocationRecord locationRecord : tResult) {
//                            locationRecord.delete();
//                        }
//                    }
//                }).error(new Transaction.Error() {
//            @Override
//            public void onError(@NonNull Transaction transaction,
//                                @NonNull Throwable error) {
//                Log.i("查询定位结果", "" + error.getMessage());
//            }
//        }).success(new Transaction.Success() {
//            @Override
//            public void onSuccess(@NonNull Transaction transaction) {
//                Log.i("查询定位结果", "定位成功");
//            }
//        }).execute();
    }

    public static void deleteRunRecordByGroupId(int groupId) {
        SQLite.delete().from(RunRecord.class).where(RunRecord_Table.groupId .eq( groupId)).execute();

    }

    public static int selectLastGroupId() {
        LocationRecord locationRecord = SQLite.select().from(LocationRecord.class).orderBy(LocationRecord_Table.groupId, false).limit(1).querySingle();
        Log.d(TAG, "selectLastGroupId: "+locationRecord);
        if(locationRecord==null){
            return 1;
        }else{
            Log.e(TAG, "selectLastGroupId: " + locationRecord.getGroupId());
            return locationRecord.getGroupId();
        }
    }

    public static void storeRunRecord(int groupId, long beginTime, double distance) {
        long curTime=System.currentTimeMillis();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
        new RunRecord()
                .setGroupId(groupId)
                .setTime(sdf1.format(beginTime))
                .setDistance(distance)
                .setDuration(sdf2.format((curTime - beginTime) - 8 * 60 * 60 * 1000))
                .save();
    }

    public static List<RunRecord> selectAllRunRecord() {
        Log.e(TAG, "selectAllRunRecord: " );
        return SQLite.select().from(RunRecord.class).queryList();
    }
    public static List<LocationRecord> selectLocationByGroupId(int groupId) {
        Log.e(TAG, "selectLocationByGroupId: " );
        return SQLite.select().from(LocationRecord.class).where(LocationRecord_Table.groupId.eq(groupId)).queryList();
    }
}
