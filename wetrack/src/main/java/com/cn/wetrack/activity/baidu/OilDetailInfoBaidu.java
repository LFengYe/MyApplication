package com.cn.wetrack.activity.baidu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.cn.wetrack.R;
import com.cn.wetrack.activity.OilDetailInfo;
import com.cn.wetrack.activity.SWApplication;
import com.cn.wetrack.activity.VehicleList;
import com.cn.wetrack.entity.Alarm;
import com.cn.wetrack.entity.Location;
import com.cn.wetrack.entity.MileageOilReport;
import com.cn.wetrack.entity.SResponse;
import com.cn.wetrack.util.HttpRequestClient;
import com.cn.wetrack.util.MappointUtil;
import com.cn.wetrack.util.SProtocol;
import com.cn.wetrack.widgets.CustomProgressDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 单车监控
 */
public class OilDetailInfoBaidu extends Activity {

    private MappointUtil maputil = new MappointUtil();//GPS转换百度坐标
    private SWApplication glob;
    private ImageButton ImageButtonBack;// 返回
    private MapView mapView;
    private BaiduMap mBaiDuMap;
    private Button monitor;
    private Marker CarMarker = null;
    private ViewGroup infoWindowView = null;
    private InfoWindow mInfoWindow = null;
    private String Address;
    private MileageOilReport oilReport;
    private Alarm alarmItem;
    private String type;
    private Location location;

    /**
     * 找到各个弹出窗的标签
     */
    private TextView infoWindow_vehnof;
    private TextView inforwindow_time;
    private TextView inforwindow_state;
    private TextView inforwindow_acc;
    private TextView inforwindow_speed;
    private TextView inforwindow_fuel;
    private TextView inforwindow_today_mileage;
    private TextView inforwindow_carlocation;
    private ImageView infordowclose;

    private CustomProgressDialog progressDialog = null;// 提示消息
    private int MaxQueryTime = 0;//解析位置的最大次数
    private BitmapDescriptor startIcon;

