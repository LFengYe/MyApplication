package com.cn.wetrack.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cn.wetrack.R;
import com.cn.wetrack.entity.Location;
import com.cn.wetrack.entity.MapPoint;
import com.cn.wetrack.entity.SResponse;
import com.cn.wetrack.entity.ServiceExpired;
import com.cn.wetrack.entity.Vehicle;
import com.cn.wetrack.util.AppUtils;
import com.cn.wetrack.util.HttpRequestClient;
import com.cn.wetrack.util.SProtocol;
import com.cn.wetrack.widgets.CustomProgressDialog;
import com.cn.wetrack.widgets.Popup;
import com.cn.wetrack.widgets.PopupDialog;
import com.cn.wetrack.widgets.PopupUtils;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
public class AllLocation extends FragmentActivity {
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000) // 5 seconds
            .setFastestInterval(16) // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    private SWApplication glob;
    private ImageButton ImageButtonBack;// 返回
    private ImageButton layerButton;
    private Button monitorButton;// 监控
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Map<String, String> vehNoMarkerMap = new HashMap<>();// 车牌号标注哈希表
    private View mWindow;
    private TextView infoWindow_vehNof;
    private TextView infoWindow_time;
    private TextView infoWindow_state;
    private TextView infoWindow_acc;
    private TextView infoWindow_speed;
    private TextView infoWindow_fuel;
    private TextView infoWindow_today_mileage;
    private TextView infoWindow_carLocation;

    private Calendar calendar = Calendar.getInstance();// 接收的时间
    private Calendar serverCalendar = Calendar.getInstance();//服务器时间
    private Date date = null;// 中间变量
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 字符转换
    private Intent intent = new Intent();
    private Marker PresentMarker = null;// 标记marker
    private LatLng end_point;// 驾车检索起始点，终止点
    private ArrayList<Marker> markerList = new ArrayList<>();// 车子标记物集合
    private HashMap<Marker, Vehicle> markerVehicleMap = new HashMap<>();// 车子标记物哈希表
    private Boolean isFirst = true;
    private String Address = "";
    private CustomProgressDialog progressDialog = null;// 提示消息
    // 向左箭头，向右箭头
    private ImageButton leftBtn, rightBtn;
    private int Index = 0;
    private int MaxQueryTime = 0;//解析位置的最大次数
    private CountDownTimer downTimer;

    private BitmapDescriptor mapPointIcon;
    private List<MapPoint> mapPointList;

    private LatLngBounds.Builder bounds;
    private PopupDialog popupDialog;

    private int mapType = 0;

    //region 获取所有车辆位置信息返回处理
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 3:
                    if (!isFirst) {
                        Bundle b = message.getData();
                        if (b.getBoolean("ret")) {
                            for (Marker marker : markerList) {
                                Location l = glob.vehNoLocationMap.get(vehNoMarkerMap
                                        .get(marker.toString()));
                                marker.setPosition(new LatLng(l.getLatitude(), l
                                        .getLongitude()));
                            }

                            LatLng point = new LatLng(glob.vehNoLocationMap.get(
                                    glob.curVehicle.getVehNoF()).getLatitude(),
                                    glob.vehNoLocationMap.get(glob.curVehicle.getVehNoF())
                                            .getLongitude());
//                            mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                            if (PresentMarker != null)
                                PresentMarker.showInfoWindow();
                            new GetAddressThread().start();
                        }
                    } else {
                        isFirst = false;
                    }
                    break;
                case 4:
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
                        mMap.addMarker(marker);
