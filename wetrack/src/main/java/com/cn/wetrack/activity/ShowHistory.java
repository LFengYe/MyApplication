package com.cn.wetrack.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.cn.wetrack.R;
import com.cn.wetrack.entity.History;
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
import java.util.List;
import java.util.Timer;

/**
 * 历史轨迹
 */
public class ShowHistory extends FragmentActivity {
    private SWApplication glob;
    private SupportMapFragment mapfregment;
    private GoogleMap mMap;
    private ImageButton back;// 返回按钮
    private Button reverse, speed;// 快进，快退按钮
    private Button playspeed, playandpause;// 播放速度，播放暂停按钮
    private ImageButton layerbutton;// 图层按钮
    private List<LatLng> points = new ArrayList<LatLng>();// 停留点集合
    private List<LatLng> temppoints = new ArrayList<LatLng>();// 临时停留点集合
    private BitmapDescriptor startbit, endbit, currentbit;
    private SeekBar seekbar = null;//进度条
    private int Index = 0;//当前的索引值
    private Thread PlayThread = null;//播放线程
    private Timer Playhistory = null;
    private Marker currentmarker = null;//当前的标记
    private Boolean isplaying = false;//是否在播放
    private int PlaySpeed = 500;//播放速度
    private Intent intent = new Intent();
    private Boolean mapmode = true;//地图的状态，true表示地图模式，false表示卫星模式
    private Boolean IsFirstInActivity = true;
    private DisplayMetrics metric;//屏幕管理
    private int width, height;//屏幕宽度，高度
    private TextView firsttime, secondtime, timeandangle;
    private SimpleDateFormat dateformat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");// 字符转换
    private Calendar starcalendar = Calendar.getInstance();// 开始的时间
    private Calendar endcalendar = Calendar.getInstance();// 结束的时间
    private java.util.Date date = null;// 中间变量
    private Handler playhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (Index < (glob.historys.size() - 1)) {
                        seekbar.setProgress(Index);
                        Index++;
                        AddCurrentmarker();
                    } else {
                        currentmarker.remove();
                        currentmarker = null;
                        isplaying = false;
                        Index = 0;
                    }
                    if (isplaying) {
                        playhandler.postDelayed(PlayThread, PlaySpeed);
                    }
                    break;
                case 2:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*获取屏幕参数*/
        metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels; // 屏幕宽度（像素）
        height = metric.heightPixels; // 屏幕高度（像素）
        setContentView(R.layout.showhistory);
        glob = (SWApplication) getApplicationContext();
        glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        PlayThread = new PlayHistoryThread();

//		mMap = mapfregment.getMap();
        seekbar = (SeekBar) this.findViewById(R.id.showhistory_seekbar);
        seekbar.setMax(glob.historys.size() - 1);
        firsttime = (TextView) this.findViewById(R.id.showhistory_firsttime);
        secondtime = (TextView) this.findViewById(R.id.showhistory_secondtime);
        timeandangle = (TextView) this.findViewById(R.id.showhistory_timeandangle);
        timeandangle.setSingleLine(true);
        try {
            date = dateformat.parse(glob.historys.get(0).getTime());
            starcalendar.setTime(date);
            date = dateformat.parse(glob.historys.get(glob.historys.size() - 1).getTime());
            endcalendar.setTime(date);
            firsttime.setText("00:00");
            secondtime.setText(GetTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Index = seekbar.getProgress();
                AddCurrentmarker();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

            }
        });
        //获取位图
        startbit = BitmapDescriptorFactory
                .fromResource(R.drawable.line_start);
        endbit = BitmapDescriptorFactory
                .fromResource(R.drawable.line_end);
        currentbit = BitmapDescriptorFactory
                .fromResource(R.drawable.history_car);

        //添加点集
        for (History history : glob.historys) {
            LatLng point = new LatLng(history.getLatitude(),
                    history.getLongitude());
            points.add(point);
        }

        mapfregment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.showhistory_googlemap);
        mapfregment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                if (points.size() >= 2)
                    Addallline();
                AddStartmarker();
                AddEndmarker();
            }
        });
        // 返回
        back = (ImageButton) this.findViewById(R.id.showhistory_backRe);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 切换车辆
        playspeed = (Button) this.findViewById(R.id.showhistory_vehicle);
        playspeed.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                intent.setClass(ShowHistory.this, VehicleList.class);
                startActivity(intent);
                finish();

            }
        });
        // 图层按钮
        layerbutton = (ImageButton) this
                .findViewById(R.id.showhistory_layerButton);
        layerbutton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mapmode) {
                    layerbutton.setImageResource(R.drawable.nav_more_map_press);
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    mapmode = false;
                } else {
                    layerbutton.setImageResource(R.drawable.nav_more_map_normal);
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    mapmode = true;
                }
            }
        });
        // 播放按钮
        playandpause = (Button) this
                .findViewById(R.id.showhistory_playandpause);
        playandpause.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isplaying) {
                    playandpause.setBackgroundResource(R.drawable.showhistory_pause);
                    isplaying = true;
                    playhandler.post(PlayThread);
                } else {
                    playandpause.setBackgroundResource(R.drawable.showhistory_playandpause_small);
                    isplaying = false;
                }

            }
        });
        // 快退
        reverse = (Button) this.findViewById(R.id.showhistory_reversebtn);
        reverse.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (PlaySpeed < 1000) {
                    PlaySpeed = PlaySpeed + 100;
                }
            }
        });
        // 快进
        speed = (Button) this.findViewById(R.id.showhistory_speedbtn);
        speed.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (PlaySpeed > 100) {
                    PlaySpeed = PlaySpeed - 100;
                }


            }
        });

    }

    public void Addallline() {
        PolylineOptions ooPolyline = new PolylineOptions().width(5)
                .color(0xff00ff00).addAll(points);
        mMap.addPolyline(ooPolyline);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(points.get(0), 16));