    //region  获取地址
    private Handler getAddressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    MaxQueryTime = 0;
                    if (CarMarker != null)
                        inforwindow_carlocation.setText(getString(R.string.car_location) + Address);
                    break;
                case 1:
                    if (MaxQueryTime < 3) {
                        new MyAddressThread().start();
                        MaxQueryTime++;
                    } else {
                        MaxQueryTime = 0;
                    }
                    break;
            }
        }
    };
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.monitorlocation_baidu);

        glob = (SWApplication) getApplicationContext();
        glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        progressDialog = new CustomProgressDialog(OilDetailInfoBaidu.this);

        mapView = (MapView) findViewById(R.id.monitor_baidumap);
        mBaiDuMap = mapView.getMap();
        mapView.showZoomControls(false);

        // 响应marker点击事件
        mBaiDuMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mBaiDuMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(marker.getPosition()));
                return true;
            }
        });
        // 响应地图点击事件
        mBaiDuMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0) {
            }
        });
        /* 返回 */
        ImageButtonBack = (ImageButton) findViewById(R.id.monitor_back);
        ImageButtonBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /* 监控 */
        monitor = (Button) this.findViewById(R.id.monitor_monitorButton);
        monitor.setVisibility(View.GONE);
        monitor.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OilDetailInfoBaidu.this,
                        VehicleList.class);
                startActivity(intent);
                finish();
            }
        });

        /* 找弹出窗 */
        infoWindowView = (ViewGroup) getLayoutInflater().inflate(R.layout.alllocation_markerinforwindow, null);
        infoWindowView.findViewById(R.id.inforwindow_right).setVisibility(View.GONE);
        infoWindowView.findViewById(R.id.inforwindow_bottom).setVisibility(View.GONE);
        infoWindowView.findViewById(R.id.divider_view).setVisibility(View.GONE);

        // 找各个标签
        infoWindow_vehnof = (TextView) infoWindowView.findViewById(R.id.inforwindow_vehnof);
        inforwindow_time = (TextView) infoWindowView.findViewById(R.id.inforwindow_time);
        inforwindow_state = (TextView) infoWindowView.findViewById(R.id.inforwindow_state);
        inforwindow_acc = (TextView) infoWindowView.findViewById(R.id.inforwindow_acc);
        inforwindow_speed = (TextView) infoWindowView.findViewById(R.id.inforwindow_speed);
        inforwindow_fuel = (TextView) infoWindowView.findViewById(R.id.inforwindow_fuel);
        inforwindow_today_mileage = (TextView) infoWindowView.findViewById(R.id.inforwindow_today_mileage);
        inforwindow_carlocation = (TextView) infoWindowView.findViewById(R.id.inforwindow_carlocation);

        startIcon = BitmapDescriptorFactory.fromResource(R.drawable.pin_green);
        Bundle bundle = getIntent().getExtras();
        oilReport = bundle.getParcelable("oilReportItem");
        alarmItem = bundle.getParcelable("alarmItem");
        type = bundle.getString("type");

        addMarkersToMap();
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        LatLng point = null;
        location = new Location();
        if (type.equals("oil")) {
            if (null != oilReport.getAddOilAddress()) {
                System.out.println("address:" + oilReport.getAddOilAddress());
                String[] address = oilReport.getAddOilAddress().split("\\|");
                if (address != null && address.length == 2) {
                    point = new LatLng(Double.valueOf(address[1]), Double.valueOf(address[0]));
                    location.setLongitude(Double.valueOf(address[0]));
                    location.setLatitude(Double.valueOf(address[1]));
                } else {
                    Toast.makeText(OilDetailInfoBaidu.this, R.string.no_location, Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                Toast.makeText(OilDetailInfoBaidu.this, R.string.no_location, Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (type.equals("alarm")) {
            point = new LatLng(alarmItem.getLatitude(), alarmItem.getLongitude());
            location.setLatitude(alarmItem.getLatitude());
            location.setLongitude(alarmItem.getLongitude());
        }

        if (CarMarker == null) {
            CarMarker = (Marker) mBaiDuMap.addOverlay(new MarkerOptions()
                    .position(point).icon(startIcon).anchor(0.5f, 0.5f));
        } else {
            CarMarker.setPosition(point);
            CarMarker.setIcon(startIcon);
            CarMarker.setAnchor(0.5f, 0.5f);
        }

        mBaiDuMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(point));
        mInfoWindow = GetInfowindow(point);
        updateInfoWindow();
        new MyAddressThread().start();
        mBaiDuMap.hideInfoWindow();
        mBaiDuMap.showInfoWindow(mInfoWindow);
    }

    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();

    }

    /**
     * 返回鍵
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            finish();
        return true;
    }

    public InfoWindow GetInfowindow(LatLng point) {
        return new InfoWindow(infoWindowView, point, -30);
    }

    public void updateInfoWindow() {
        Address = getString(R.string.Alarm_address_getting);

        if (type.equals("oil")) {
            infoWindow_vehnof.setText(getString(R.string.car_number) + oilReport.getVehNoF());
            inforwindow_time.setText(getString(R.string.car_time) + oilReport.getAddOilTime());
            inforwindow_speed.setText(String.format(getString(R.string.add_oil), Float.valueOf(oilReport.getAddOilValue())));
            inforwindow_fuel.setVisibility(View.GONE);
            inforwindow_today_mileage.setText(String.format(getString(R.string.car_today_mileage), oilReport.getTotalMiles()));
        }
        if (type.equals("alarm")) {
            infoWindow_vehnof.setText(alarmItem.getAlarmType());
            inforwindow_time.setText(getString(R.string.car_time) + alarmItem.getTime());
            inforwindow_speed.setText(getString(R.string.speed) + alarmItem.getVelocity() + "km/h");
            inforwindow_fuel.setVisibility(View.GONE);
            inforwindow_today_mileage.setVisibility(View.GONE);
        }

        inforwindow_state.setVisibility(View.GONE);
        inforwindow_acc.setVisibility(View.GONE);

        inforwindow_carlocation.setText(getString(R.string.car_location) + Address);
    }

    /**
     * 获取地址
     */
    class MyAddressThread extends Thread {
        public void run() {
            Message message = new Message();
            Location l = location;

            SResponse response = HttpRequestClient.addressTranslate(
                    l.getLongitude(), l.getLatitude());
            if (response.getCode() == SProtocol.SUCCESS) {
                Address = (String) response.getResult();
                message.what = 0;
            } else {
                Address = SProtocol.getFailMessage(response.getCode(),
                        response.getMessage());
                message.what = 1;
            }

            getAddressHandler.sendMessage(message);

        }
    }
}
