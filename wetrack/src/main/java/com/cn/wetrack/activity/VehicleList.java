package com.cn.wetrack.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wetrack.R;
import com.cn.wetrack.activity.baidu.MonitorLocationBaidu;
import com.cn.wetrack.entity.Location;
import com.cn.wetrack.entity.SResponse;
import com.cn.wetrack.entity.Vehicle;
import com.cn.wetrack.util.AppUtils;
import com.cn.wetrack.util.HttpRequestClient;
import com.cn.wetrack.util.SProtocol;
import com.cn.wetrack.widgets.CustomProgressDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 车队树
 */
public class VehicleList extends Activity implements OnChildClickListener {
    public EditText operatePsw;// 操作密码
    SWApplication glob;// 全局控制类
    private Handler handler;
    private CustomProgressDialog progressDialog = null;
    private ImageButton backButton;// 返回
    private ExpandableListView listView = null;
    private ExpandAdapter adapter = null;

    /*临时车辆列表*/
    private List<List<Vehicle>> vehicleListData = new ArrayList<>();
    private ArrayList<String> motorcadeNameArray = new ArrayList<>();
    /*在线车辆列表*/
    private List<List<Vehicle>> onlineVehicleListData = new ArrayList<>();
    private ArrayList<String> onlineMotorcadeNameArray = new ArrayList<>();
    /*离线车辆列表*/
    private List<List<Vehicle>> offlineVehicleListData = new ArrayList<>();
    private ArrayList<String> offlineMotorcadeNameArray = new ArrayList<>();
    /*到期车辆列表*/
    private List<List<Vehicle>> overdueVehicleListData = new ArrayList<>();
    private ArrayList<String> overdueMotorcadeNameArray = new ArrayList<>();

    private ArrayList<String> allMotorcadeNameArray = new ArrayList<>();
    private boolean state_all = true;
    private boolean state_online = false;// 四个按钮的状态
    private boolean state_offline = false;
    private boolean state_overdue = false;
    private boolean search = false;
    private int oldState = 0;
    private LinearLayout vehicle_all, vehicle_online, vehicle_offline, vehicle_overdue;// 四个状态的线性布局
    private ImageView all_img, online_img, offline_img, overdue_img;// 四个状态的图片
    private TextView all_text, online_text, offline_text, overdue_text;// 四个状态的文字
    private Calendar calendar = Calendar.getInstance();// 接收的时间
    private Date date = null;// 中间变量
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 字符转换
    private SimpleDateFormat otherDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");// 另外一种字符转换
    private Calendar serverCalendar = Calendar.getInstance();//服务器时间
    private Date serverDate = null;//服务器日期
    private EditText searchEditText = null;// 搜索输入框
    private TextView searchAllBtn;
    private TextView title = null;// 标题
    private InputMethodManager manager;
    private DisplayMetrics metric;// 屏幕管理
    private int online = 0, offline = 0, overdue = 0, all = 0;
    private Boolean IsMonitor = false;
    private List<List<Vehicle>> vehicleList;
    private Map<String, Location> locationMap;

    //region  查询车队按钮点击处理Handler
    private ExpandAdapter.GroupBtnListener groupBtnListener = new ExpandAdapter.GroupBtnListener() {
        @Override
        public void searchGroup(int position) {
            String systemNoStr = "";
            switch (oldState) {
                case 0: {
                    systemNoStr = getGroupSystemNoStr(vehicleList, position);
                    break;
                }
                case 1: {
                    systemNoStr = getGroupSystemNoStr(onlineVehicleListData, position);
                    break;
                }
                case 3: {
                    systemNoStr = getGroupSystemNoStr(offlineVehicleListData, position);
                    break;
                }
                case 4: {
                    systemNoStr = getGroupSystemNoStr(overdueVehicleListData, position);
                    break;
                }
            }

            Intent intent = new Intent();
            intent.putExtra("querySystemNoStr", systemNoStr);
//            System.out.println("querySystemNoStr:" + systemNoStr);
            intent.setClass(VehicleList.this, SelectStartEndDate.class);
            startActivity(intent);
        }
    };
    //endregion

