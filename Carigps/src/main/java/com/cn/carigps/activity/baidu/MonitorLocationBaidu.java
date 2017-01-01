package com.cn.carigps.activity.baidu;

import android.app.Activity;
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
import com.cn.carigps.R;
import com.cn.carigps.activity.MyApplication;
import com.cn.carigps.activity.VehicleList;
import com.cn.carigps.entity.Location;
import com.cn.carigps.entity.SResponse;
import com.cn.carigps.util.HttpRequestClient;
import com.cn.carigps.util.MappointUtil;
import com.cn.carigps.util.SProtocol;
import com.cn.carigps.widgets.CustomProgressDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 单车监控
 */
public class MonitorLocationBaidu extends Activity {

    private MappointUtil maputil = new MappointUtil();//GPS转换百度坐标
    private MyApplication glob;
    private ImageButton ImageButtonBack;// 返回
    private MapView mapView;
    private BaiduMap mBaiDuMap;
    private Button monitor;
    private Marker CarMarker = null;
    private ViewGroup infoWindowView = null;
    private InfoWindow mInfoWindow = null;
    private boolean IsInforwindowShowing = false;
    private String Address;
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

    private Location oldLocation;// 上一次定位信息
    private List<LatLng> points = new ArrayList<LatLng>();
    private Calendar serverCalendar = Calendar.getInstance();// 系统时间
    private Calendar calendar = Calendar.getInstance();// 接收的时间
    private java.util.Date date = null;// 中间变量
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 字符转换
    private CustomProgressDialog progressDialog = null;// 提示消息
    private int MaxQueryTime = 0;//解析位置的最大次数
    private CountDownTimer downTimer;
    private BitmapDescriptor startIcon;
    private BitmapDescriptor directionIcon;
    private boolean isFirst = true;