//		mBaiDuMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom((points
//				.get(0)),13));
    }

    public void AddStartmarker() {
        mMap.addMarker(new MarkerOptions().position(points.get(0)).icon(
                startbit).anchor(0.5f, 0.5f));
//		mBaiDuMap.addOverlay(new MarkerOptions().position(points.get(0)).icon(
//				startbit));
    }

    public void AddCurrentmarker() {
        /**
         * 不必每次都移动镜头，只需要在marker移动到屏幕外面去了在移动镜头
         * */
        if (currentmarker == null) {
            currentmarker = mMap.addMarker(new MarkerOptions().position(points.get(Index)).icon(
                    currentbit).anchor(0.5f, 0.5f).rotation(Integer.parseInt(glob.historys.get(Index).getAngle())));
        } else {
            currentmarker.setPosition(points.get(Index));
            currentmarker.setAnchor(0.5f, 0.5f);
            currentmarker.setRotation(Integer.parseInt(glob.historys.get(Index).getAngle()));
        }

        String s = "";
        s += glob.historys.get(Index).getTime() + "  " +
                getResources().getString(R.string.speed) + glob.historys.get(Index).getVelocity() + "km/h" + "  " +
                getResources().getString(R.string.car_mileage) + ":" + glob.historys.get(Index).getMiles() + "km";
        timeandangle.setText(s);
        if (!IsInScreen(points.get(Index))) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(points.get(Index)));
        }
    }

    public void AddEndmarker() {
        mMap.addMarker(new MarkerOptions().position(
                points.get(glob.historys.size() - 1)).icon(endbit).anchor(0.5f, 0.5f));
    }

    /**
     * 返回鍵
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //返回时移除线程，销毁地图，解决导致立即定位界面点击maker无弹出窗问题
//			if(PlayThread!=null){
//			playhandler .removeCallbacks(PlayThread);
//			mapview.onDestroy();
//			PlayThread=null;
//			isplaying=false;
//			}
            finish();

        }
        return true;
    }

    protected void onDestroy() {
        isplaying = false;
        playhandler.removeCallbacks(PlayThread);
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        isplaying = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (IsFirstInActivity) {
            IsFirstInActivity = false;
        } else {
            isplaying = true;
            playhandler.post(PlayThread);

        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 判断marker是否在屏幕内
     */
    public Boolean IsInScreen(LatLng Mappoint) {
        Point point = mMap.getProjection().toScreenLocation(Mappoint);
        if (point.x < 0 || point.y < 0 || point.x > width || point.y > height) {
            return false;
        } else {
            return true;
        }
    }

    public String getOrientation(int orientation) {
        String orientation_zh = "";

        if (orientation == 0) {
            orientation_zh += getResources().getString(R.string.Direction_north);
        }
        if (orientation > 0 && orientation < 45) {
            orientation_zh += getResources().getString(R.string.Direction_northeastbynorth);
        }
        if (orientation == 45) {
            orientation_zh += getResources().getString(R.string.Direction_northeast);
        }
        if (orientation > 45 && orientation < 90) {
            orientation_zh += getResources().getString(R.string.Direction_northeastbyesat);
        }
        if (orientation == 90) {
            orientation_zh += getResources().getString(R.string.Direction_east);
        }
        if (orientation > 90 && orientation < 135) {
            orientation_zh += getResources().getString(R.string.Direction_southeastbyeast);
        }
        if (orientation == 135) {
            orientation_zh += getResources().getString(R.string.Direction_southeast);
        }
        if (orientation > 135 && orientation < 180) {
            orientation_zh += getResources().getString(R.string.Direction_southeastbysouth);
        }
        if (orientation == 180) {
            orientation_zh += getResources().getString(R.string.Direction_south);
        }
        if (orientation > 180 && orientation < 225) {
            orientation_zh += getResources().getString(R.string.Direction_southbysouthwest);
        }
        if (orientation == 225) {
            orientation_zh += getResources().getString(R.string.Direction_southwest);
        }
        if (orientation > 225 && orientation < 270) {
            orientation_zh += getResources().getString(R.string.Direction_southwestbywest);
        }
        if (orientation == 270) {
            orientation_zh += getResources().getString(R.string.Direction_west);
        }
        if (orientation > 270 && orientation < 315) {
            orientation_zh += getResources().getString(R.string.Direction_northwestbywest);
        }
        if (orientation == 315) {
            orientation_zh += getResources().getString(R.string.Direction_northwest);
        }
        if (orientation > 315 && orientation < 360) {
            orientation_zh += getResources().getString(R.string.Direction_northbynorthwest);
        }
        return orientation_zh;
    }

    public String GetTime() {
        String time = "";
        long difference = 0, hour = 0, minute = 0;
        difference = endcalendar.getTimeInMillis() - starcalendar.getTimeInMillis();
        hour = difference / (3600 * 1000);
        difference = difference % (3600 * 1000);
        minute = difference / (60 * 1000);
        time = "" + hour + ":" + minute;
        return time;
    }

    public class PlayHistoryThread extends Thread {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 1;
            playhandler.sendMessage(msg);
        }
    }

}
