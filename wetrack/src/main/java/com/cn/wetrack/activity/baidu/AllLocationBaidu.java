package com.cn.wetrack.activity.baidu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
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
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.cn.wetrack.R;
import com.cn.wetrack.activity.Dizhijiexi;
import com.cn.wetrack.activity.ExpiredListAdapter;
import com.cn.wetrack.activity.SWApplication;
import com.cn.wetrack.activity.SelectDate;
import com.cn.wetrack.activity.VehicleList;
import com.cn.wetrack.entity.Location;
import com.cn.wetrack.entity.MapPoint;
import com.cn.wetrack.entity.SResponse;
import com.cn.wetrack.entity.ServiceExpired;
import com.cn.wetrack.entity.Vehicle;
import com.cn.wetrack.util.AppUtils;
import com.cn.wetrack.util.HttpRequestClient;
import com.cn.wetrack.util.MappointUtil;
import com.cn.wetrack.util.SProtocol;
import com.cn.wetrack.widgets.CustomProgressDialog;
import com.cn.wetrack.widgets.Popup;
import com.cn.wetrack.widgets.PopupUtils;
import com.google.android.gms.maps.GoogleMap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 所有车辆显示
 */
public class AllLocationBaidu extends Activity {
    private static final String TAG = "AllLocationBaidu";

    private SWApplication glob;//全局变量
    private ImageButton ImageButtonBack;// 返回
    private Button monitorButton;// 监控
    private MapView mapView;//地图控件
    private Map<String, String> vehNoMarkerMap = new HashMap<>();// 车牌号标注哈希表
//    private ImageButton layerButton;// 图层按钮

    private BaiduMap mBaiDuMap = null;//百度地图管理类
    //    private MapStatusUpdate mapstatusupdate = null;
    private ViewGroup infoWindowView = null;
    private InfoWindow mInfoWindow = null;
    /**
     * 找到各个弹出窗的标签
     */
    private TextView infoWindow_vehnof;
    private TextView infoWindow_time;
    private TextView infoWindow_state;
    private TextView infoWindow_acc;
    private TextView infoWindow_speed;
    private TextView infoWindow_fuel;
    private TextView infoWindow_today_mileage;
    private TextView infoWindow_CarLocation;
    private ImageView infoWindow_close;
    private TextView trackBtn;
    private TextView orbitReplyBtn;

    private View popupLyaout;
    private TextView markerTitle;

    private Calendar serverCalendar = Calendar.getInstance();// 系统时间
    private Calendar calendar = Calendar.getInstance();// 接收的时间
    private Date date = null;// 中间变量
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 字符转换
    private Intent intent = new Intent();
    private Marker PresentMarker = null;// 标记marker
    private LatLng end_point;// 驾车检索起始点，终止点
    private ArrayList<Marker> markerList = new ArrayList<>();// 车子标记物集合
    private HashMap<Marker, Vehicle> MarkerVehMap = new HashMap<>();// 车子标记物哈希表
    private String Address = "";
    private CustomProgressDialog progressDialog = null;// 提示消息
    private boolean isInfoWindowShowing = false;// inforwindow是否显示
    private Boolean isActivityAlive = true;
    // 向左箭头，向右箭头
    private ImageButton nextCar_leftBtn, nextCar_rightBtn;
    private int Index = 0;
    private int MaxQueryTime = 0;//解析位置的最大次数
    private MappointUtil mapUtil = new MappointUtil();//GPS转换百度坐标
    private CountDownTimer downTimer;
    private BitmapDescriptor mapPointIcon;
    private List<MapPoint> mapPointList;
    private boolean isFirst = true;

    private ImageButton layerButton;
    private int mapType = 0;

    private LatLngBounds.Builder bounds;


    private PopupWindow popupDialog = null;

