package com.cn.wetrack.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cn.wetrack.R;
import com.cn.wetrack.entity.Location;
import com.cn.wetrack.entity.SResponse;
import com.cn.wetrack.entity.Vehicle;
import com.cn.wetrack.util.HttpRequestClient;
import com.cn.wetrack.util.SProtocol;
import com.cn.wetrack.widgets.CustomProgressDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 单车监控
 */
public class MonitorLocation extends FragmentActivity {
    private SWApplication glob;
    private ImageButton ImageButtonBack;// 返回
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
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


    private Location oldLocation;// 上一次定位信息
    private List<LatLng> points = new ArrayList<>();
    private LatLng old_point;
    private CustomProgressDialog progressDialog = null;// 提示消息
    private Calendar calendar = Calendar.getInstance();// 接收的时间
    private Date date = null;// 中间变量
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 字符转换
    private Calendar serverCalendar = Calendar.getInstance();//服务器时间
    private String Address;
    private int MaxQueryTime = 0;//解析位置的最大次数
    private Boolean IsFirst = true;
    private CountDownTimer downTimer;

    private BitmapDescriptor startIcon;
    private BitmapDescriptor directionIcon;

    //region 位置更新
    private Handler monitorUpdateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            switch (msg.what) {
                case 2: {
                    addMarkersToMap();
                    LatLng point  = new LatLng(glob.curVLocation.getLatitude(),
                            glob.curVLocation.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(point));

                    points.clear();
                    points.add(new LatLng(oldLocation.getLatitude(), oldLocation.getLongitude()));
                    points.add(point);
                    PolylineOptions polylineOptions = new PolylineOptions().width(5)
                            .color(0xff00ff00).addAll(points);
                    mMap.addPolyline(polylineOptions);

                    if (oldLocation.getLatitude().compareTo(glob.curVLocation.getLatitude()) != 0
                            || oldLocation.getLongitude().compareTo(glob.curVLocation.getLongitude()) != 0) {
                        LatLng oldPoint = new LatLng(oldLocation.getLatitude(), oldLocation.getLongitude());
                        MarkerOptions marker = new MarkerOptions()
                                .position(oldPoint).icon(directionIcon)
                                .anchor(0.5f, 0.5f).rotation(oldLocation.getAngle());
                        marker.title("direction");
                        mMap.addMarker(marker);
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
            switch (msg.what) {
                case 0:
                    try {
                        System.out.println("serverTime:" + msg.getData().getString("serverTime"));
                        date = dateFormat.parse(msg.getData().getString("serverTime"));
                        serverCalendar.setTime(date);

                        if (IsFirst) {
                            // 添加marker
                            addMarkersToMap();
                            if (CarMarker != null) {
                                CarMarker.setRotation(0);
                                CarMarker.setAnchor(0.5f, 0.5f);
                                CarMarker.setInfoWindowAnchor(0.5f, 0.5f);
                            }
                            // 设置终点坐标
                            old_point = new LatLng(glob.curVLocation.getLatitude(), glob.curVLocation.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(old_point));
                            CarMarker.showInfoWindow();
                            new GetAddressThread().start();

                            IsFirst = false;
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
            progressDialog.dismiss();
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
        glob = (SWApplication) getApplicationContext();
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
        progressDialog = new CustomProgressDialog(MonitorLocation.this);
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

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (marker.getTitle().compareTo("start") == 0)
                            return false;
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

                        return true;
                    }
                });

                LatLng point = new LatLng(glob.curVLocation.getLatitude(), glob.curVLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(point).icon(startIcon).anchor(0.5f, 1f).title("start"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
            }
        });

        oldLocation = glob.curVLocation;
        startIcon = BitmapDescriptorFactory.fromResource(R.drawable.line_start);
        directionIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_direction);

		/* 返回 */
        ImageButtonBack = (ImageButton) findViewById(R.id.monitor_back);
        ImageButtonBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
        progressDialog.setMessage(getString(R.string.refreshing));
        progressDialog.show();
        new GetServerTime().start();
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
        downTimer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        downTimer.cancel();
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
        Vehicle v = glob.curVehicle;
        Location l = glob.curVLocation;
        LatLng point = new LatLng(l.getLatitude(), l.getLongitude());
        old_point = point;
        if (CarMarker == null) {
            CarMarker = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("", 0))).position(point)
                    .title(v.getVehNoF()).snippet("")
                    .anchor(0.5f, 0.5f).infoWindowAnchor(0.5f, 0.5f));
        } else {
            CarMarker.setPosition(point);
        }

        try {
            date = dateFormat.parse(l.getTime());
            calendar.setTime(date);
            if (isOnline(serverCalendar, calendar)) {
                if (null == l.getAlarmmsg() || l.getAlarmmsg().length() <= 0) {
                    if (l.getVelocity() < 2) {
                        CarMarker.setTitle("static");
                        CarMarker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("j_", l.getAngle())));
                    } else {
                        CarMarker.setTitle("online");
                        CarMarker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("", l.getAngle())));
                    }
                } else {
                    CarMarker.setTitle("alarm");
                    CarMarker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("c_", l.getAngle())));
                }
            } else {
                CarMarker.setTitle("offline");
                CarMarker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("l_", l.getAngle())));
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
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

        Class<com.cn.wetrack.R.drawable> drawableClass = R.drawable.class;
        try {
            return drawableClass.getDeclaredField(drawableName).getInt(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return R.drawable.vehicle0;
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
            Location location = glob.curVLocation;
//                Address = getString(R.string.Alarm_address_getting);

            infoWindow_vehNof.setText(getString(R.string.car_number) + location.getVehNoF());
            infoWindow_time.setText(getString(R.string.car_time) + location.getTime());
            String state = getString(R.string.car_state);
            if (location.getVehStatus() == 1)
                state += getString(R.string.runing);
            if (location.getVehStatus() == 2)
                state += getString(R.string.stoping);
            if (location.getVehStatus() == 3)
                state += getString(R.string.vehicle_offline);
            if (location.getVehStatus() == 5)
                state += getString(R.string.no_location);
            infoWindow_state.setText(state);

            String acc = "ACC:";
            if (location.getDtStatus() > 0)
                acc += getString(R.string.open);
            else acc += getString(R.string.off);
            infoWindow_acc.setText(acc);

            infoWindow_speed.setText(getString(R.string.speed) + location.getVelocity() + "km/h");
            infoWindow_fuel.setText(String.format(getString(R.string.oils), location.getOil()));
            infoWindow_today_mileage.setText(String.format(getString(R.string.car_today_mileage), location.getTodayMile()));
            infoWindow_carLocation.setText(getString(R.string.car_location) + Address);
        }

    }

    // 判断是否在线
    public boolean isOnline(Calendar first, Calendar second) {
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

    /**
     * 获取地址
     */
    class GetAddressThread extends Thread {
        public void run() {
            /* 获取位置 */
            Address = getResources().getString(R.string.Main_get_Address_fail);
            Message message = new Message();
            SResponse response1 = HttpRequestClient.addressTranslate(
                    glob.curVLocation.getLongitude(),
                    glob.curVLocation.getLatitude());
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
}