//                        mBaiDuMap.addOverlay(marker);
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
    private Handler updateAddressHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    MaxQueryTime = 0;
                    if (PresentMarker != null)
                        PresentMarker.showInfoWindow();
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

    //region 获取服务器时间
    private Handler getServerTimeHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        System.out.println("serverTime:" + msg.getData().getString("serverTime"));
                        date = dateFormat.parse(msg.getData().getString("serverTime"));
                        serverCalendar.setTime(date);

                        // 添加marker
                        addMarkersToMap();

                        // 构建MarkerOption，用于在地图上添加Marker
                        if (PresentMarker != null) {
                            PresentMarker.setRotation(0);
                            PresentMarker.setAnchor(0.5f, 1f);
                            PresentMarker.setInfoWindowAnchor(0.5f, 0.5f);
                            // 设置终点坐标
                            Location l = glob.vehNoLocationMap.get(vehNoMarkerMap.get(PresentMarker.toString()));
                            end_point = new LatLng(l.getLatitude(), l.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 10));
                            PresentMarker.showInfoWindow();
                        }

                        new GetAddressThread().start();
                        new GetAllLocationThread().start();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1: {
                    new AlertDialog.Builder(AllLocation.this)
                            .setTitle(R.string.alert_title)
                            .setMessage(R.string.data_get_fail)
                            .setNegativeButton(R.string.btn_title_cancel, null)
                            .setPositiveButton(R.string.btn_title_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.setMessage(getResources().getString(
                                            R.string.refreshing));
                                    progressDialog.show();
                                    new GetServerTime().start();
                                }
                            })
                            .show();
                    break;
                }
                case -1: {
                    new AlertDialog.Builder(AllLocation.this)
                            .setTitle(R.string.alert_title)
                            .setMessage(R.string.data_get_fail)
                            .setNegativeButton(R.string.btn_title_cancel, null)
                            .setPositiveButton(R.string.btn_title_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.setMessage(getResources().getString(
                                            R.string.refreshing));
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
            int xPos = AppUtils.getScreenWidth(AllLocation.this) / 2;
            int yPos = AppUtils.getScreenHeight(AllLocation.this) / 2;
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

            /*
            View view = LayoutInflater.from(AllLocation.this).inflate(R.layout.expired_list, null);
            view.setBackground(getResources().getDrawable(R.drawable.white_circular_back));
            ListView listView = (ListView) view.findViewById(R.id.expired_car_list);
            ExpiredListAdapter adapter = new ExpiredListAdapter(AllLocation.this, expireds);
            listView.setAdapter(adapter);
            */

            popupDialog = PopupUtils.createPopupDialog(AllLocation.this, popup);
            popupDialog.setOutsideTouchable(true);
            popupDialog.setFocusable(true);

            View view = popupDialog.getContentView();
            view.setBackground(getResources().getDrawable(R.drawable.white_circular_back));
            ListView listView = (ListView) view.findViewById(R.id.expired_car_list);
            ExpiredListAdapter adapter = new ExpiredListAdapter(AllLocation.this, expireds);
            listView.setAdapter(adapter);

            popupDialog.showAtLocation(findViewById(R.id.titleText), Gravity.BOTTOM, 0, yPos / 2);
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
        setContentView(R.layout.alllocation);

        glob = (SWApplication) getApplicationContext();
        glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);

        int refreshTime = glob.sp.getInt("refreshTime", 10);
        downTimer = new CountDownTimer(refreshTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                new GetAllLocationThread().start();
                downTimer.start();
            }
        };
        //设置默认值
        Address = getResources().getString(R.string.Location_address_fail);
        mapPointIcon = BitmapDescriptorFactory.fromResource(R.drawable.pin_green);
        progressDialog = new CustomProgressDialog(AllLocation.this);
        bounds = new LatLngBounds.Builder();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.alllocation_googlemap);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                mWindow = getLayoutInflater().inflate(R.layout.alllocation_markerinforwindow, null);
                mWindow.findViewById(R.id.inforwindow_close).setVisibility(View.GONE);
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
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        intent.setClass(AllLocation.this, Dizhijiexi.class);
                        startActivity(intent);
                    }
                });
                //marker点击事件
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (markerList.contains(marker)) {
                            if (!marker.equals(PresentMarker)) {
                                PresentMarker = marker;
                                glob.curVehicle = markerVehicleMap.get(marker);
                                glob.curVLocation = glob.vehNoLocationMap
                                        .get(vehNoMarkerMap.get(marker.toString()));

                                end_point = new LatLng(glob.curVLocation.getLatitude(),
                                        glob.curVLocation.getLongitude());
                                PresentMarker.setRotation(0);
                                PresentMarker.setAnchor(0.5f, 1f);
                                PresentMarker.setInfoWindowAnchor(0.5f, 0.5f);
                            }
                            // 设置终点坐标
                            Location l = glob.vehNoLocationMap.get(vehNoMarkerMap.get(PresentMarker.toString()));
                            end_point = new LatLng(l.getLatitude(), l.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(end_point));
                            PresentMarker.showInfoWindow();
                            new GetAddressThread().start();
                        } else {
                            System.out.println("mark Title:" + marker.getTitle());
                        }
                        return false;
                    }
                });

                progressDialog.setMessage(getString(R.string.refreshing));
                progressDialog.show();
                new GetServerTime().start();
                new GetMapPoint().start();
            }
        });

        // 切换车辆左箭头
        leftBtn = (ImageButton) this
                .findViewById(R.id.alllocation_nextcarleft);
        leftBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Index--;
                if (Index < 0) {
                    Index = markerList.size() - 1;
                }
                change();
            }
        });

        // 切换车辆右箭头
        rightBtn = (ImageButton) this
                .findViewById(R.id.alllocation_nextcarright);
        rightBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Index++;
                if (Index >= markerList.size()) {
                    Index = 0;
                }
                change();
            }
        });


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
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    layerButton.setImageResource(R.drawable.nav_more_map_press);
                    mapType = 1;
                    return;
                }

                if (mapType == 1) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    layerButton.setImageResource(R.drawable.nav_more_map_normal);
                    mapType = 0;
                    return;
                }
            }
        });

        monitorButton = (Button) findViewById(R.id.monitorButton);
        monitorButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllLocation.this, VehicleList.class);
                startActivity(intent);
            }
        });


        new GetServiceExpiredList().start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
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
                    LatLng point = new LatLng(l.getLatitude(),
                            l.getLongitude());
                    bounds = bounds.include(point);
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("", 0))).position(point)
                            .title(v.getVehNoF()).snippet("")
                            .anchor(0.5f, 0.5f).infoWindowAnchor(0f, 0f));

                    try {
                        int resourceId = getDrawableIdWithAngle("", 0);
                        if (l.getVehStatus() == 1) {
                            marker.setTitle("online");
//                            marker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("", l.getAngle())));
                            resourceId = getDrawableIdWithAngle("", l.getAngle());
                        }
                        if (l.getVehStatus() == 2) {
                            marker.setTitle("static");
//                            marker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("j_", l.getAngle())));
                            resourceId = getDrawableIdWithAngle("j_", l.getAngle());
                        }
                        if (l.getVehStatus() == 3) {
                            marker.setTitle("offline");
//                            marker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("l_", l.getAngle())));
                            resourceId = getDrawableIdWithAngle("l_", l.getAngle());
                        }
                        if (l.getVehStatus() == 5) {
                            marker.setTitle("unlocation");
//                            marker.setIcon(BitmapDescriptorFactory.fromResource(getDrawableIdWithAngle("d_", l.getAngle())));
                            resourceId = getDrawableIdWithAngle("d_", l.getAngle());
                        }

                        View view = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
                        ImageView imageView = (ImageView) view.findViewById(R.id.marker_img);
                        TextView textView = (TextView) view.findViewById(R.id.marker_carNo);
                        imageView.setImageResource(resourceId);
                        textView.setText(l.getVehNoF());

                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, view)));
