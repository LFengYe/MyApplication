package com.cn.carigps.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.carigps.R;
import com.cn.carigps.entity.Alarm;
import com.cn.carigps.entity.Location;
import com.cn.carigps.entity.MileageOilReport;
import com.cn.carigps.entity.SResponse;
import com.cn.carigps.util.HttpRequestClient;
import com.cn.carigps.util.SProtocol;
import com.cn.carigps.widgets.CustomProgressDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * 单车监控
 */
public class OilDetailInfo extends FragmentActivity {
    private MyApplication glob;
    private ImageButton ImageButtonBack;// 返回
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private BitmapDescriptor startIcon;
    private String Address;
    private MileageOilReport oilReport;
    private Alarm alarmItem;
    private String type;
    private Location location;

    private View mWindow;
    private Marker CarMarker = null;
    private TextView infoWindow_vehNof;
    private TextView infoWindow_time;
    private TextView infoWindow_state;
    private TextView infoWindow_acc;
    private TextView infoWindow_speed;
    private TextView infoWindow_fuel;
    private TextView infoWindow_today_mileage;
    private TextView infoWindow_carLocation;
    ;
    private CustomProgressDialog progressDialog = null;// 提示消息
    private int MaxQueryTime = 0;//解析位置的最大次数

    //region  获取地址
    private Handler getAddressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    MaxQueryTime = 0;
                    if (CarMarker != null) {
                        CarMarker.showInfoWindow();
                    }
                    break;
                case 1:
                    if (MaxQueryTime < 3) {
                        new GetAddressThread().start();
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
        setContentView(R.layout.monitorlocation);
        glob = (MyApplication) getApplicationContext();
        glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);

        /**加载地图*/
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.monitor_map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                mWindow = getLayoutInflater().inflate(R.layout.alllocation_markerinforwindow, null);
                mWindow.findViewById(R.id.inforwindow_right).setVisibility(View.GONE);
                mWindow.findViewById(R.id.inforwindow_bottom).setVisibility(View.GONE);
                mWindow.findViewById(R.id.divider_view).setVisibility(View.GONE);
                // 找各个标签
                infoWindow_vehNof = (TextView) mWindow.findViewById(R.id.inforwindow_vehnof);
                infoWindow_time = (TextView) mWindow.findViewById(R.id.inforwindow_time);
                infoWindow_state = (TextView) mWindow.findViewById(R.id.inforwindow_state);
                infoWindow_acc = (TextView) mWindow.findViewById(R.id.inforwindow_acc);
                infoWindow_speed = (TextView) mWindow.findViewById(R.id.inforwindow_speed);
                infoWindow_fuel = (TextView) mWindow.findViewById(R.id.inforwindow_fuel);
                infoWindow_today_mileage = (TextView) mWindow.findViewById(R.id.inforwindow_today_mileage);
                infoWindow_carLocation = (TextView) mWindow.findViewById(R.id.inforwindow_carlocation);
                mMap.setInfoWindowAdapter(new AllLocationInfoWindowAdapter());

                addMarkersToMap();
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

        startIcon = BitmapDescriptorFactory.fromResource(R.drawable.pin_green);
        Bundle bundle = getIntent().getExtras();
        oilReport = bundle.getParcelable("oilReportItem");
        alarmItem = bundle.getParcelable("alarmItem");
        type = bundle.getString("type");
        Address = getString(R.string.Alarm_address_getting);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 返回鍵
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
            finish();
        return true;
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {

        location = new Location();
        LatLng point = null;

        if (type.equals("oil")) {
            if (null != oilReport.getAddOilAddress()) {
                String[] address = oilReport.getAddOilAddress().split("\\|");
                if (address != null && address.length == 2) {
                    point = new LatLng(Double.valueOf(address[1]), Double.valueOf(address[0]));
                    location.setLongitude(Double.valueOf(address[0]));
                    location.setLatitude(Double.valueOf(address[1]));
                } else {
                    Toast.makeText(OilDetailInfo.this, R.string.no_location, Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                Toast.makeText(OilDetailInfo.this, R.string.no_location, Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (type.equals("alarm")) {
            point = new LatLng(alarmItem.getLatitude(), alarmItem.getLongitude());
            location.setLatitude(alarmItem.getLatitude());
            location.setLongitude(alarmItem.getLongitude());
        }

        if (CarMarker == null) {
            CarMarker = mMap.addMarker(new MarkerOptions()
                    .icon(startIcon).position(point).snippet("")
                    .anchor(0.5f, 0.5f).infoWindowAnchor(0.5f, 0.5f));
        } else {
            CarMarker.setPosition(point);
            CarMarker.setIcon(startIcon);
            CarMarker.setAnchor(0.5f, 0.5f);
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
        CarMarker.showInfoWindow();
        new GetAddressThread().start();
    }

    /*
     * 自定义弹出窗 *
     */
    public class AllLocationInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            if (CarMarker != null) {
                render(marker);
                return mWindow;
            } else {
                return null;
            }
        }

        private void render(Marker marker) {
            if (type.equals("oil")) {
                infoWindow_vehNof.setText(getString(R.string.car_number) + oilReport.getVehNoF());
                infoWindow_time.setText(getString(R.string.car_time) + oilReport.getAddOilTime());
                infoWindow_speed.setText(String.format(getString(R.string.add_oil), Float.valueOf(oilReport.getAddOilValue())));
                infoWindow_fuel.setVisibility(View.GONE);
                infoWindow_today_mileage.setText(String.format(getString(R.string.car_today_mileage), oilReport.getTotalMiles()));
            }
            if (type.equals("alarm")) {
                infoWindow_vehNof.setText(alarmItem.getAlarmType());
                infoWindow_time.setText(getString(R.string.car_time) + alarmItem.getTime());
                infoWindow_speed.setText(getString(R.string.speed) + alarmItem.getVelocity() + "km/h");
                infoWindow_fuel.setVisibility(View.GONE);
                infoWindow_today_mileage.setVisibility(View.GONE);
            }

            infoWindow_state.setVisibility(View.GONE);
            infoWindow_acc.setVisibility(View.GONE);

            infoWindow_carLocation.setText(getString(R.string.car_location) + Address);
        }

    }

    /**
     * 获取地址
     */
    class GetAddressThread extends Thread {
        public void run() {
            /* 获取位置 */
            Address = getResources().getString(R.string.Location_address_getting);
            Message message = new Message();
            SResponse response1 = HttpRequestClient.addressTranslate(
                    location.getLongitude(),
                    location.getLatitude());
            if (response1.getCode() == SProtocol.SUCCESS) {
                Address = (String) response1.getResult();
                message.what = 0;
            } else {
                Address = response1.getMessage();
                message.what = 1;
            }

            getAddressHandler.sendMessage(message);
        }
    }
}
