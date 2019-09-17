package com.cn.wms_system_new.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.wms_system_new.R;
import com.cn.wms_system_new.bean.FunctionItem;
import com.cn.wms_system_new.bean.SResponse;
import com.cn.wms_system_new.component.GetNowTime;
import com.cn.wms_system_new.component.TitleViewHolder;
import com.cn.wms_system_new.fragment.FunPicAdapter;
import com.cn.wms_system_new.service.MyHandler;
import com.cn.wms_system_new.service.MyThread;

import java.util.ArrayList;

/**
 * Created by LFeng on 2017/10/3.
 */

public class GridActivity extends Activity {

    private TitleViewHolder titleHolder;

    private GridView gridView;
    private FunPicAdapter adapter;
    private ArrayList<FunctionItem> itemList;

    private Bundle bundle;

    //region 各个点击事件
    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back: {
                    finish();
                    break;
                }
                case R.id.refresh: {
                    getGridList();
                    break;
                }
            }
        }
    };
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_grid);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.layout_title_bar);

        initParams();
        initComponents();
        setComponents();
    }

    /**
     * 初始化UI组件
     */
    public void initComponents() {
        titleHolder = new TitleViewHolder();
        titleHolder.backButton = (LinearLayout) findViewById(R.id.back);
        titleHolder.curSystemTime = (TextView) findViewById(R.id.cur_sys_time);
        titleHolder.titleTextView = (TextView) findViewById(R.id.title);
        titleHolder.refreshButton = (LinearLayout) findViewById(R.id.refresh);

        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setNumColumns(3);
    }

    /**
     * 设置UI组件属性
     */
    public void setComponents() {
        titleHolder.backButton.setOnClickListener(onClickListener);
        titleHolder.refreshButton.setOnClickListener(onClickListener);
        titleHolder.curSystemTime.setText(getResources().getString(
                R.string.cur_time_promote)
                + GetNowTime.getHour()
                + ":"
                + ((GetNowTime.getMinute() < 10) ? ("0" + GetNowTime
                .getMinute()) : GetNowTime.getMinute()));
        titleHolder.titleTextView.setText(bundle.getString("module"));

        itemList = new ArrayList<>();
        adapter = new FunPicAdapter(this, itemList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FunctionItem item = itemList.get(i);
                Intent intent = new Intent();
                Bundle itemBundle = new Bundle();
                itemBundle.putString("funName", bundle.getString("funName", ""));
                itemBundle.putString("module", bundle.getString("module", ""));
                itemBundle.putString("title", item.getTitle());
                itemBundle.putString("action", item.getActionName());
                intent.putExtras(itemBundle);
                intent.setClass(GridActivity.this, ListDataActivity.class);
                startActivity(intent);
            }
        });

        getGridList();
    }

    /**
     * 初始化参数
     */
    public void initParams() {
        bundle = getIntent().getExtras();
    }


    private void getGridList() {
        JSONObject object = new JSONObject();
        object.put("module", bundle.getString("module", ""));
        object.put("operation", "getList");
        object.put("type", "app");
        new MyThread(dataHandler, object, bundle.getString("action")).start();
    }

    private MyHandler dataHandler = new MyHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyThread.RESPONSE_SUCCESS:
                    SResponse response = (SResponse) msg.obj;
                    if (response.getStatus() == 0) {
                        itemList.clear();
                        itemList.addAll(JSONArray.parseArray(response.getData(), FunctionItem.class));
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(GridActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
}
