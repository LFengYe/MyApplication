package com.cn.carigps.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cn.carigps.R;
import com.cn.carigps.entity.Alarm;
import com.cn.carigps.entity.SResponse;
import com.cn.carigps.util.HttpRequestClient;
import com.cn.carigps.util.SProtocol;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;

/**
 * Created by fuyzh on 16/8/2.
 */
public class AlarmReportPull extends Activity {
    MyApplication glob;// 全局控制类
    private ImageButton backBtn;// 返回
    private TableView tableView;
    private List<String[]> data;
    private SResponse response;

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                List<Alarm> resData = (List<Alarm>) response.getResult();
                data = new ArrayList<>();
                for (Alarm report: resData) {
                    String[] item = new String[4];
                    item[0] = report.getVehNoF();
                    item[1] = report.getAlarmType();
                    item[2] = report.getTime();
                    item[3] = report.getLatitude() + "\n" + report.getLongitude();
                    data.add(item);
                }
                SimpleTableDataAdapter dataAdapter = new SimpleTableDataAdapter(AlarmReportPull.this, data);
                dataAdapter.setTextSize(12);
                tableView.setDataAdapter(dataAdapter);
            } else {
                Toast.makeText(AlarmReportPull.this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_report_pull);
		/* 取得全局变量 */
        glob = (MyApplication) getApplicationContext();
        glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        initView();
    }

    private void initView() {
        backBtn = (ImageButton) findViewById(R.id.backButton);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getData(glob.sp.getString("user", ""), glob.sp.getString("psw", ""), "");
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
}
