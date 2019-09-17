package com.cn.wetrack.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.wetrack.R;
import com.cn.wetrack.activity.baidu.AllLocationBaidu;
import com.cn.wetrack.entity.Location;
import com.cn.wetrack.entity.SResponse;
import com.cn.wetrack.entity.Structure;
import com.cn.wetrack.entity.Vehicle;
import com.cn.wetrack.util.AppUtils;
import com.cn.wetrack.util.HttpRequestClient;
import com.cn.wetrack.util.SProtocol;
import com.cn.wetrack.widgets.CustomProgressDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 主界面
 */
public class Main extends Activity {
    SWApplication glob;
    private Handler handler;
    private long exitTimeMillis = 0;// 返回退出标记
    private String menuStr[] = new String[9];// 菜单
    private Integer[] menuIcon = new Integer[9];// 菜单图标
    private CustomProgressDialog progressDialog = null;// 进度
    //    private Runnable keepAliveRunnable = null;// 心跳线程
    private Boolean IsLoaded = false;
    //    private Thread getVehicleDateThread = new getVehicleData();
    private Boolean IsListGet = false;
    private MainMenuAdapter adapter;

    //region 获取车辆数据的处理
    private Handler getVehicleDataHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            switch (msg.what) {
                case 0:
                    IsLoaded = true;
                    MenuClick(glob.menuIndex);
                    break;
                case 1:
                    new AlertDialog.Builder(Main.this)
                            .setTitle(R.string.alert_title)
                            .setMessage(R.string.data_get_fail)
                            .setNegativeButton(R.string.btn_title_cancel, null)
                            .setPositiveButton(R.string.btn_title_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.setMessage(getResources().getString(
                                            R.string.get_vehicle_info));
                                    progressDialog.show();
                                    new getVehicleData().start();
                                }
                            })
                            .show();
                    break;
            }
        }
    };
    //endregion

    //region 登出数据处理
    private Handler logoutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    glob.sp.edit().putBoolean("autoLogin", false).commit();
                    Intent intent = new Intent();
                    intent.setClass(Main.this, Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
                    break;
                case 1:
                    Toast.makeText(Main.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    //endregion

    //region 获取未读消息数数据处理
    private Handler unReadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: {
                    JSONObject object = JSON.parseObject((String) msg.obj);
                    adapter.setAlarmNum(object.getIntValue("alarmcount"));
                    adapter.setNoticeNum(object.getIntValue("noticecount"));
                    adapter.notifyDataSetChanged();
                    break;
                }
                case 1: {
                    break;
                }
            }
        }
    };
    //endregion

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        glob = (SWApplication) getApplicationContext();
        handler = createHandler();
        glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        glob.mapType = glob.sp.getInt("mapType", 0);

        progressDialog = new CustomProgressDialog(Main.this);
        /* 切换账户 */
        ImageView switchAccountBtn = (ImageView) this
                .findViewById(R.id.main_switchAccountbtn);
        switchAccountBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Main.this)
                        .setTitle(R.string.alert_title)
                        .setMessage(R.string.exit_alert)
                        .setNegativeButton(R.string.btn_title_cancel, null)
                        .setPositiveButton(R.string.btn_title_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Logout().start();
                            }
                        })
                        .show();
            }
        });
        /* 菜单 */
        menuStr[0] = getResources().getString(R.string.main_btn_title_now_location);
        menuStr[1] = getResources().getString(R.string.main_btn_title_orbit_query);
        menuStr[2] = getResources().getString(R.string.main_btn_title_mileage_statistics);
        menuStr[3] = getResources().getString(R.string.main_btn_title_oil_statistics);
        menuStr[4] = getResources().getString(R.string.main_btn_title_alarm_infomation);
        menuStr[5] = getResources().getString(R.string.main_btn_title_official_news);
        menuStr[6] = getResources().getString(R.string.main_btn_title_remote_on_lock);
        menuStr[7] = getResources().getString(R.string.main_btn_title_remote_off_lock);
        menuStr[8] = getResources().getString(R.string.main_btn_title_app_settings);
        menuIcon[0] = R.mipmap.main_btn_now_location;
        menuIcon[1] = R.mipmap.main_btn_orbit_query;
        menuIcon[2] = R.mipmap.main_btn_mileage_statistics;
        menuIcon[3] = R.mipmap.main_btn_oil_statistics;
        menuIcon[4] = R.mipmap.main_btn_alarm_infomation;
        menuIcon[5] = R.mipmap.main_btn_official_news;
        menuIcon[6] = R.mipmap.main_btn_on_lock;
        menuIcon[7] = R.mipmap.main_btn_off_lock;
        menuIcon[8] = R.mipmap.main_btn_app_settings;

		/* 功能表格布局 */
        initGrid();

        /*
        new Thread() {
            @Override
            public void run() {
                super.run();
                HttpRequestClient.alarmPushTest();
                HttpRequestClient.noticePushTest();
            }
        }.start();
        */
    }

    /**
     * Handler
     */
    private Handler createHandler() {
        return new Handler() {
            @Override
            public void handleMessage(Message message) {
                Bundle b = message.getData();
                switch (message.what) {
                    case 0: {
                        IsListGet = true;
                        if (null != progressDialog && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Intent intent = new Intent();
                        if (b.getBoolean("ret")) {
                            switch (glob.menuIndex) {
                                case 0:
                                    if (glob.mapType == 0)
                                        intent.setClass(Main.this, AllLocation.class);
                                    if (glob.mapType == 1)
                                        intent.setClass(Main.this, AllLocationBaidu.class);
                                    startActivity(intent);
                                    break;
                                case 1:
                                    intent.setClass(Main.this, VehicleList.class);
                                    startActivity(intent);
                                    break;
                                case 2:
                                    intent.setClass(Main.this, VehicleList.class);
                                    startActivity(intent);
                                    break;
                                case 3:
                                    intent.setClass(Main.this, VehicleList.class);
                                    startActivity(intent);
                                    break;
                                case 4:
                                    intent.setClass(Main.this, AlarmReport.class);
                                    startActivity(intent);
                                    break;

                                case 5:
                                    intent.setClass(Main.this, NoticeActivity.class);
                                    startActivity(intent);
                                    break;
                                case 6:
                                    intent.setClass(Main.this, VehicleList.class);
                                    startActivity(intent);
                                    break;
                                case 7:
                                    intent.setClass(Main.this, VehicleList.class);
                                    startActivity(intent);
                                    break;
                                default:
                                    break;
                            }

                        } else {
                            Toast.makeText(Main.this, b.getString("result"),
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                    case 9: {
                        if (b.getString("msg") != null) {
                            Toast.makeText(Main.this, b.getString("msg"),
                                    Toast.LENGTH_SHORT).show();
                        }
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        break;
                    }
                }
                super.handleMessage(message);
            }
        };
    }

    /**
     * 功能列表布局
     */
    private void initGrid() {
        GridView gridView = (GridView) findViewById(R.id.gridView1);
        ArrayList<HashMap<String, Object>> menuLists = new ArrayList<>();
        for (int i = 0; i < menuStr.length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("itemImage", menuIcon[i]);
            map.put("itemText", menuStr[i]);
            menuLists.add(map);
        }

        /*
        SimpleAdapter adpter = new SimpleAdapter(this, menuLists,
                R.layout.menuitem, new String[]{"itemImage", "itemText"},
                new int[]{R.id.imageView_ItemImage, R.id.textView_ItemText});
        */
        adapter = new MainMenuAdapter(menuStr, menuIcon, this);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                showMenu(arg2);
            }
        });
    }

    /**
     * 菜单响应
     */
    public void showMenu(int iSel) {
        /* 记录点击菜单序号 */
        glob.menuIndex = iSel;
        if (glob.vehicleListData == null || glob.vehicleListData.size() <= 0) {
            progressDialog.setMessage(getResources().getString(
                    R.string.get_vehicle_info));
            progressDialog.show();
            new getVehicleData().start();
        } else {
            MenuClick(iSel);
        }
    }

    public void MenuClick(int iSel) {
        Intent intent = new Intent();
        switch (iSel) {
            /* 实时定位 */
            case 0: {
                if (glob.mapType == 0) {
                    if (AppUtils.IsGooglePlayAvailable(Main.this)) {
                        if (glob.vehNoLocationMap == null || glob.vehNoLocationMap.size() <= 0) {
                            progressDialog.setMessage(getResources().getString(
                                    R.string.get_location_info));
                            progressDialog.show();
                            new GetAllLocationThread().start();
                        } else {
                            intent.setClass(Main.this, AllLocation.class);
                            startActivity(intent);
                        }
                    }
                }
                if (glob.mapType == 1) {
                    if (glob.vehNoLocationMap == null || glob.vehNoLocationMap.size() <= 0) {
                        progressDialog.setMessage(getResources().getString(
                                R.string.get_location_info));
                        progressDialog.show();
                        new GetAllLocationThread().start();
                    } else {
                        intent.setClass(Main.this, AllLocationBaidu.class);
                        startActivity(intent);
                    }
                }
                break;
            }
            /* 轨迹查询 */
            case 1: {
                if (glob.vehNoLocationMap == null || glob.vehNoLocationMap.size() <= 0) {
//                    IsListGet = true;
                    progressDialog.setMessage(getResources().getString(
                            R.string.get_location_info));
                    progressDialog.show();
                    new GetAllLocationThread().start();
                } else {
                    intent.setClass(Main.this, VehicleList.class);
                    startActivity(intent);
                }
                break;
            }
            /** 里程统计*/
            case 2: {
                if (glob.vehNoLocationMap == null || glob.vehNoLocationMap.size() <= 0) {
//                    IsListGet = true;
                    progressDialog.setMessage(getResources().getString(
                            R.string.get_location_info));
                    progressDialog.show();
                    new GetAllLocationThread().start();
                } else {
                    intent.setClass(Main.this, VehicleList.class);
                    startActivity(intent);
                }
                break;
            }
            /* 油量统计 */
            case 3: {
                if (glob.vehNoLocationMap == null || glob.vehNoLocationMap.size() <= 0) {
//                    IsListGet = true;
                    progressDialog.setMessage(getResources().getString(
                            R.string.get_location_info));
                    progressDialog.show();
                    new GetAllLocationThread().start();
                } else {
                    intent.setClass(Main.this, VehicleList.class);
                    startActivity(intent);
                }
                break;
            }
            /* 报警信息 */
            case 4: {
                intent.setClass(Main.this, AlarmReport.class);
                startActivity(intent);
                break;
            }
            /* 公告信息 */
            case 5: {
                intent.setClass(Main.this, NoticeActivity.class);
                startActivity(intent);
                break;
            }
            /* 远程开锁 */
            case 6: {
                if (glob.vehNoLocationMap == null || glob.vehNoLocationMap.size() <= 0) {
//                    IsListGet = true;
                    progressDialog.setMessage(getResources().getString(
                            R.string.get_location_info));
                    progressDialog.show();
                    new GetAllLocationThread().start();
                } else {
                    intent.setClass(Main.this, VehicleList.class);
                    startActivity(intent);
                }
                break;
            }
            /* 远程关锁 */
            case 7: {
                if (glob.vehNoLocationMap == null || glob.vehNoLocationMap.size() <= 0) {
//                    IsListGet = true;
                    progressDialog.setMessage(getResources().getString(
                            R.string.get_location_info));
                    progressDialog.show();
                    new GetAllLocationThread().start();
                } else {
                    intent.setClass(Main.this, VehicleList.class);
                    startActivity(intent);
                }
                break;
            }
        /* 设置 */
            case 8: {
                intent.setClass(Main.this, Setting.class);
                Main.this.startActivity(intent);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
//		getVehicleDataHandler.post(new getVehicleData());
        super.onResume();
        new GetUnReadRecordCount().start();
		/* 判断是否切换帐号 */
        if (glob.switchAccount) {
            glob.switchAccount = false;
            Intent intent = new Intent(Main.this, Login.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
		/* 移除心跳线程 */
//        handler.removeCallbacks(keepAliveRunnable);
    }

    /* 返回按键 */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ((System.currentTimeMillis() - exitTimeMillis) > 3000) {
                Toast.makeText(Main.this, R.string.SURETOLOGOUT,
                        Toast.LENGTH_SHORT).show();
                exitTimeMillis = System.currentTimeMillis();
            } else {
                finish();
            }
        }
        return false;
    }

    /**
     * 获取本账户下所有位置
     */
    class GetAllLocationThread extends Thread {
        public void run() {
            Bundle b = new Bundle();
            SResponse response = HttpRequestClient.getVehicleLocation("", "1",
                    glob.sp.getString("user", ""), glob.sp.getString("psw", ""));
            if (response.getCode() == SProtocol.SUCCESS) {
                b.putBoolean("ret", true);
                @SuppressWarnings("unchecked")
                List<Location> locations = (List<Location>) response
                        .getResult();
				/* 先清空 */
                glob.vehNoLocationMap.clear();
                for (Location l : locations) {
                    glob.vehNoLocationMap.put(
                            glob.systemNoVehNoMap.get(l.getSystemNo()), l);
                }
                glob.curVLocation = locations.get(0);
            } else {
                b.putBoolean("ret", false);
                b.putString("result", SProtocol.getFailMessage(response.getCode(),
                        response.getMessage()));
            }

            Message message = new Message();
            message.setData(b);
            message.what = 0;
            handler.sendMessage(message);
        }
    }

    /**
     * 获取车辆列表
     **/
    class getVehicleData extends Thread {
        @Override
        public void run() {
			/*获取结构数据*/
            SResponse responseS = HttpRequestClient.getVehicleData(glob.sp.getString("user", ""));
            if (responseS.getCode() == SProtocol.SUCCESS) {
				/*清空数据*/
                glob.vehicleListData.clear();
                glob.systemNoVehNoMap.clear();
                glob.vehNoLocationMap.clear();
                @SuppressWarnings("unchecked")
                List<Structure> structures = (List<Structure>) responseS.getResult();
                glob.motorcadeNameArray = new String[structures.size()];
                for (int i = 0; i < structures.size(); i++) {
                    Structure structure = structures.get(i);
					/*车队名数组*/
                    glob.motorcadeNameArray[i] = structure.getMotorcade().getMotorcadeName();
					/*车队树数据*/
                    glob.vehicleListData.add(Arrays.asList(structure.getVehicles()));

					/*系统编号车牌号哈希表*/
                    for (Vehicle v : structure.getVehicles()) {
                        glob.systemNoVehNoMap.put(v.getSystemNo(), v.getVehNoF());
                    }
                }
                Message msg = new Message();
                msg.what = 0;
                getVehicleDataHandler.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.what = 1;
                getVehicleDataHandler.sendMessage(msg);
            }
            super.run();
        }
    }

    class Logout extends Thread {
        @Override
        public void run() {
            super.run();
            SResponse response = HttpRequestClient.logout(glob.sp.getString("user", "")
                    , glob.sp.getString("RegistrationID", ""));
            if (response.getCode() == SProtocol.SUCCESS) {
                Message msg = new Message();
                msg.what = 0;
                logoutHandler.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = response.getMessage();
                logoutHandler.sendMessage(msg);
            }
        }
    }

    class GetUnReadRecordCount extends Thread {
        @Override
        public void run() {
            super.run();
            SResponse response = HttpRequestClient.GetUnReadRecordCount(glob.sp.getString("user", ""));
            Message msg = new Message();
            if (response.getCode() == SProtocol.SUCCESS) {
                msg.what = 0;
                msg.obj = response.getResult();
                Log.i("获取未读消息数", String.valueOf(response.getResult()));
            } else {
                msg.what = 1;
                msg.obj = response.getMessage();
            }
            unReadHandler.sendMessage(msg);
        }
    }
}