    //region 获取所有车辆位置信息返回处理
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            Log.i(TAG, "get all location with message what:" + message.what);
            switch (message.what) {
                case 6:
                    for (Marker marker : markerList) {
                        Location l = glob.vehNoLocationMap.get(vehNoMarkerMap.get(marker.toString()));
                        marker.setPosition(new LatLng(l.getLatitude(), l.getLongitude()));
                        setMarkerIcon(l, marker);
                    }

                    Location l = glob.vehNoLocationMap.get(vehNoMarkerMap
                            .get(PresentMarker.toString()));
                    LatLng point = new LatLng(l.getLatitude(), l.getLongitude());
//                    mBaiDuMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(point));

                    mInfoWindow = getInfoWindow(point);
                    updateInfoWindow(PresentMarker);
                    new MyAddressThread().start();
                    if (isInfoWindowShowing) {
                        mBaiDuMap.hideInfoWindow();
                        mBaiDuMap.showInfoWindow(mInfoWindow);
                    }
                    break;
                case 7:
                    if (isActivityAlive) {
                        new GetAllLocationThread().start();
                    }
                    break;
            }
            super.handleMessage(message);
        }
    };
    //endregion

    //region 获取所有兴趣点
    private Handler getMapPointHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SProtocol.SUCCESS: {
                    mapPointList = (List<MapPoint>) msg.obj;
                    for (MapPoint point : mapPointList) {
                        MarkerOptions marker = new MarkerOptions().title(point.getMpName())
                                .icon(mapPointIcon).anchor(0.5f, 0.5f)
                                .position(new LatLng(point.getLatitude(), point.getLongitude()));

                        mBaiDuMap.addOverlay(marker);
                    }
                    break;
                }
                case SProtocol.FAIL: {
                    break;
                }
                case SProtocol.CONNECT_FAIL: {
                    break;
                }
            }
        }
    };
    //endregion

    //region 地址解析
    private Handler getAddressHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    MaxQueryTime = 0;
                    if (PresentMarker != null) {
                        updateInfoWindow(PresentMarker);
                        if (isInfoWindowShowing) {
                            mBaiDuMap.hideInfoWindow();
                        }
                        mBaiDuMap.showInfoWindow(mInfoWindow);
                    }
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

    //region 获取服务器时间
    private Handler getServerTimeHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        date = dateFormat.parse(msg.getData().getString("serverTime"));
                        serverCalendar.setTime(date);

                        if (isFirst) {
                            // 添加marker
                            addMarkersToMap();
                            // 设置终点坐标
                            Location l = glob.vehNoLocationMap.get(vehNoMarkerMap.get(PresentMarker.toString()));
                            end_point = new LatLng(l.getLatitude(), l.getLongitude());
                            mBaiDuMap.animateMapStatus(MapStatusUpdateFactory.newLatLngBounds(bounds.build()));
//                            mBaiDuMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(end_point));

                            if (PresentMarker != null) {
                                PresentMarker.setRotate(0);
                                PresentMarker.setAnchor(0.5f, 0.5f);
                                setMarkerIcon(l, PresentMarker);
                            }

                            mInfoWindow = getInfoWindow(end_point);
                            updateInfoWindow(PresentMarker);
                            new MyAddressThread().start();
                            if (isInfoWindowShowing) {
                                mBaiDuMap.hideInfoWindow();
                            }
                            mBaiDuMap.showInfoWindow(mInfoWindow);
                            new GetAllLocationThread().start();
                            isFirst = false;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1: {
                    new AlertDialog.Builder(AllLocationBaidu.this)
                            .setTitle(R.string.alert_title)
                            .setMessage(R.string.data_get_fail)
                            .setNegativeButton(R.string.btn_title_cancel, null)
                            .setPositiveButton(R.string.btn_title_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.setMessage(getResources().getString(
                                            R.string.get_vehicle_info));
                                    progressDialog.show();
                                    new GetServerTime().start();
                                }
                            })
                            .show();
                    break;
                }
                case -1: {
                    new AlertDialog.Builder(AllLocationBaidu.this)
                            .setTitle(R.string.alert_title)
                            .setMessage(R.string.data_get_fail)
                            .setNegativeButton(R.string.btn_title_cancel, null)
                            .setPositiveButton(R.string.btn_title_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.setMessage(getResources().getString(
                                            R.string.get_vehicle_info));
                                    progressDialog.show();
                                    new GetServerTime().start();
                                }
                            })
                            .show();
                }
            }
            progressDialog.dismiss();
        }

    };
    //endregion

    //region 获取服务到期提醒列表
    private Handler getServiceExpiredListHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SProtocol.SUCCESS: {
                    List<ServiceExpired> expireds = (List<ServiceExpired>) msg.obj;
                    if (expireds != null && expireds.size() > 0)
                        showPopupDialog(expireds);
                    break;
                }
                case SProtocol.FAIL: {
                    break;
                }
                case SProtocol.CONNECT_FAIL: {
                    break;
                }
            }
        }

        private void showPopupDialog(List<ServiceExpired> expireds) {
            Popup popup = new Popup();
            int xPos = AppUtils.getScreenWidth(AllLocationBaidu.this) / 2;
            int yPos = AppUtils.getScreenHeight(AllLocationBaidu.this) / 2;
            popup.setxPos(xPos);
            popup.setyPos(yPos);
            popup.setvWidth((int) (xPos * 1.5));
            popup.setvHeight((int) (yPos * 1.2));
            popup.setIsClickable(true);
            popup.setAnimFadeInOut(R.style.AnimationFade);
            popup.setContentView(R.layout.expired_list);
            popup.setListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(1f);
                }
            });

            popupDialog = PopupUtils.createPopupDialog(AllLocationBaidu.this, popup);
            popupDialog.setOutsideTouchable(true);
            popupDialog.setFocusable(true);

            View view = popupDialog.getContentView();
            view.setBackground(getResources().getDrawable(R.drawable.white_circular_back));
            ListView listView = (ListView) view.findViewById(R.id.expired_car_list);
            ExpiredListAdapter adapter = new ExpiredListAdapter(AllLocationBaidu.this, expireds);
            listView.setAdapter(adapter);

            popupDialog.showAtLocation(findViewById(R.id.main_layout), Gravity.BOTTOM, 0, yPos / 2);
            backgroundAlpha(0.5f);
        }

        private void backgroundAlpha(float bgAlpha) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = bgAlpha; //0.0-1.0
            getWindow().setAttributes(lp);
        }
    };
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.alllocation_baidu);
        glob = (SWApplication) getApplicationContext();
        glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        int refreshTime = glob.sp.getInt("refreshTime", 10);
        downTimer = new CountDownTimer(refreshTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                new GetServerTime().start();
                new GetAllLocationThread().start();
                downTimer.start();
            }
        };
        //设置默认值
        Address = getResources().getString(R.string.Location_address_fail);
        mapPointIcon = BitmapDescriptorFactory.fromResource(R.drawable.pin_green);
        progressDialog = new CustomProgressDialog(AllLocationBaidu.this);

        mapView = (MapView) this.findViewById(R.id.baidumap);
        mBaiDuMap = mapView.getMap();
        bounds = new LatLngBounds.Builder();

		/* 找弹出窗 */
        infoWindowView = (ViewGroup) getLayoutInflater().inflate(
                R.layout.alllocation_markerinforwindow, null);
        infoWindowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setClass(AllLocationBaidu.this, Dizhijiexi.class);
                startActivity(intent);
            }
        });

        // 找各个标签
        infoWindow_vehnof = (TextView) infoWindowView.findViewById(R.id.inforwindow_vehnof);
        infoWindow_time = (TextView) infoWindowView.findViewById(R.id.inforwindow_time);
        infoWindow_state = (TextView) infoWindowView.findViewById(R.id.inforwindow_state);
        infoWindow_acc = (TextView) infoWindowView.findViewById(R.id.inforwindow_acc);
        infoWindow_speed = (TextView) infoWindowView.findViewById(R.id.inforwindow_speed);
        infoWindow_fuel = (TextView) infoWindowView.findViewById(R.id.inforwindow_fuel);
        infoWindow_today_mileage = (TextView) infoWindowView.findViewById(R.id.inforwindow_today_mileage);
        infoWindow_CarLocation = (TextView) infoWindowView.findViewById(R.id.inforwindow_carlocation);

        infoWindow_close = (ImageView) infoWindowView.findViewById(R.id.inforwindow_close);
        infoWindow_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaiDuMap.hideInfoWindow();
                isInfoWindowShowing = false;
            }
        });

        popupLyaout = getLayoutInflater().inflate(R.layout.popup_layout, null);
        markerTitle = (TextView) popupLyaout.findViewById(R.id.marker_title);

        trackBtn = (TextView) infoWindowView.findViewById(R.id.track_btn);
        trackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                if (glob.mapType == 0) {
                    if (IsGooglePlayAvailable()) {
                        intent.setClass(AllLocationBaidu.this, MonitorLocation.class);
                    }
                }
                if (glob.mapType == 1) {
                    intent.setClass(AllLocationBaidu.this, MonitorLocationBaidu.class);
                }
                */
                intent.setClass(AllLocationBaidu.this, MonitorLocationBaidu.class);
                startActivity(intent);
            }
        });
        orbitReplyBtn = (TextView) infoWindowView.findViewById(R.id.orbit_reply_btn);
        orbitReplyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.setClass(AllLocationBaidu.this, SelectDate.class);
                startActivity(intent);
            }
        });


        //region 切换车辆左箭头
        nextCar_leftBtn = (ImageButton) this
                .findViewById(R.id.alllocation_nextcarleft);
        nextCar_leftBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Index--;
                if (Index < 0) {
                    Index = markerList.size() - 1;
                }

                PresentMarker = markerList.get(Index);
                glob.curVehicle = MarkerVehMap.get(PresentMarker);
                glob.curVLocation = glob.vehNoLocationMap
                        .get(vehNoMarkerMap.get(PresentMarker.toString()));
                PresentMarker.setRotate(0);
                PresentMarker.setAnchor(0.5f, 1f);
                setMarkerIcon(glob.curVLocation, PresentMarker);

                Location l = glob.vehNoLocationMap.get(vehNoMarkerMap
                        .get(PresentMarker.toString()));
                end_point = new LatLng(l.getLatitude(), l.getLongitude());

                LatLng point = new LatLng(l.getLatitude(), l.getLongitude());
                mBaiDuMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(point));
                mInfoWindow = getInfoWindow(point);
                updateInfoWindow(PresentMarker);
                new MyAddressThread().start();
                // 显示InfoWindow
                if (isInfoWindowShowing) {
                    mBaiDuMap.hideInfoWindow();
                }
                mBaiDuMap.showInfoWindow(mInfoWindow);
                isInfoWindowShowing = true;
            }

        });
        //endregion

        //region 切换车辆右箭头
        nextCar_rightBtn = (ImageButton) this
                .findViewById(R.id.alllocation_nextcarright);
        nextCar_rightBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Index++;
                if (Index >= markerList.size()) {
                    Index = 0;
                }

                PresentMarker = markerList.get(Index);
                glob.curVehicle = MarkerVehMap.get(PresentMarker);
                glob.curVLocation = glob.vehNoLocationMap
                        .get(vehNoMarkerMap.get(PresentMarker.toString()));

                PresentMarker.setRotate(0);
                setMarkerIcon(glob.curVLocation, PresentMarker);

                Location l = glob.vehNoLocationMap.get(vehNoMarkerMap
                        .get(PresentMarker.toString()));
                end_point = new LatLng(l.getLatitude(), l.getLongitude());

                LatLng point = new LatLng(l.getLatitude(), l.getLongitude());
                mBaiDuMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(point));
                mInfoWindow = getInfoWindow(point);

                updateInfoWindow(PresentMarker);
                new MyAddressThread().start();
                // 显示InfoWindow
                if (isInfoWindowShowing) {
                    mBaiDuMap.hideInfoWindow();
                }
                mBaiDuMap.showInfoWindow(mInfoWindow);
                isInfoWindowShowing = true;
            }
        });
        //endregion

        //region 地图标注点击事件
        mBaiDuMap.setOnMarkerClickListener(new OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                if (markerList.contains(marker)) {
                    if (marker != PresentMarker) {
                        PresentMarker = marker;
                        glob.curVehicle = MarkerVehMap.get(marker);
                        glob.curVLocation = glob.vehNoLocationMap
                                .get(vehNoMarkerMap.get(marker.toString()));

                        PresentMarker.setRotate(0);
                        PresentMarker.setAnchor(0.5f, 1f);
                        setMarkerIcon(glob.curVLocation, PresentMarker);

                        Location l = glob.vehNoLocationMap.get(vehNoMarkerMap
                                .get(marker.toString()));
                        end_point = new LatLng(l.getLatitude(), l.getLongitude());
                    }

                    Location l = glob.vehNoLocationMap.get(vehNoMarkerMap
                            .get(marker.toString()));
                    LatLng point = new LatLng(l.getLatitude(), l.getLongitude());
                    mBaiDuMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(point));
                    mInfoWindow = getInfoWindow(point);
                    updateInfoWindow(marker);
                    new MyAddressThread().start();
                    // 显示InfoWindow
                    if (isInfoWindowShowing) {
                        mBaiDuMap.hideInfoWindow();
                    }
                    mBaiDuMap.showInfoWindow(mInfoWindow);
                    isInfoWindowShowing = true;
                } else {
                    mInfoWindow = new InfoWindow(popupLyaout, marker.getPosition(), 0);
                    markerTitle.setText(marker.getTitle());
                    mBaiDuMap.showInfoWindow(mInfoWindow);
                    isInfoWindowShowing = true;
                }

                return false;
            }
        });
        //endregion

        mBaiDuMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                return true;
            }

            @Override
            public void onMapClick(LatLng arg0) {
                mBaiDuMap.hideInfoWindow();
                isInfoWindowShowing = false;
                return;
            }
        });
        /* 返回 */
        ImageButtonBack = (ImageButton) findViewById(R.id.alllocation_back);
        ImageButtonBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layerButton = (ImageButton) findViewById(R.id.layerButton);
        layerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapType == 0) {
                    mBaiDuMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    layerButton.setImageResource(R.drawable.nav_more_map_press);
                    mapType = 1;
                    return;
                }

                if (mapType == 1) {
                    mBaiDuMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    layerButton.setImageResource(R.drawable.nav_more_map_normal);
                    mapType = 0;
                    return;
                }
            }
        });

		/* 监控 */
        monitorButton = (Button) findViewById(R.id.monitorButton);
        monitorButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllLocationBaidu.this, VehicleList.class);
                startActivity(intent);
            }
        });

        progressDialog.setMessage(getString(R.string.refreshing));
        progressDialog.show();
        new GetServerTime().start();
        new GetMapPoint().start();
        new GetServiceExpiredList().start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        /* 遍历车队车辆画标注 */
        for (int i = 0; i < glob.vehicleListData.size(); i++) {
            for (int j = 0; j < glob.vehicleListData.get(i).size(); j++) {
                Vehicle v = glob.vehicleListData.get(i).get(j);
                Location l = glob.vehNoLocationMap.get(v.getVehNoF());
                /* 判断是否位置 */
                if (l != null) {
                    // 定义Maker坐标点
                    LatLng point = new LatLng(l.getLatitude(), l.getLongitude());
                    bounds = bounds.include(point);

                    Marker marker = (Marker) mBaiDuMap
                            .addOverlay(new MarkerOptions().position(point)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.vehicle0))
                                    .anchor(0.5f, 0.5f));
                    setMarkerIcon(l, marker);

                    if (glob.curVehicle == null) {
                        if (i == 0 && j == 0) {
                            PresentMarker = marker;
                            glob.curVehicle = v;
                            glob.curVLocation = l;
                        }
                    } else {
                        if (glob.curVehicle.getVehNoF().compareTo(v.getVehNoF()) == 0) {
                            PresentMarker = marker;
                            glob.curVehicle = v;
                            glob.curVLocation = l;
                        }
                    }

                    vehNoMarkerMap.put(marker.toString(), v.getVehNoF());
                    MarkerVehMap.put(marker, v);
                    markerList.add(marker);
                }
            }
        }
    }

    public void setMarkerIcon(Location location, Marker marker) {
        try {
            /*
            date = dateFormat.parse(location.getTime());
            calendar.setTime(date);
            if (isonline(serverCalendar, calendar)) {
                if (null == location.getAlarmmsg() || location.getAlarmmsg().length() <= 0) {
                    if (location.getDtStatus() == 4) {
                        marker.setTitle("unlocation");
                        marker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("d_", location.getAngle())));
                    } else {
                        if (location.getVelocity() < 2) {
                            marker.setTitle("static");
                            marker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("j_", location.getAngle())));
                        } else {
                            marker.setTitle("online");
                            marker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("", location.getAngle())));
                        }
                    }
                } else {
                    marker.setTitle("alarm");
                    marker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("c_", location.getAngle())));
                }
            } else {
                marker.setTitle("offline");
                marker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("l_", location.getAngle())));
            }
            */
            int resourceId = getDrawableIdWithAngle("", location.getAngle());
            if (location.getVehStatus() == 1) {
                marker.setTitle("online");
//                marker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("", location.getAngle())));
                resourceId = getDrawableIdWithAngle("", location.getAngle());
            }
            if (location.getVehStatus() == 2) {
                marker.setTitle("static");
//                marker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("j_", location.getAngle())));
                resourceId = getDrawableIdWithAngle("j_", location.getAngle());
            }
            if (location.getVehStatus() == 3) {
                marker.setTitle("offline");
//                marker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("l_", location.getAngle())));
                resourceId = getDrawableIdWithAngle("l_", location.getAngle());
            }
            if (location.getVehStatus() == 5) {
                marker.setTitle("unlocation");
//                marker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("d_", location.getAngle())));
                resourceId = getDrawableIdWithAngle("d_", location.getAngle());
            }

            View view = getLayoutInflater().inflate(R.layout.custom_marker, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.marker_img);
            TextView textView = (TextView) view.findViewById(R.id.marker_carNo);
            imageView.setImageResource(resourceId);
            textView.setText(location.getVehNoF());

            marker.setIcon(BitmapDescriptorFactory.fromView(view));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InfoWindow getInfoWindow(LatLng point) {
        if (mInfoWindow != null)
            mBaiDuMap.hideInfoWindow();
        return new InfoWindow(infoWindowView, point, -35);
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

        Class<R.drawable> drawableClass = R.drawable.class;
        try {
            return drawableClass.getDeclaredField(drawableName).getInt(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return R.drawable.vehicle0;
    }

    @Override
    protected void onResume() {
        if (glob.curVehicle != null) {
            Iterator<Marker> iterator = MarkerVehMap.keySet().iterator();
            while (iterator.hasNext()) {
                Marker marker = iterator.next();
                if (MarkerVehMap.get(marker).getVehNoF().compareTo(glob.curVehicle.getVehNoF()) == 0) {
                    PresentMarker = marker;
                    break;
                }
            }

            if (PresentMarker != null) {
                glob.curVehicle = MarkerVehMap.get(PresentMarker);
                glob.curVLocation = glob.vehNoLocationMap
                        .get(vehNoMarkerMap.get(PresentMarker.toString()));

                PresentMarker.setRotate(0);
                PresentMarker.setAnchor(0.5f, 1f);
                setMarkerIcon(glob.curVLocation, PresentMarker);

                Location l = glob.vehNoLocationMap.get(vehNoMarkerMap
                        .get(PresentMarker.toString()));
                end_point = new LatLng(l.getLatitude(), l.getLongitude());

                LatLng point = new LatLng(l.getLatitude(), l.getLongitude());
                mBaiDuMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(point));
                mInfoWindow = getInfoWindow(point);
                updateInfoWindow(PresentMarker);
                new MyAddressThread().start();
                // 显示InfoWindow
                if (isInfoWindowShowing) {
                    mBaiDuMap.hideInfoWindow();
                }
                mBaiDuMap.showInfoWindow(mInfoWindow);
                isInfoWindowShowing = true;
            }
        }

        downTimer.start();
        isActivityAlive = true;
        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        isActivityAlive = false;
        downTimer.cancel();
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
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

    public void updateInfoWindow(Marker marker) {
        Location l = glob.vehNoLocationMap.get(vehNoMarkerMap.get(marker.toString()));

        infoWindow_vehnof.setText(getString(R.string.car_number) + vehNoMarkerMap.get(marker.toString()));
        infoWindow_time.setText(getString(R.string.car_time) + l.getTime());
        String state = getString(R.string.car_state);
        if (l.getVehStatus() == 1)
            state += getString(R.string.runing);
        if (l.getVehStatus() == 2)
            state += getString(R.string.stoping);
        if (l.getVehStatus() == 3)
            state += getString(R.string.vehicle_offline);
        if (l.getVehStatus() == 5)
            state += getString(R.string.no_location);
        infoWindow_state.setText(state);

        String acc = "ACC:";
        if (l.getDtStatus() > 0)
            acc += getString(R.string.open);
        else acc += getString(R.string.off);
        infoWindow_acc.setText(acc);

        infoWindow_speed.setText(getString(R.string.speed) + l.getVelocity() + "km/h");
//        infoWindow_fuel.setText(getString(R.string.oils) + l.getOil() + "L");
        infoWindow_fuel.setText(String.format(getString(R.string.oils), l.getOil()));
        infoWindow_today_mileage.setText(String.format(getString(R.string.car_today_mileage), l.getTodayMile()));
//        infoWindow_today_mileage.setText(getString(R.string.car_today_mileage) + l.getTodayMile() + "km");
        infoWindow_CarLocation.setText(getString(R.string.car_location) + Address);
    }

    /**
     * 获取本账户下所有位置
     */
    class GetAllLocationThread extends Thread {
        public void run() {
            Bundle b = new Bundle();
            Message message = new Message();
            SResponse response = HttpRequestClient.getVehicleLocation("", "1",
                    glob.sp.getString("user", ""), glob.sp.getString("psw", ""));
            if (response.getCode() == SProtocol.SUCCESS) {
                b.putBoolean("ret", true);
                List<Location> locations = (List<Location>) response
                        .getResult();
                /* 先清空 */
                glob.vehNoLocationMap.clear();
                for (Location l : locations) {
                    glob.vehNoLocationMap.put(
                            glob.systemNoVehNoMap.get(l.getSystemNo()), l);
                }

                glob.curVLocation = glob.vehNoLocationMap.get(vehNoMarkerMap
                        .get(PresentMarker.toString()));
                message.setData(b);
                message.what = 6;
            } else {
                b.putBoolean("ret", false);
                b.putString(
                        "result",
                        SProtocol.getFailMessage(response.getCode(),
                                response.getMessage()));
                message.setData(b);
                message.what = 7;
            }
            handler.sendMessage(message);
        }
    }

    /**
     * 获取兴趣点
     */
    class GetMapPoint extends Thread {
        @Override
        public void run() {
            super.run();
            Message msg = new Message();
            try {
                SResponse response = HttpRequestClient.getMapPoint();
                msg.what = response.getCode();
                if (response.getCode() == SProtocol.SUCCESS) {
                    msg.obj = response.getResult();
                }
            } catch (Exception e) {
                msg.what = SProtocol.CONNECT_FAIL;
            }
            getMapPointHandler.sendMessage(msg);
        }
    }

    /**
     * 获取地址
     */
    class MyAddressThread extends Thread {
        public void run() {
            Message message = new Message();
            String address;
            Location l = glob.curVLocation;
            SResponse response = HttpRequestClient.addressTranslate(l.getLongitude(), l.getLatitude());
            if (response.getCode() == SProtocol.SUCCESS) {
                address = (String) response.getResult();
                message.what = 0;
            } else {
                address = SProtocol.getFailMessage(response.getCode(),
                        response.getMessage());
                message.what = 1;
            }
            Address = address;
            getAddressHandler.sendMessage(message);
        }
    }

    /**
     * 获取当前服务器时间
     */
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
     * 获取过期设备列表
     */
    class GetServiceExpiredList extends Thread {
        @Override
        public void run() {
            Message message = new Message();
            try {
                SResponse response = HttpRequestClient.getServiceExpiredList(
                        glob.sp.getString("user", ""), glob.sp.getString("psw", ""));
                message.what = response.getCode();
                if (response.getCode() == SProtocol.SUCCESS) {
                    message.obj = response.getResult();
                }

            } catch (Exception e) {
                message.what = SProtocol.CONNECT_FAIL;
            }
            getServiceExpiredListHandler.sendMessage(message);
        }
    }
}