    //region  位置更新
    private Handler monitorUpdateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            switch (msg.what) {
                case 2: {
                    addMarkersToMap();
                    LatLng point = new LatLng(glob.curVLocation.getLatitude(),
                            glob.curVLocation.getLongitude());
                    mBaiDuMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(point));
                    mInfoWindow = getInfoWindow(point);
                    updateInfoWindow(CarMarker);
                    new MyAddressThread().start();
                    mBaiDuMap.hideInfoWindow();
                    mBaiDuMap.showInfoWindow(mInfoWindow);

                    points.clear();
                    points.add(new LatLng(oldLocation.getLatitude(), oldLocation.getLongitude()));
                    points.add(new LatLng(glob.curVLocation.getLatitude(),
                            glob.curVLocation.getLongitude()));
                    OverlayOptions ooPolyline = new PolylineOptions().width(5).color(0xff00ff00).points(points);
                    mBaiDuMap.addOverlay(ooPolyline);

                    if (oldLocation.getLatitude().compareTo(glob.curVLocation.getLatitude()) != 0
                            || oldLocation.getLongitude().compareTo(glob.curVLocation.getLongitude()) != 0) {
                        LatLng oldPoint = new LatLng(oldLocation.getLatitude(), oldLocation.getLongitude());
                        MarkerOptions marker = new MarkerOptions()
                                .position(oldPoint).icon(directionIcon)
                                .anchor(0.5f, 0.5f).rotate(360 - oldLocation.getAngle());
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("location", oldLocation);
                        marker.extraInfo(bundle);
                        marker.title("direction");
                        mBaiDuMap.addOverlay(marker);
                    }
                    break;
                }
                case 3: {
                    break;
                }
            }
        }
    };
    //endregion

    //region 获取服务器时间
    private Handler getServerTimeHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            switch (msg.what) {
                case 0:
                    try {
                        System.out.println("serverTime:" + msg.getData().getString("serverTime"));
                        date = dateFormat.parse(msg.getData().getString("serverTime"));
                        serverCalendar.setTime(date);
                        if (isFirst) {
                            // 添加marker
                            addMarkersToMap();
                            LatLng point = new LatLng(glob.curVLocation.getLatitude(), glob.curVLocation.getLongitude());
                            mBaiDuMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(point, 11));
                            mInfoWindow = getInfoWindow(point);
                            updateInfoWindow(CarMarker);
                            new MyAddressThread().start();
                            mBaiDuMap.hideInfoWindow();
                            mBaiDuMap.showInfoWindow(mInfoWindow);

                            isFirst = false;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1: {
                }
                case -1: {
                }
            }
        }

    };
    //endregion

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
//                        updateInfoWindow(PresentMarker);
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

        glob = (MyApplication) getApplicationContext();
        glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        int refreshTime = glob.sp.getInt("refreshTime", 10);
        downTimer = new CountDownTimer(refreshTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                progressDialog.setMessage(getString(R.string.refreshing));
                progressDialog.show();
                new GetServerTime().start();
                new GetLocationThread().start();
                downTimer.start();
            }
        };
        oldLocation = glob.curVLocation;
        progressDialog = new CustomProgressDialog(MonitorLocationBaidu.this);

        mapView = (MapView) findViewById(R.id.monitor_baidumap);
        mBaiDuMap = mapView.getMap();
        mapView.showZoomControls(false);

        // 响应marker点击事件
        mBaiDuMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getTitle().compareTo("start") == 0) {
                    return false;
                }
                mBaiDuMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(marker.getPosition()));
                mInfoWindow = getInfoWindow(marker.getPosition());
                updateInfoWindow(marker);
                mBaiDuMap.hideInfoWindow();
                mBaiDuMap.showInfoWindow(mInfoWindow);
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
                mBaiDuMap.hideInfoWindow();
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
        monitor.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MonitorLocationBaidu.this,
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

        startIcon = BitmapDescriptorFactory.fromResource(R.drawable.line_start);
        directionIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_direction);
        LatLng point = new LatLng(glob.curVLocation.getLatitude(), glob.curVLocation.getLongitude());
        mBaiDuMap.addOverlay(new MarkerOptions()
                .position(point)
                .icon(startIcon)
                .title("start")
                .anchor(0.5f, 1f));

    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        Location location = glob.curVLocation;
        LatLng point = new LatLng(glob.curVLocation.getLatitude(), glob.curVLocation.getLongitude());

        Bundle bundle = new Bundle();
        bundle.putParcelable("location", location);
        if (CarMarker == null) {
            CarMarker = (Marker) mBaiDuMap.addOverlay(new MarkerOptions()
                    .position(point)
                    .icon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("x_", 0)))
                    .title("marker")
                    .anchor(0.5f, 0.5f)
                    .extraInfo(bundle));

        } else {
            CarMarker.setPosition(point);
            CarMarker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("x_", 0)));
            CarMarker.setAnchor(0.5f, 0.5f);
            CarMarker.setExtraInfo(bundle);
        }

        try {
            date = dateFormat.parse(location.getTime());
            calendar.setTime(date);
            if (isonline(serverCalendar, calendar)) {
                if (null == location.getAlarmmsg() || location.getAlarmmsg().length() <= 0) {
                    if (location.getVelocity() < 2) {
                        CarMarker.setTitle("static");
                        CarMarker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("j_", location.getAngle())));
                    } else {
                        CarMarker.setTitle("online");
                        CarMarker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("x_", location.getAngle())));
                    }
                } else {
                    CarMarker.setTitle("alarm");
                    CarMarker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("c_", location.getAngle())));
                }
            } else {
                CarMarker.setTitle("offline");
                CarMarker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("l_", location.getAngle())));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mBaiDuMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(point));
    }

    protected void onResume() {
        mapView.onResume();
        progressDialog.setMessage(getString(R.string.refreshing));
        progressDialog.show();
        new GetServerTime().start();
        downTimer.start();
        super.onResume();
    }

    @Override
    protected void onPause() {
        downTimer.cancel();
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

    public InfoWindow getInfoWindow(LatLng point) {
        return new InfoWindow(infoWindowView, point, -13);
    }

    public void updateInfoWindow(Marker marker) {
        Location location = marker.getExtraInfo().getParcelable("location");
//        Address = getString(R.string.Alarm_address_getting);

        infoWindow_vehnof.setText(getString(R.string.car_number) + location.getVehNoF());
        inforwindow_time.setText(getString(R.string.car_time) + location.getTime());
        String state = getString(R.string.car_state);
        if (location.getVehStatus() == 1) {
            state += getString(R.string.runing);
//            marker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("x_", location.getAngle())));
        }
        if (location.getVehStatus() == 2) {
            state += getString(R.string.stoping);
//            marker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("j_", location.getAngle())));
        }
        if (location.getVehStatus() == 3) {
            state += getString(R.string.vehicle_offline);
//            marker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("l_", location.getAngle())));
        }
        if (location.getVehStatus() == 5) {
            state += getString(R.string.no_location);
//            marker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("d_", location.getAngle())));
        }
        inforwindow_state.setText(state);

        String acc = "ACC:";
        if (location.getDtStatus() > 0)
            acc += getString(R.string.open);
        else acc += getString(R.string.off);
        inforwindow_acc.setText(acc);

        inforwindow_speed.setText(getString(R.string.speed) + location.getVelocity() + "km/h");
        inforwindow_fuel.setText(String.format(getString(R.string.oils), location.getOil()));
        inforwindow_today_mileage.setText(String.format(getString(R.string.car_today_mileage), location.getTodayMile()));
        inforwindow_carlocation.setText(getString(R.string.car_location) + Address);
    }

    // 判断是否在线
    public boolean isonline(Calendar first, Calendar second) {
        if (first.get(Calendar.YEAR) == second.get(Calendar.YEAR)) {
            if (first.get(Calendar.MONTH) == second.get(Calendar.MONTH)) {
                if (first.get(Calendar.DATE) == second.get(Calendar.DATE)) {
                    if (first.get(Calendar.HOUR) == second.get(Calendar.HOUR)) {
                        if ((first.get(Calendar.MINUTE) - second
                                .get(Calendar.MINUTE)) < 30) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public int getDrawableIdWithAngle(String status, int angle) {
        String drawableName = status;

        if (angle == 0) {
            drawableName += "vehicle0";
        }
        if (angle > 0 && angle < 45) {
            drawableName += "vehicle0_45";
        }
        if (angle == 45) {
            drawableName += "vehicle45";
        }
        if (angle > 45 && angle < 90) {
            drawableName += "vehicle45_90";
        }
        if (angle == 90) {
            drawableName += "vehicle90";
        }
        if (angle > 90 && angle < 135) {
            drawableName += "vehicle90_135";
        }
        if (angle == 135) {
            drawableName += "vehicle135";
        }
        if (angle > 135 && angle < 180) {
            drawableName += "vehicle135_180";
        }
        if (angle == 180) {
            drawableName += "vehicle180";
        }
        if (angle > 180 && angle < 225) {
            drawableName += "vehicle180_225";
        }
        if (angle == 225) {
            drawableName += "vehicle225";
        }
        if (angle > 225 && angle < 270) {
            drawableName += "vehicle225_270";
        }
        if (angle == 270) {
            drawableName += "vehicle270";
        }
        if (angle > 270 && angle < 315) {
            drawableName += "vehicle270_315";
        }
        if (angle == 315) {
            drawableName += "vehicle315";
        }
        if (angle > 315 && angle < 360) {
            drawableName += "vehicle315_350";
        }

        Class<com.cn.carigps.R.drawable> drawableClass = R.drawable.class;
        try {
            return drawableClass.getDeclaredField(drawableName).getInt(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return R.drawable.x_vehicle0;
    }

    class GetLocationThread extends Thread {
        @Override
        public void run() {
            Bundle b = new Bundle();
            SResponse response = HttpRequestClient.getVehicleLocation(
                    glob.curVehicle.getSystemNo(), "0", glob.sp.getString("user", ""),
                    glob.sp.getString("psw", ""));
            if (response.getCode() == SProtocol.SUCCESS) {
                b.putBoolean("ret", true);
                List<Location> locations = (List<Location>) response.getResult();
                /* 上一次定位信息 */
                oldLocation = glob.curVLocation;
                /* 更新位置 */
                glob.curVLocation = locations.get(0);
                Message message = new Message();
                message.setData(b);
                message.what = 2;
                monitorUpdateHandler.sendMessage(message);

            } else {
                b.putBoolean("ret", false);
                b.putString("msg", SProtocol.getFailMessage(
                        response.getCode(), response.getMessage()));
                Message message = new Message();
                message.setData(b);
                message.what = 3;
                monitorUpdateHandler.sendMessage(message);
            }
            super.run();
        }
    }

    class GetServerTime extends Thread {
        public void run() {
            try {
                Message message = new Message();
                Bundle bundle = new Bundle();
                String serverTime;
                SResponse response = HttpRequestClient.getServerTime();
                if (response.getCode() == SProtocol.SUCCESS) {
                    serverTime = (String) response.getResult();
                    message.what = 0;
                } else {
                    serverTime = SProtocol.getFailMessage(response.getCode(),
                            response.getMessage());
                    message.what = 1;
                }
                bundle.putString("serverTime", serverTime);
                message.setData(bundle);
                getServerTimeHandler.sendMessage(message);
            } catch (Exception e) {
                getServerTimeHandler.sendEmptyMessage(-1);
            }
        }
    }

    /**
     * 获取地址
     */
    class MyAddressThread extends Thread {
        public void run() {
            Message message = new Message();
            Location l = glob.curVLocation;

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