    //region  单车监控时, 获取当前位置信息返回处理Handler
    private Handler vehicleHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            switch (msg.what) {
                case 0:
                    if (IsMonitor) {
                        Intent intent = new Intent();
                        if (glob.mapType == 0) {
                            if (AppUtils.IsGooglePlayAvailable(VehicleList.this)) {
                                intent.setClass(VehicleList.this, MonitorLocation.class);
                            }
                        }
                        if (glob.mapType == 1) {
                            intent.setClass(VehicleList.this, MonitorLocationBaidu.class);
                        }
                        startActivity(intent);
                    }
                    break;
                case 1:
                    Toast.makeText(VehicleList.this, getString(R.string.data_get_fail), Toast.LENGTH_LONG).show();
//                    getLocationThread.start();
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
                        GetAllList();
                        GetDataSource(0, "");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1: {

                    break;
                }
                case -1: {

                    break;
                }
            }
            progressDialog.dismiss();
        }

    };
    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* 获取屏幕参数 */
        metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        setContentView(R.layout.activity_vehiclelist);

        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        /* 取得全局变量 */
        glob = (SWApplication) getApplicationContext();
        vehicleList = glob.vehicleListData;
        locationMap = glob.vehNoLocationMap;
        handler = createHandler();
        progressDialog = new CustomProgressDialog(VehicleList.this);
        /* 返回 */
        backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /* 标题 */
        title = (TextView) findViewById(R.id.vehiclelist_title);

        searchAllBtn = (TextView) findViewById(R.id.vehicle_search_all);
        searchAllBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String systemNoStr = "";
                for (int i = 0; i < vehicleList.size(); i++) {
                    systemNoStr += getGroupSystemNoStr(vehicleList, i) + ",";
                }
                systemNoStr = systemNoStr.substring(0, systemNoStr.length() - 1);
                Intent intent = new Intent();
                intent.putExtra("querySystemNoStr", systemNoStr);
                intent.setClass(VehicleList.this, SelectStartEndDate.class);
                startActivity(intent);
            }
        });
        if (glob.menuIndex == 2 || glob.menuIndex == 3)
            searchAllBtn.setVisibility(View.VISIBLE);
        else searchAllBtn.setVisibility(View.GONE);

        /* 搜索输入框 */
        searchEditText = (EditText) findViewById(R.id.vehiclelist_edittext);
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //根据输入获取筛选数据
                GetDataSource(5, "" + searchEditText.getText().toString());
            }
        });

        listView = (ExpandableListView) findViewById(R.id.list);
        // 四组状态

        all_img = (ImageView) findViewById(R.id.all_img);
        all_text = (TextView) findViewById(R.id.all_text);

        online_img = (ImageView) findViewById(R.id.online_img);
        online_text = (TextView) findViewById(R.id.online_text);

        offline_img = (ImageView) findViewById(R.id.offline_img);
        offline_text = (TextView) findViewById(R.id.offline_text);

        overdue_img = (ImageView) findViewById(R.id.overdue_img);
        overdue_text = (TextView) findViewById(R.id.overdue_text);

        listView.setDescendantFocusability(ExpandableListView.FOCUS_AFTER_DESCENDANTS);
        listView.setOnChildClickListener(this);

        vehicle_all = (LinearLayout) findViewById(R.id.linear_all);
        vehicle_all.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!state_all) {
                    if (search) {
                        hideSearch();
                    }
                    GetDataSource(0, "");
                }
            }
        });

        // 在线
        vehicle_online = (LinearLayout) findViewById(R.id.linear_online);
        vehicle_online.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!state_online) {
                    if (search) {
                        hideSearch();
                    }
                    GetDataSource(1, "");
                }
            }
        });

        // 离线
        vehicle_offline = (LinearLayout) findViewById(R.id.linear_offline);
        vehicle_offline.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!state_offline) {
                    if (search) {
                        hideSearch();
                    }
                    GetDataSource(3, "");

                }
            }
        });
        // 服务到期
        vehicle_overdue = (LinearLayout) findViewById(R.id.linear_overdue);
        vehicle_overdue.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!state_overdue) {
                    if (search) {
                        hideSearch();
                    }
                    GetDataSource(4, "");
                }
            }
        });

        progressDialog.setMessage(getString(R.string.refreshing));
        progressDialog.show();
        new GetServerTime().start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public String getGroupSystemNoStr(List<List<Vehicle>> vehicleList, int position) {
        String systemNoStr = "";
        List<Vehicle> groupList = vehicleList.get(position);
        for (Vehicle vehicle : groupList) {
            systemNoStr += vehicle.getSystemNo() + ",";
        }
        systemNoStr = systemNoStr.substring(0, systemNoStr.length() - 1);
        return systemNoStr;
    }

    /**
     * 创建Handler
     */
    private Handler createHandler() {

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {

                // 关闭进度条
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Bundle b = message.getData();
                switch (message.what) {
                    case 1: {
                        if (b.getBoolean("ret")) {
                            glob.totalMileage = b.getString("result");
                            Intent intent1 = new Intent();
                            intent1.setClass(VehicleList.this, MileageReport.class);
                            startActivity(intent1);
                        } else {
                            Toast.makeText(VehicleList.this, b.getString("result"),
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                    case 2: {
                        Toast.makeText(VehicleList.this, b.getString("result"),
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 3: {
                        Toast.makeText(VehicleList.this, b.getString("result"),
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                super.handleMessage(message);
            }
        };
        return handler;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        Vehicle vehicle = adapter.getChild(groupPosition, childPosition);
        glob.curVehicle = vehicle;
        // 判断菜单进行对应操作
        switch (glob.menuIndex) {
            // 实时定位——单车监控
            case 0: {
                // 当前监控车辆位置
                IsMonitor = true;
                finish();//方案一: 结束当前Activity
                break;
            }
            // 历史轨迹
            case 1: {
                Intent intent = new Intent();
                intent.setClass(VehicleList.this, SelectDate.class);
                startActivity(intent);
                break;
            }
            // 里程统计
            case 2: {
                Intent intent = new Intent();
                intent.putExtra("querySystemNoStr", glob.curVehicle.getSystemNo());
                intent.setClass(VehicleList.this, SelectStartEndDate.class);
                startActivity(intent);
                break;
            }
            // 油量统计
            case 3: {
                Intent intent = new Intent();
                intent.putExtra("querySystemNoStr", glob.curVehicle.getSystemNo());
                intent.setClass(VehicleList.this, SelectStartEndDate.class);
                startActivity(intent);
                break;
            }
            // 远程开锁
            case 6: {
                showOperatePsw();
                break;
            }
            // 远程关锁
            case 7: {
                showOperatePsw();
                break;
            }
        }
        return true;
    }

    /**
     * 输入操作密码
     */
    private void showOperatePsw() {
        operatePsw = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.KDY_please_input));
        builder.setView(operatePsw);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.btn_title_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 获取操作密码
                        String operationPsw = glob.curVehicle.getOperationPwd();
                        System.out.println("operationPsw:" + operationPsw);
                        // 判断操作密码是否正确
                        if (operationPsw.equals(operatePsw.getText().toString())) {
                            if (glob.menuIndex == 6) {
                                new KYThread().start();
                            }
                            if (glob.menuIndex == 7) {
                                new DYThread().start();
                            }
                        } else {
                            Toast.makeText(VehicleList.this,
                                    getResources().getString(R.string.KDY_psw_error),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        builder.setNegativeButton(R.string.btn_title_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * 返回按键 结束activity
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
        }
        return true;
    }

    public void GetDataSource(int method, String searchtext) {

        vehicleListData.clear();
        motorcadeNameArray.clear();

        switch (method) {
            case 0:// 无筛选
                all_img.setBackgroundResource(R.drawable.vehicle_all_select);
                all_text.setTextColor(0xff3fa9fd);
                online_img.setBackgroundResource(R.drawable.vehicle_online);
                online_text.setTextColor(0xffc9c9c9);
                offline_img.setBackgroundResource(R.drawable.vehicle_offline);
                offline_text.setTextColor(0xffc9c9c9);
                overdue_img.setBackgroundResource(R.drawable.vehicle_overdue);
                overdue_text.setTextColor(0xffc9c9c9);

                state_all = true;
                state_online = false;
                state_offline = false;
                state_overdue = false;
                search = false;
                oldState = 0;

                if (glob.menuIndex == 2 || glob.menuIndex == 3) {
                    adapter = new ExpandAdapter(this, vehicleList, groupBtnListener, allMotorcadeNameArray, locationMap, method, true);
                } else {
                    adapter = new ExpandAdapter(this, vehicleList, groupBtnListener, allMotorcadeNameArray, locationMap, method, false);
                }

                listView.setAdapter(adapter);
                break;
            case 1:// 筛选在线数据
                all_img.setBackgroundResource(R.drawable.vehicle_all);
                all_text.setTextColor(0xffc9c9c9);
                online_img.setBackgroundResource(R.drawable.vehicle_online_select);
                online_text.setTextColor(0xff3fa9fd);
                offline_img.setBackgroundResource(R.drawable.vehicle_offline);
                offline_text.setTextColor(0xffc9c9c9);
                overdue_img.setBackgroundResource(R.drawable.vehicle_overdue);
                overdue_text.setTextColor(0xffc9c9c9);

                state_all = false;
                state_online = true;
                state_offline = false;
                state_overdue = false;
                search = false;
                oldState = 1;

                if (glob.menuIndex == 2 || glob.menuIndex == 3) {
                    adapter = new ExpandAdapter(this, onlineVehicleListData, groupBtnListener, allMotorcadeNameArray, locationMap, method, true);
                } else {
                    adapter = new ExpandAdapter(this, onlineVehicleListData, groupBtnListener, allMotorcadeNameArray, locationMap, method, false);
                }

                listView.setAdapter(adapter);
                break;
            case 2:// 筛选静止数据
                break;

            case 3:// 筛选离线数据
                all_img.setBackgroundResource(R.drawable.vehicle_all);
                all_text.setTextColor(0xffc9c9c9);
                online_img.setBackgroundResource(R.drawable.vehicle_online);
                online_text.setTextColor(0xffc9c9c9);
                offline_img.setBackgroundResource(R.drawable.vehicle_offline_select);
                offline_text.setTextColor(0xff3fa9fd);
                overdue_img.setBackgroundResource(R.drawable.vehicle_overdue);
                overdue_text.setTextColor(0xffc9c9c9);

                state_all = false;
                state_online = false;
                state_offline = true;
                state_overdue = false;
                search = false;
                oldState = 3;

                if (glob.menuIndex == 2 || glob.menuIndex == 3) {
                    adapter = new ExpandAdapter(this, offlineVehicleListData, groupBtnListener, allMotorcadeNameArray, locationMap, method, true);
                } else {
                    adapter = new ExpandAdapter(this, offlineVehicleListData, groupBtnListener, allMotorcadeNameArray, locationMap, method, false);
                }

                listView.setAdapter(adapter);
                break;
            case 4:// 筛选服务到期数据
                all_img.setBackgroundResource(R.drawable.vehicle_all);
                all_text.setTextColor(0xffc9c9c9);
                online_img.setBackgroundResource(R.drawable.vehicle_online);
                online_text.setTextColor(0xffc9c9c9);
                offline_img.setBackgroundResource(R.drawable.vehicle_offline);
                offline_text.setTextColor(0xffc9c9c9);
                overdue_img.setBackgroundResource(R.drawable.vehicle_overdue_select);
                overdue_text.setTextColor(0xff3fa9fd);

                state_all = false;
                state_online = false;
                state_offline = false;
                state_overdue = true;
                search = false;
                oldState = 4;

                if (glob.menuIndex == 2 || glob.menuIndex == 3) {
                    adapter = new ExpandAdapter(this, overdueVehicleListData, groupBtnListener, allMotorcadeNameArray, locationMap, method, true);
                } else {
                    adapter = new ExpandAdapter(this, overdueVehicleListData, groupBtnListener, allMotorcadeNameArray, locationMap, method, false);
                }

                listView.setAdapter(adapter);
                break;
            /* 按输入车牌号筛选数据 */
            case 5:
                all_img.setBackgroundResource(R.drawable.vehicle_all);
                all_text.setTextColor(0xffc9c9c9);
                online_img.setBackgroundResource(R.drawable.vehicle_online);
                online_text.setTextColor(0xffc9c9c9);
                offline_img.setBackgroundResource(R.drawable.vehicle_offline);
                offline_text.setTextColor(0xffc9c9c9);
                overdue_img.setBackgroundResource(R.drawable.vehicle_overdue);
                overdue_text.setTextColor(0xffc9c9c9);
                state_all = false;
                state_online = false;
                state_offline = false;
                state_overdue = false;
                search = true;
                for (int i = 0; i < vehicleList.size(); i++) {

                    List<Vehicle> mydata = new ArrayList<Vehicle>();
                    for (int j = 0; j < vehicleList.get(i).size(); j++) {
                        if (vehicleList.get(i).get(j).getVehNoF()
                                .indexOf(searchtext) != -1) {
                            mydata.add(vehicleList.get(i).get(j));
                        }
                    }
                    if (mydata.size() != 0) {
                        vehicleListData.add(mydata);
                        motorcadeNameArray.add(glob.motorcadeNameArray[i]);
                    }

                }

                if (glob.menuIndex == 2 || glob.menuIndex == 3) {
                    adapter = new ExpandAdapter(this, vehicleListData, groupBtnListener, allMotorcadeNameArray, locationMap, method, true);
                } else {
                    adapter = new ExpandAdapter(this, vehicleListData, groupBtnListener, allMotorcadeNameArray, locationMap, method, false);
                }

                listView.setAdapter(adapter);
                break;
        }

    }

    /**
     * 获取在线，离线，到期车辆数据
     */
    public void GetAllList() {
        all = 0;
        for (int i = 0; i < vehicleList.size(); i++) {
            List<Vehicle> onlinedata = new ArrayList<>();
            List<Vehicle> offlinedata = new ArrayList<>();
            List<Vehicle> overduedata = new ArrayList<>();
            for (int j = 0; j < vehicleList.get(i).size(); j++) {
                all++;
                Vehicle tempv = vehicleList.get(i).get(j);
                if (tempv.getIsOverdue()) {
                    vehicleList.get(i).get(j).setCurStatus(4);
                    overduedata.add(tempv);
                    overdue++;
                } else {
                    if (locationMap.containsKey(tempv.getVehNoF())) {
                        try {
                            date = dateFormat.parse(locationMap.get(
                                    tempv.getVehNoF()).getTime());

                        } catch (ParseException e) {
                            try {
                                date = otherDateFormat.parse(locationMap.get(
                                        tempv.getVehNoF()).getTime());
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            e.printStackTrace();
                        }
                        calendar.setTime(date);
                        Boolean IsOnLine = isOnline(serverCalendar, calendar);

                        /*
                        sysCalendar = Calendar.getInstance();
                        if (getResources().getConfiguration().locale.getCountry().equals("CN")) {
                            if (locationMap.get(
                                    tempv.getVehNoF()).getServerTime() != null) {
                                try {
                                    serverDate = dateFormat.parse(locationMap.get(tempv.getVehNoF()).getServerTime());
                                    serverCalendar.setTime(serverDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                IsOnLine = isOnline(sysCalendar, serverCalendar);
                            } else {
                                IsOnLine = isOnline(sysCalendar, calendar);
                            }
                        } else {
                            IsOnLine = isOnline(sysCalendar, calendar);
                        }
                        */

                        if (IsOnLine) {
                            vehicleList.get(i).get(j).setCurStatus(1);
                            onlinedata.add(tempv);
                            online++;
                        } else {
                            vehicleList.get(i).get(j).setCurStatus(3);
                            offlinedata.add(tempv);
                            offline++;
                        }
                    } else {
                        vehicleList.get(i).get(j).setCurStatus(3);
                        offlinedata.add(tempv);
                        offline++;
                    }
                }
            }

            allMotorcadeNameArray.add(glob.motorcadeNameArray[i]);
            if (onlinedata.size() != 0) {
                onlineVehicleListData.add(onlinedata);
                onlineMotorcadeNameArray.add(glob.motorcadeNameArray[i]);
            }
            if (offlinedata.size() != 0) {
                offlineVehicleListData.add(offlinedata);
                offlineMotorcadeNameArray.add(glob.motorcadeNameArray[i]);
            }
            if (overduedata.size() != 0) {
                overdueVehicleListData.add(overduedata);
                overdueMotorcadeNameArray.add(glob.motorcadeNameArray[i]);
            }
        }

        all_text.setText(getResources().getString(R.string.vehicle_all)
                + "(" + all + ")");
        online_text.setText(getResources().getString(R.string.vehicle_online)
                + "(" + online + ")");
        offline_text.setText(getResources().getString(R.string.vehicle_offline)
                + "(" + offline + ")");
        overdue_text.setText(getResources().getString(R.string.vehicle_overdue)
                + "(" + overdue + ")");
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

    public void hideSearch() {
//        searchlinear.setVisibility(View.GONE);
//        cancelbtn.setVisibility(View.GONE);
//        searchbtn.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        searchEditText.setText("");
        manager.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0); // 强制隐藏键盘
    }

    /**
     * 远程开油
     */
    class KYThread extends Thread {
        public void run() {
            Bundle b = new Bundle();
            SResponse response = HttpRequestClient.vehicleOperational(
                    glob.curVehicle.getSystemNo(), 1, 1);
            if (response.getCode() == SProtocol.SUCCESS) {
                b.putString("result", (String) response.getResult());
            } else {
                b.putString(
                        "result",
                        SProtocol.getFailMessage(response.getCode(),
                                response.getMessage()));
            }

            Message message = new Message();
            message.setData(b);
            message.what = 2;
            handler.sendMessage(message);
        }
    }

    /**
     * 远程断油
     */
    class DYThread extends Thread {
        public void run() {
            Bundle b = new Bundle();

            SResponse response = HttpRequestClient.vehicleOperational(
                    glob.curVehicle.getSystemNo(), 1, 0);
            if (response.getCode() == SProtocol.SUCCESS) {
                b.putString("result", (String) response.getResult());
            } else {
                b.putString(
                        "result",
                        SProtocol.getFailMessage(response.getCode(),
                                response.getMessage()));
            }

            Message message = new Message();
            message.setData(b);
            message.what = 3;
            handler.sendMessage(message);
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