//                        System.out.println("设置ICON成功, resourceId:" + resourceId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (i == 0 && j == 0) {
                        PresentMarker = marker;
                        glob.curVehicle = v;
                        glob.curVLocation = l;
                    }

                    vehNoMarkerMap.put(marker.toString(), v.getVehNoF());
                    markerVehicleMap.put(marker, v);
                    markerList.add(marker);
                }
            }
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

    // Convert a view to bitmap
    public Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        view.measure(view.getMeasuredWidth(), view.getMeasuredHeight());
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (glob.curVehicle != null) {
            Iterator<Marker> iterator = markerVehicleMap.keySet().iterator();
            while (iterator.hasNext()) {
                Marker marker = iterator.next();
                if (markerVehicleMap.get(marker).getVehNoF().compareTo(glob.curVehicle.getVehNoF()) == 0) {
                    PresentMarker = marker;
                    break;
                }
            }

            if (PresentMarker != null) {
                glob.curVehicle = markerVehicleMap.get(PresentMarker);
                glob.curVLocation = glob.vehNoLocationMap
                        .get(vehNoMarkerMap.get(PresentMarker.toString()));

                PresentMarker.setRotation(0);
                PresentMarker.setAnchor(0.5f, 1f);
                PresentMarker.setInfoWindowAnchor(0.5f, 0.5f);

                end_point = new LatLng(glob.curVLocation.getLatitude(),
                        glob.curVLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(end_point));
                PresentMarker.showInfoWindow();
                new GetAddressThread().start();
            }
        }

        downTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        downTimer.cancel();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void change() {
        if (PresentMarker.isInfoWindowShown()) {
            PresentMarker.hideInfoWindow();
        }

        PresentMarker = markerList.get(Index);
        glob.curVehicle = markerVehicleMap.get(PresentMarker);
        glob.curVLocation = glob.vehNoLocationMap.get(vehNoMarkerMap
                .get(PresentMarker.toString()));

        PresentMarker.setRotation(0);
        PresentMarker.setAnchor(0.5f, 1f);
        PresentMarker.setInfoWindowAnchor(0.5f, 0.5f);

        Location l = glob.vehNoLocationMap.get(vehNoMarkerMap
                .get(PresentMarker.toString()));
        end_point = new LatLng(l.getLatitude(), l.getLongitude());

        LatLng point = new LatLng(l.getLatitude(), l.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));


        //更新infoWindow
        new GetAddressThread().start();
        // 显示InfoWindow
        PresentMarker.showInfoWindow();
    }

    /*
     * 自定义弹出窗 *
     */
    public class AllLocationInfoWindowAdapter implements InfoWindowAdapter {

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            if (markerList.contains(marker)) {
                render(marker);
                return mWindow;
            } else {
                return null;
            }
        }

        private void render(Marker marker) {
            if (markerList.contains(marker)) {
                Location l = glob.vehNoLocationMap.get(vehNoMarkerMap.get(marker.toString()));
//                Address = getString(R.string.Alarm_address_getting);

                infoWindow_vehNof.setText(getString(R.string.car_number) + vehNoMarkerMap.get(marker.toString()));
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
                infoWindow_fuel.setText(String.format(getString(R.string.oils), l.getOil()));
                infoWindow_today_mileage.setText(String.format(getString(R.string.car_today_mileage), l.getTodayMile()));
                infoWindow_carLocation.setText(getString(R.string.car_location) + Address);
            }
        }

    }

    /**
     * 获取本账户下所位置
     */
    class GetAllLocationThread extends Thread {
        public void run() {
            Bundle b = new Bundle();
            if (!isFirst) {
                SResponse response = HttpRequestClient.getVehicleLocation("", "1",
                        glob.sp.getString("user", ""), glob.sp.getString("psw", ""));
                if (response.getCode() == SProtocol.SUCCESS) {
                    b.putBoolean("ret", true);
                    @SuppressWarnings("unchecked")
                    List<Location> locations = (List<Location>) response
                            .getResult();
				/* 先清空 */
                    glob.TempvehNoLocationMap.clear();
                    for (Location l : locations) {
                        glob.TempvehNoLocationMap.put(
                                glob.systemNoVehNoMap.get(l.getSystemNo()), l);
                    }
                    glob.vehNoLocationMap.clear();
                    glob.vehNoLocationMap.putAll(glob.TempvehNoLocationMap);
                } else {
                    b.putBoolean("ret", false);
                    b.putString(
                            "result",
                            SProtocol.getFailMessage(response.getCode(),
                                    response.getMessage()));
                }
            }
            Message message = new Message();
            message.setData(b);
            message.what = 3;
            handler.sendMessage(message);
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

            updateAddressHandler.sendMessage(message);
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
