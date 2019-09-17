package com.cn.wetrack.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cn.wetrack.R;
import com.cn.wetrack.entity.Notice;
import com.cn.wetrack.entity.SResponse;
import com.cn.wetrack.util.HttpRequestClient;
import com.cn.wetrack.util.SProtocol;
import com.cn.wetrack.widgets.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LFeng on 16/8/2.
 */
public class NoticeActivity extends Activity {

    SWApplication glob;// 全局控制类

    private ListView listView;
    private CustomProgressDialog progressDialog = null;// 进度
    private SResponse response;
    private List<Notice> data;
    private NoticeAdapter adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            if (msg.what == 0) {
                data = (List<Notice>) response.getResult();
                adapter.setData(data);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(NoticeActivity.this, R.string.refreshing_failed, Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog = new CustomProgressDialog(this);
        progressDialog.setMessage(getString(R.string.refreshing));
        progressDialog.show();
        getData();
    }

    private void initView() {
        glob = (SWApplication) getApplicationContext();
        glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.ignore_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAllRecordStatus(1, glob.sp.getString("user", ""));
            }
        });
        listView = (ListView) findViewById(R.id.notice_list);
        data = new ArrayList<>();
        adapter = new NoticeAdapter(this, data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notice notice = data.get(position);
                Intent intent = new Intent();
                intent.putExtra("noticeTitle", notice.getNoticeTitle());
                intent.putExtra("noticeContent", notice.getNoticeContent());
                intent.setClass(NoticeActivity.this, NoticeDetail.class);
                startActivity(intent);

                updateRecordStatus(1, notice.getId());
            }
        });
    }

    private void getData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                response = HttpRequestClient.offcialNews(glob.sp.getString("user", ""), glob.sp.getString("psw", ""));
                Message msg = new Message();
                if (response.getCode() == SProtocol.SUCCESS) {
                    msg.what = 0;
                } else {
                    msg.what = 1;
                }
                handler.sendMessage(msg);
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
