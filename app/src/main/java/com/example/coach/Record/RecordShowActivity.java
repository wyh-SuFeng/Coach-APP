package com.example.coach.Record;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.example.coach.R;
import com.example.coach.dbFlow.DBUtils;
import com.example.coach.dbFlow.LocationRecord;

import java.util.ArrayList;
import java.util.List;

public class RecordShowActivity extends AppCompatActivity {
    private int groupId;
    private MapView mMapView;
    private AMap aMap;
    private LatLng latLng;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recordshow_activity);
        Intent intent = getIntent();
        groupId = intent.getIntExtra("groupId", 1);
        mMapView = findViewById(R.id.map_record_show);
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        drawLinear();
        latLng= getCurLatLng();
        if (latLng != null) {
            aMap.animateCamera(new CameraUpdateFactory().newLatLngZoom(latLng, 15));
        }

}

    public void drawLinear() {
        List<LocationRecord> list = DBUtils.selectLocationByGroupId(groupId);
        Log.e("pppppppppppppppp", "drawLinear: "+list.size()+"groupId"+groupId);
        List<LatLng> latLngList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            latLngList.add(new LatLng(list.get(i).getLatitude(), list.get(i).getLongitude()));
        }
        PolylineOptions options = new PolylineOptions();
        options.width(10).geodesic(true).color(Color.GREEN);
        options.addAll(latLngList);
        aMap.addPolyline(options);

    }

    public LatLng getCurLatLng() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return new LatLng(location.getLatitude(), location.getLongitude());

    }


    @Override
    protected void onDestroy() {
        Log.e("TAG", "ondestroy: "+"recordShowppppppppppppppppppppppppppppppp" );
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
        Log.e("TAG", "onresume: "+"recordShowppppppppppppppppppppppppppppppp" );
    }

    @Override
    protected void onPause() {
        super.onPause();
//        在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
        Log.e("TAG", "onpause: "+"recordShowppppppppppppppppppppppppppppppp" );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}
