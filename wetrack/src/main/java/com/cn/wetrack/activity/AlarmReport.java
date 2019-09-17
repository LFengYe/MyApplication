package com.cn.wetrack.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cn.wetrack.R;
import com.cn.wetrack.activity.baidu.OilDetailInfoBaidu;
import com.cn.wetrack.entity.Alarm;
import com.cn.wetrack.entity.SResponse;
import com.cn.wetrack.util.AppUtils;
import com.cn.wetrack.util.HttpRequestClient;
import com.cn.wetrack.util.SProtocol;
import com.cn.wetrack.widgets.CustomProgressDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

/**
 * Created by fuyzh on 16/8/2.
 */
public class AlarmReport extends Activity {
    SWApplication glob;// 全局控制类
    private ImageButton backBtn;// 返回
    private TableView tableView;
    private List<Alarm> resData;
    private List<String[]> data;
    SimpleTableDataAdapter dataAdapter;
    private SResponse response;
    private Calendar calendar = Calendar.getInstance();// 接收的时间
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 字符转换
    private CustomProgressDialog progressDialog = null;// 提示消息
    private String endTime;
    private boolean isLoading;
    private TableDataClickListener tableDataClickListener = new TableDataClickListener() {
        @Override
        public void onDataClicked(int rowIndex, Object clickedData) {
            Alarm alarm = resData.get(rowIndex);
            Bundle bundle = new Bundle();
            bundle.putParcelable("alarmItem", alarm);
            bundle.putString("type", "alarm");
            Intent intent = new Intent();
            intent.putExtras(bundle);
            if (glob.mapType == 0) {
                if (AppUtils.IsGooglePlayAvailable(AlarmReport.this)) {
                    intent.setClass(AlarmReport.this, OilDetailInfo.class);
                }
            }
            if (glob.mapType == 1) {
                intent.setClass(AlarmReport.this, OilDetailInfoBaidu.class);
            }
            startActivity(intent);

            updateRecordStatus(2, alarm.getId());
        }
    };

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            isLoading = false;
            if (msg.what == 0) {
                ArrayList<Alarm> tmpList = (ArrayList<Alarm>) response.getResult();
                resData.addAll(tmpList);

                for (Alarm report : tmpList) {
                    String[] item = new String[4];
                    item[0] = report.getVehNoF() + "#" + report.getIsOpen();
                    item[1] = report.getAlarmType();
                    item[2] = report.getTime();
                    item[3] = report.getLatitude() + "\n" + report.getLongitude();
                    data.add(item);
                }

                dataAdapter.notifyDataSetChanged();
                endTime = resData.get(resData.size() - 1).getTime();
                //System.out.println("endTime:" + endTime);
            } else {
                Toast.makeText(AlarmReport.this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_report);
        /* 取得全局变量 */
        glob = (SWApplication) getApplicationContext();
        glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resData = new ArrayList<>();
        data = new ArrayList<>();
        dataAdapter = new SimpleTableDataAdapter(AlarmReport.this, data);
        dataAdapter.setTextSize(12);
        tableView.setDataAdapter(dataAdapter);

        Date date = calendar.getTime();
        endTime = dateFormat.format(date);
        progressDialog = new CustomProgressDialog(AlarmReport.this);
        progressDialog.setMessage(getString(R.string.refreshing));
        progressDialog.show();
        getData(glob.sp.getString("user", ""), glob.sp.getString("psw", ""), endTime);
    }

    private void initView() {
        backBtn = (ImageButton) findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.ignore_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAllRecordStatus(2, glob.sp.getString("user", ""));
            }
        });

        tableView = (TableView) findViewById(R.id.tableView);
        tableView.setColumnCount(4);
        tableView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_IDLE: {
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            if (!isLoading) {
                                progressDialog.setMessage(getString(R.string.refreshing));
                                progressDialog.show();
                                getData(glob.sp.getString("user", ""), glob.sp.getString("psw", ""), endTime);
                                isLoading = true;
                            }
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        SimpleTableHeaderAdapter headerAdapter = new SimpleTableHeaderAdapter(this, getResources().getStringArray(R.array.alert_message_table_title));
        headerAdapter.setTextSize(14);
        tableView.setHeaderAdapter(headerAdapter);
        tableView.addDataClickListener(tableDataClickListener);
    }

    private void getData(final String username, final String password, final String endTime) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                response = HttpRequestClient.getAlarmList(username, password, endTime);
                Message msg = new Message();
                if (response.getCode() == SProtocol.SUCCESS) {
                    msg.what = 0;
                } else {
                    msg.what = 1;
                }
                myHandler.sendMessage(msg);
            }
        }.start();
    }

    private void updateRecordStatus(final int type, final int unReadRecordId) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                HttpRequestClient.updateUnReadRecordStatus(type, unReadRecordId);
            }
        }.start();
    }

    private void updateAllRecordStatus(final int type, final String userName) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                HttpRequestClient.UpdateAllUnReadRecordStatus(type, userName);
            }
        }.start();
    }
}
