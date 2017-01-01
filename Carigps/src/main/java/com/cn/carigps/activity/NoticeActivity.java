package com.cn.carigps.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cn.carigps.R;
import com.cn.carigps.entity.Notice;
import com.cn.carigps.entity.SResponse;
import com.cn.carigps.util.HttpRequestClient;
import com.cn.carigps.util.SProtocol;
import com.cn.carigps.widgets.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LFeng on 16/8/2.
 */
public class NoticeActivity extends Activity {

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

    private void initView() {
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
            }
        });

        progressDialog = new CustomProgressDialog(this);
        progressDialog.setMessage(getString(R.string.refreshing));
        progressDialog.show();
        getData();
    }

    private void getData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                response = HttpRequestClient.offcialNews();
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
}
