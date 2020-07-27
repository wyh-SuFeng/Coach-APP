package com.example.coach;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements View.OnClickListener {
    private MapView mainMapView;
    private AMap aMap;
    private PolylineOptions polylineOptions;
    private Button buttonInit;
    private Button buttonSport;
    private EditText editText;
    private AMapLocation privLocation;
    private MyLocationStyle myLocationStyle;
    private AMap.OnMyLocationChangeListener myLocationChangeListener = new AMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            Log.d("onMyLocationChange", "onMyLocationChange: " + location.getLatitude());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //显示地图
        mainMapView = findViewById(R.id.mainMap);
        mainMapView.onCreate(savedInstanceState);// 调用地图所必须重写
        if (aMap == null) {
            aMap = mainMapView.getMap();
        }
        initPosition();
        aMap.setOnMyLocationChangeListener(myLocationChangeListener);
        buttonInit = findViewById(R.id.button_init);
        buttonSport = findViewById(R.id.button_sport);
        editText = findViewById(R.id.text);
        buttonInit.setOnClickListener(this);
        buttonSport.setOnClickListener(this);
    }

    private void setFloatMapUi(AMap aMap) {
        /**
         * 设置一些amap的属性
         */
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);//隐藏缩放按钮就是那个+ - 放大和缩小额两个按钮
        uiSettings.setCompassEnabled(true);// 设置指南针是否显示
        uiSettings.setRotateGesturesEnabled(true);// 设置地图旋转是否可用
        uiSettings.setTiltGesturesEnabled(true);// 设置地图倾斜是否可用
        uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
    }

    private void setLocationPointStyle() {
        //自定义系统定位小蓝点
        myLocationStyle = new MyLocationStyle();
        myLocationStyle.strokeColor(Color.argb(80, 50, 230, 230));//设置定位蓝点精度圆圈的边框颜色的方法。
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 189, 170));//设置定位蓝点精度圆圈的填充颜色的方法。
        myLocationStyle.strokeWidth(10);//设置定位蓝点精度圈的边框宽度的方法。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
    }

    private void initPosition() {
        setFloatMapUi(aMap);
        setLocationPointStyle();
        aMap.setMyLocationEnabled(true);// 置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_init:
                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE));//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
                break;
            case R.id.button_sport:
                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE));//连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（1秒1次定位）
                break;
            default:

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mainMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mainMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mainMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mainMapView.onSaveInstanceState(outState);
    }


}
