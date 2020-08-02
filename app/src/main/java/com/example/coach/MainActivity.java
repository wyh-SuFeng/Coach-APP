package com.example.coach;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.GroundOverlay;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.navi.view.AmapCameraOverlay;
import com.example.coach.dbFlow.DBUtils;
import com.example.coach.dbFlow.LocationRecord;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static AMap aMap;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    private AMapLocationClientOption mLocationOption = null;
    private MapView mMapView;
    private MyLocationStyle myLocationStyle;
    private AMapLocation privLocation;
    private int groupId;
    private TextView text;
    private Button locationInfoButton;
    private Button stopLocationButton;
    private boolean displayLocation = false;
    private boolean stopOrStartLocation = true;
    private List<LocationRecord> locationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用-----地图控件会遮挡其他所有控件
        mMapView = findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        initActivity();
        initPosition();
        if (permissionsGrant()) {
            initLocation();
        }
    }

    private void initActivity() {
        text = findViewById(R.id.text);
        text.setVisibility(View.GONE);
        locationInfoButton = findViewById(R.id.button_info);
        locationInfoButton.setOnClickListener(this);
        stopLocationButton = findViewById(R.id.stop_location);
        stopLocationButton.setOnClickListener(this);
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

    private boolean permissionsGrant() {
        //需要动态获取的所有权限
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION,//与上同属一个权限组location
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE,//与上同属一个权限组storage
//                Manifest.permission.READ_PHONE_STATE//属于phone权限，允许程序访问电话状态
        List<String> permissionList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissionArr = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissionArr, 1);
        }
        return true;
    }

    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        // 初始化定位参数
        initLocationOption();

        mLocationClient.setLocationOption(mLocationOption);
        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        mLocationClient.stopLocation();
//      mLocationClient.startLocation();
        //设置定位回调监听
        mLocationClient.setLocationListener(aMapLocationListener);
    }

    private void initLocationOption() {
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setInterval(2000);
        mLocationOption.setSensorEnable(true);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        mLocationOption= getDefaultOption();
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLocation();
                } else {
                    Toast.makeText(this, "需要授予权限", Toast.LENGTH_LONG).show();
                    finish();
                }
            default:
        }

    }


    //声明定位监听器
    public AMapLocationListener aMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            //AMapLocation包含经度纬度国家省份。。所有位置信息

            if (aMapLocation != null) {
                if (displayLocation) printLocationMessage(aMapLocation);
                //对定位数据实时存储
                Log.e("wwww", "onLocationChanged: " + aMapLocation.getLatitude());
                locationList.add(new LocationRecord()
                        .setGroupId(groupId+ 1)
                        .setLatitude(aMapLocation.getLatitude())
                        .setLongitude(aMapLocation.getLongitude()));
                //一边定位一边连线
                drawLines(aMapLocation);
                privLocation = aMapLocation;
            }
        }
    };

    private void printLocationMessage(AMapLocation location) {
        StringBuffer sb = new StringBuffer();
        if (location.getErrorCode() == 0) {
            sb.append("定位成功" + "\n");
            sb.append("定位类型: " + getLocationMessageString(location.getLocationType()) + "\n");
            sb.append("经    度    : " + location.getLongitude() + "\n");
            sb.append("纬    度    : " + location.getLatitude() + "\n");
            sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
            sb.append("提供者    : " + location.getProvider() + "\n");

            sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
            sb.append("角    度    : " + location.getBearing() + "\n");
            // 获取当前提供定位服务的卫星个数
//            sb.append("星    数    : " + location.getSatellites() + "\n");
//            sb.append("国    家    : " + location.getCountry() + "\n");
//            sb.append("省            : " + location.getProvince() + "\n");
//            sb.append("市            : " + location.getCity() + "\n");
//            sb.append("城市编码 : " + location.getCityCode() + "\n");
//            sb.append("区            : " + location.getDistrict() + "\n");
//            sb.append("区域 码   : " + location.getAdCode() + "\n");
//            sb.append("地    址    : " + location.getAddress() + "\n");
//            sb.append("兴趣点    : " + location.getPoiName() + "\n");
        } else {
            //定位失败
            sb.append("定位失败" + "\n");
            sb.append("错误码:" + location.getErrorCode() + "\n");
            sb.append("错误信息:" + location.getErrorInfo() + "\n");
            sb.append("错误描述:" + location.getLocationDetail() + "\n");
        }
        sb.append("***定位质量报告***").append("\n");
        sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启" : "关闭").append("\n");
        sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
        sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
        sb.append("* 网络类型：" + location.getLocationQualityReport().getNetworkType()).append("\n");
        sb.append("* 网络耗时：" + location.getLocationQualityReport().getNetUseTime()).append("\n");
        sb.append("****************").append("\n");
        text.setText(sb.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_info:
                if (!displayLocation) {
                    text.setVisibility(View.VISIBLE);
                    displayLocation = true;
                } else {
                    text.setVisibility(View.GONE);
                    displayLocation = false;
                }
                break;
            case R.id.stop_location:
                if (stopOrStartLocation) {
                    groupId=DBUtils.selectLastGroupId();
                    mLocationClient.startLocation();
                    stopLocationButton.setText("停止定位，存储定位数据");
                    stopOrStartLocation = false;
                } else {
                    mLocationClient.stopLocation();
                    //存储定位数据
                    stopLocationButton.setText("开始定位，获取定位数据");
                    stopOrStartLocation = true;
                    DBUtils.storeLocation(locationList);
                    locationList.clear();
                }
                break;
            default:
        }
    }

    private String getLocationMessageString(int locationType) {
        String str = "";
        switch (locationType) {
            case 0:
                str = "定位失败";
                break;
            case 1:
                str = "GPS定位结果";
                break;
            case 2:
                str = "前次定位结果";
                break;
            case 4:
                str = "缓存定位结果";
                break;
            case 5:
                str = "Wifi定位结果";
                break;
            case 6:
                str = "基站定位结果";
                break;
            case 8:
                str = "离线定位结果";
                break;
            case 9:
                str = "最后位置缓存";
                break;
            default:

        }
        return str;
    }

    private String getGPSStatusString(int statusCode) {
        String str = "";
        switch (statusCode) {
            case AMapLocationQualityReport.GPS_STATUS_OK:
                str = "GPS状态正常";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
                str = "手机中没有GPS Provider，无法进行GPS定位";
                break;
            case AMapLocationQualityReport.GPS_STATUS_OFF:
                str = "GPS关闭，建议开启GPS，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
                str = "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
                str = "没有GPS定位权限，建议开启gps定位权限";
                break;
        }
        return str;
    }

    public void drawLines(AMapLocation curLocation) {

        if (null == privLocation) {
            return;
        }
        if (curLocation.getLatitude() != 0.0 && curLocation.getLongitude() != 0.0
                && privLocation.getLongitude() != 0.0 && privLocation.getLatitude() != 0.0) {
            PolylineOptions options = new PolylineOptions();
            //上一个点的经纬度
            options.add(new LatLng(privLocation.getLatitude(), privLocation.getLongitude()));
            //当前的经纬度
            options.add(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()));
            options.width(10).geodesic(true).color(Color.GREEN);
            aMap.addPolyline(options);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        mLocationClient.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }


}
