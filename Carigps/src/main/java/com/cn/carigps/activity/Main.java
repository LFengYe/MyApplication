package com.cn.carigps.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.cn.carigps.R;
import com.cn.carigps.activity.baidu.AllLocationBaidu;
import com.cn.carigps.entity.ADInfo;
import com.cn.carigps.entity.Location;
import com.cn.carigps.entity.SResponse;
import com.cn.carigps.entity.Structure;
import com.cn.carigps.entity.Vehicle;
import com.cn.carigps.util.AppUtils;
import com.cn.carigps.util.HttpRequestClient;
import com.cn.carigps.util.SProtocol;
import com.cn.carigps.widgets.CustomProgressDialog;
import com.cn.carigps.widgets.ImageCycleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 主界面
 */
public class Main extends Activity {
    MyApplication glob;
    private Handler handler;
    private long exitTimeMillis = 0;// 返回退出标记
    private CustomProgressDialog progressDialog = null;// 进度
    private ImageCycleView imageCycle;

    //region 点击事件
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_layout_now_location:
                    showMenu(0);
                    break;
                case R.id.main_layout_orbit_query:
                    showMenu(1);
                    break;
                case R.id.main_layout_mileage_statistics:
                    showMenu(2);
                    break;
                case R.id.main_layout_oil_statistics:
                    showMenu(3);
                    break;
                case R.id.main_layout_alarm_infomation:
                    showMenu(4);
                    break;
                case R.id.main_layout_official_news:
                    showMenu(5);
                    break;
                case R.id.main_layout_remote_on_lock:
                    showMenu(6);
                    break;
                case R.id.main_layout_remote_off_locak:
                    showMenu(7);
                    break;
                case R.id.main_layout_app_settings:
                    showMenu(8);
                    break;
                case R.id.main_layout_exit_system:
                    new AlertDialog.Builder(Main.this)
                            .setTitle(R.string.alert_title)
                            .setMessage(R.string.exit_alert)
                            .setNegativeButton(R.string.btn_title_cancel, null)
                            .setPositiveButton(R.string.btn_title_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    glob.sp.edit().putBoolean("autoLogin", false).commit();
                                    Intent intent = new Intent();
                                    intent.setClass(Main.this, Login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
                                }
                            })
                            .show();
                    break;
            }
        }
    };
    //endregion

    //region 获取车辆处理返回结果处理
    private Handler getVehicleDataHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null != progressDialog && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            switch (msg.what) {
                case 0:
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

    private ImageCycleView.ImageCycleViewListener myImageCycleViewListener = new ImageCycleView.ImageCycleViewListener() {
        @Override
        public void displayImage(int imageId, ImageView imageView) {
            imageView.setImageResource(imageId);
        }

        @Override
        public void onImageClick(ADInfo info, int position, View imageView) {

        }
    };

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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        glob = (MyApplication) getApplicationContext();
        handler = createHandler();
        glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        glob.mapType = glob.sp.getInt("mapType", 0);

        progressDialog = new CustomProgressDialog(Main.this);

        findViewById(R.id.main_layout_now_location).setOnClickListener(onClickListener);
        findViewById(R.id.main_layout_orbit_query).setOnClickListener(onClickListener);
        findViewById(R.id.main_layout_mileage_statistics).setOnClickListener(onClickListener);
        findViewById(R.id.main_layout_oil_statistics).setOnClickListener(onClickListener);
        findViewById(R.id.main_layout_alarm_infomation).setOnClickListener(onClickListener);
        findViewById(R.id.main_layout_official_news).setOnClickListener(onClickListener);
        findViewById(R.id.main_layout_remote_on_lock).setOnClickListener(onClickListener);
        findViewById(R.id.main_layout_remote_off_locak).setOnClickListener(onClickListener);
        findViewById(R.id.main_layout_app_settings).setOnClickListener(onClickListener);
        findViewById(R.id.main_layout_exit_system).setOnClickListener(onClickListener);

        initImageCycle();
    }

    private void initImageCycle() {
        imageCycle = (ImageCycleView) findViewById(R.id.image_cycle);
        ArrayList<ADInfo> adInfos = new ArrayList<>();
        adInfos.add(new ADInfo(R.mipmap.img_head_ad1));
        adInfos.add(new ADInfo(R.mipmap.img_head_ad2));
        adInfos.add(new ADInfo(R.mipmap.img_head_ad3));

        imageCycle.setImageResources(adInfos, myImageCycleViewListener);
    }

    /**
     * 功能列表布局
     */
    /*
    private void initGrid() {
        GridView gridView = (GridView) findViewById(R.id.gridView1);
        ArrayList<HashMap<String, Object>> menuLists = new ArrayList<>();
        for (int i = 0; i < menuStr.length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("itemImage", menuIcon[i]);
            map.put("itemText", menuStr[i]);
            menuLists.add(map);
        }

        SimpleAdapter adpter = new SimpleAdapter(this, menuLists,
                R.layout.menuitem, new String[]{"itemImage", "itemText"},
                new int[]{R.id.imageView_ItemImage, R.id.textView_ItemText});
        gridView.setAdapter(adpter);

        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                showMenu(arg2);
            }
        });
    }
    */

    /**
     * 菜单响应
     */
    public void showMenu(int iSel) {
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
    public class getVehicleData extends Thread {
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
}